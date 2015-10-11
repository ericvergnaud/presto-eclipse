package prompto.problem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import prompto.core.Utils;
import prompto.declaration.DeclarationList;
import prompto.parser.Dialect;
import prompto.parser.IParser;
import prompto.runtime.Context;
import prompto.store.IEclipseCodeStore;
import prompto.store.StoreUtils;

@SuppressWarnings("restriction")
public class ProblemManager {

	public static void processFile(IFile file) {
		processFile(file, null);
	}

	public static void processFile(IFile file, InputStream input) {
		final ProblemManager pb = new ProblemManager(file, input);
		try { 
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					pb.manageProblems();
				}
			}, null);
		} catch (CoreException e) {
			// TODO, but what?
			e.printStackTrace(System.err);
		}
	}
	
	IFile editedFile;
	InputStream editedInput;
	IEclipseCodeStore store;
	Context context;
	Map<String, IFile> pathToFileMap = new HashMap<String, IFile>();
	Map<IFile, DeclarationList> fileToDeclarationMap = new HashMap<IFile, DeclarationList>();
	Map<DeclarationList, IFile> declarationToFileMap = new HashMap<DeclarationList, IFile>();
	
	private ProblemManager(IFile file, InputStream input) {
		this.editedFile = file;
		this.editedInput = input;
	}

	private void manageProblems() throws CoreException {
		store = StoreUtils.fetchStoreFor(editedFile);
		context = store.getContext();
		synchronized(context) {
			if(!initialized()) {
				// need to register libraries before checking project files
				manageImpact();
				manageLatestInput();
			} else {
				// manage edited file first to provide quick feedback
				manageLatestInput();
				manageImpact();
			}
		}
	}
	
	private boolean initialized() throws CoreException {
		return !context.isEmpty();
	}

	private void manageImpact() throws CoreException {
		clearProblemMarkers();
		parseDeclarations();
		unregisterDeclarations();
		registerDeclarations();
		checkDeclarations();
	}

	private void checkDeclarations() throws CoreException {
		for(IFile file : store.getFiles()) {
			if(editedInput!=null && file.equals(editedFile))
				continue; // already managed
			Collection<IProblem> problems = checkDeclarations(file);
			createProblemMarkers(file, problems);
		}
	}

	private void parseDeclarations() throws CoreException {
		for(IFile file : store.getFiles()) {
			if(editedInput!=null && file.equals(editedFile))
				continue; // already managed
			ProblemCollector listener = new ProblemCollector();
			DeclarationList dl = parseDeclarations(file, null, listener);
			fileToDeclarationMap.put(file, dl);
			declarationToFileMap.put(dl, file);
			createProblemMarkers(file, listener.getProblems());
		}
	}
	

	private void clearProblemMarkers() throws CoreException {
		clearProblemMarkers(ResourcesPlugin.getWorkspace().getRoot());
		for(IFile file : store.getFiles()) {
			if(editedInput!=null && file.equals(editedFile))
				continue; // already managed
			clearProblemMarkers(file);
		}
	}

	private void unregisterDeclarations() throws CoreException {
		for(IFile file : store.getFiles()) {
			if(editedInput!=null && file.equals(editedFile))
				continue; // already managed
			unregisterDeclarations(file);
		}
	}
	
	private void unregisterDeclarations(IFile inputFile) throws CoreException {
		ProblemCollector listener = new ProblemCollector();
		context.setProblemListener(listener);
		try {
			String path = inputFile.getFullPath().toPortableString();
			context.unregister(path);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			listener.getProblems().add(new InternalProblem(e.getMessage()));
		} 
		createProblemMarkers(inputFile, listener.getProblems());
	}

	private void registerDeclarations() throws CoreException {
		// need to register all at once, to ensure correct sequence of types
		DeclarationList decls = new DeclarationList();
		for(IFile file : store.getFiles()) {
			if(editedInput!=null && file.equals(editedFile))
				continue; // already managed
			decls.addAll(fileToDeclarationMap.get(file));
		}
		ProblemCollector listener = new ProblemCollector();
		context.setProblemListener(listener);
		try {
			decls.register(context);
		} catch(Exception e) {
			e.printStackTrace(System.err);
			listener.getProblems().add(new InternalProblem(e.getMessage()));
		}
		// collect impacted files
		Map<String, List<IProblem>> problemsMap = new HashMap<>();
		for(IProblem problem : listener.getProblems()) {
			List<IProblem> list = problemsMap.get(problem.getPath());
			if(list==null) {
				list = new ArrayList<>();
				problemsMap.put(problem.getPath(), list);
			}
			list.add(problem);
		}
		for(Map.Entry<String,List<IProblem>> entry : problemsMap.entrySet()) {
			IFile file = pathToFileMap.get(entry.getKey());
			createProblemMarkers(file, entry.getValue());
		}
	}

	private Collection<IProblem> checkDeclarations(IFile inputFile) {
		ProblemCollector listener = new ProblemCollector();
		context.setProblemListener(listener);
		try {
			DeclarationList dl = fileToDeclarationMap.get(inputFile);
			if(dl!=null)
				dl.check(context);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			listener.getProblems().add(new InternalProblem(e.getMessage()));
		} 
		return listener.getProblems();
	}


	private DeclarationList parseDeclarations(IFile inputFile, InputStream inputStream, ProblemCollector listener) {
		Dialect dialect = Utils.getDialect(inputFile);
		IParser parser = dialect.getParserFactory().newParser();
		parser.setProblemListener(listener);
		String path = inputFile.getFullPath().toPortableString();
		pathToFileMap.put(path, inputFile);
		InputStream input = inputStream;
		try {
			if(input==null)
				input = inputFile.getContents();
			return parser.parse(path, input);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			listener.getProblems().add(new InternalProblem(e.getMessage()));
		} finally {
			if(inputStream==null) try {
				input.close();
			} catch(IOException e) {
				e.printStackTrace(System.err);
			}
		}
		return null; 
	}

	private void manageLatestInput() throws CoreException {
		if(editedInput!=null)
			manageProblems(editedFile, editedInput);
	}

	private void manageProblems(IFile file, InputStream input) throws CoreException {
		try {
			clearProblemMarkers(file);
			parseDeclarations(file, input);
			unregisterDeclarations(file);
			registerDeclarations(file);
			checkDeclarations(file);
		} catch (CoreException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(IStatus.ERROR, file.getFullPath(), e.getMessage(), e);
		}
	}
	
	private void registerDeclarations(IFile file) throws CoreException {
		DeclarationList decls = new DeclarationList();
		ProblemCollector listener = new ProblemCollector();
		context.setProblemListener(listener);
		try {
			decls.register(context);
		} catch(Exception e) {
			e.printStackTrace(System.err);
			listener.getProblems().add(new InternalProblem(e.getMessage()));
		}
		createProblemMarkers(file, listener.getProblems());
	}

	private void parseDeclarations(IFile file, InputStream input) throws CoreException {
		ProblemCollector listener = new ProblemCollector();
		parseDeclarations(file, input, listener);
		createProblemMarkers(file, listener.getProblems());
	}

	private void clearProblemMarkers(IResource resource) throws CoreException {
		for(IMarker marker : resource.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO)) {
			if(marker.exists())
				marker.delete();
		}
		
	}

	private void createProblemMarkers(IResource resource, Collection<IProblem> problems) throws CoreException {
		for(IProblem problem : problems) {
			if(problemMarkerAlreadyExists(resource, problem))
				continue;
			// no marker found, create one
			createProblemMarker(resource, problem);
		}
	}

	private void createProblemMarker(IResource resource, IProblem problem) throws CoreException {
		if(resource==null)
			resource = ResourcesPlugin.getWorkspace().getRoot();
		IMarker marker = resource.createMarker("prompto.problem.marker");
		marker.setAttribute(IMarker.SEVERITY, problem.getType().ordinal());
		marker.setAttribute(IMarker.CHAR_START, problem.getStartIndex());
		marker.setAttribute(IMarker.CHAR_END, problem.getEndIndex());
		marker.setAttribute(IMarker.LINE_NUMBER, problem.getStartLine());
		marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
	}

	private boolean problemMarkerAlreadyExists(IResource resource, IProblem problem) throws CoreException {
		for(IMarker marker : resource.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
			if(!marker.exists())
				continue;
			int start = marker.getAttribute(IMarker.CHAR_START, 0);
			int end = marker.getAttribute(IMarker.CHAR_END, 0);
			String msg = marker.getAttribute(IMarker.MESSAGE, "");
			if(start==problem.getStartIndex()
				&& end==problem.getEndIndex()
				&& msg.equalsIgnoreCase(problem.getMessage()))
				return true; // marker already exists
		}
		return false;
	}


}
