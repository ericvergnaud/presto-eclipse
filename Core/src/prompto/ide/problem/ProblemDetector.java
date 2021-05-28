package prompto.ide.problem;

import java.io.ByteArrayInputStream;
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
import org.eclipse.jface.text.IDocument;

import prompto.code.ICodeStore;
import prompto.declaration.DeclarationList;
import prompto.ide.code.IEclipseCodeStore;
import prompto.ide.utils.CoreUtils;
import prompto.ide.utils.StoreUtils;
import prompto.parser.Dialect;
import prompto.parser.IParser;
import prompto.problem.IProblem;
import prompto.problem.ProblemCollector;
import prompto.runtime.Context;

@SuppressWarnings("restriction")
public class ProblemDetector {

	public static void fileAdded(IFile file) {
		documentChanged(file, null);
	}

	public static void documentChanged(IFile file, IDocument document) {
		final ProblemDetector pb = new ProblemDetector(file, document);
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
	
	static class PromptoProblemCollector extends ProblemCollector {
		@Override
		public boolean isCheckNative() {
			return false;
		}
	}
	
	IFile editedFile;
	IDocument editedDocument;
	IEclipseCodeStore store;
	Context context;
	Map<String, IFile> pathToFileMap = new HashMap<String, IFile>();
	Map<IFile, DeclarationList> fileToDeclarationMap = new HashMap<IFile, DeclarationList>();
	Map<DeclarationList, IFile> declarationToFileMap = new HashMap<DeclarationList, IFile>();
	
	private ProblemDetector(IFile file, IDocument document) {
		this.editedFile = file;
		this.editedDocument = document;
	}

	private void manageProblems() throws CoreException {
		// need to ensure ICodeStore instance is constant, TODO use TLS
		synchronized(ICodeStore.class) {
			this.store = StoreUtils.setStoreFor(editedFile);
			this.context = store.getProjectContext();
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
			if(editedDocument!=null && file.equals(editedFile))
				continue; // already managed
			Collection<IProblem> problems = checkDeclarations(file);
			createProblemMarkers(file, problems);
		}
	}

	private void parseDeclarations() throws CoreException {
		for(IFile file : store.getFiles()) {
			if(editedDocument!=null && file.equals(editedFile))
				continue; // already managed
			ProblemCollector listener = new PromptoProblemCollector();
			parseDeclarations(file, null, listener);
			createProblemMarkers(file, listener.getProblems());
		}
	}
	

	private void clearProblemMarkers() throws CoreException {
		clearProblemMarkers(ResourcesPlugin.getWorkspace().getRoot());
		for(IFile file : store.getFiles()) {
			if(editedDocument!=null && file.equals(editedFile))
				continue; // already managed
			clearProblemMarkers(file);
		}
	}

	private void unregisterDeclarations() throws CoreException {
		for(IFile file : store.getFiles()) {
			if(editedDocument!=null && file.equals(editedFile))
				continue; // already managed
			unregisterDeclarations(file);
		}
	}
	
	private void unregisterDeclarations(IFile inputFile) throws CoreException {
		ProblemCollector listener = new PromptoProblemCollector();
		context.pushProblemListener(listener);
		unregisterDeclarations(inputFile, listener);
		createProblemMarkers(inputFile, listener.getProblems());
		context.popProblemListener();
	}

	private Collection<IProblem> unregisterDeclarations(IFile inputFile, ProblemCollector listener) {
		try {
			String path = inputFile.getFullPath().toPortableString();
			context.unregister(path);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			listener.getProblems().add(new InternalProblem(e.getMessage()));
		} 
		return listener.getProblems();
	}

	private void registerDeclarations() throws CoreException {
		// need to register all at once, to ensure correct sequence of types
		DeclarationList decls = new DeclarationList();
		for(IFile file : store.getFiles()) {
			if(editedDocument!=null && file.equals(editedFile))
				continue; // already managed
			decls.addAll(fileToDeclarationMap.get(file));
		}
		ProblemCollector listener = new PromptoProblemCollector();
		context.pushProblemListener(listener);
		// register project declarations
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
			if(file!=null) // happens when source is <INTERNAL>
				createProblemMarkers(file, entry.getValue());
		}
	}

	private Collection<IProblem> checkDeclarations(IFile inputFile) {
		ProblemCollector listener = new PromptoProblemCollector();
		checkDeclarations(inputFile, listener);
		return listener.getProblems();
	}


	private void checkDeclarations(IFile inputFile, ProblemCollector listener) {
		context.pushProblemListener(listener);
		try {
			DeclarationList dl = fileToDeclarationMap.get(inputFile);
			if(dl!=null)
				dl.check(context);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			listener.getProblems().add(new InternalProblem(e.getMessage()));
		} 
	}

	private DeclarationList parseDeclarations(IFile inputFile, IDocument document, ProblemCollector listener) throws CoreException {
		Dialect dialect = CoreUtils.getDialect(inputFile);
		IParser parser = dialect.getParserFactory().newParser();
		parser.setProblemListener(listener);
		String path = inputFile.getFullPath().toPortableString();
		pathToFileMap.put(path, inputFile);
		InputStream input = document==null ? inputFile.getContents() : new ByteArrayInputStream(document.get().getBytes());
		try {
			DeclarationList dl = parser.parse(path, input);
			fileToDeclarationMap.put(inputFile, dl);
			declarationToFileMap.put(dl, inputFile);
			return dl;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			listener.getProblems().add(new InternalProblem(e.getMessage()));
		} finally {
			try {
				input.close();
			} catch(IOException e) {
				e.printStackTrace(System.err);
			}
		}
		return null; 
	}

	private void manageLatestInput() throws CoreException {
		if(editedDocument!=null)
			manageProblems(editedFile, editedDocument);
	}

	private void manageProblems(IFile file, IDocument document) throws CoreException {
		try {
			clearProblemMarkers(file);
			ProblemCollector listener = new PromptoProblemCollector();
			parseDeclarations(file, document, listener);
			unregisterDeclarations(file, listener);
			registerDeclarations(file, listener);
			checkDeclarations(file, listener);
			createProblemMarkers(file, listener.getProblems());
		} catch (CoreException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(IStatus.ERROR, file.getFullPath(), e.getMessage(), e);
		}
	}
	
	private void registerDeclarations(IFile inputFile, ProblemCollector listener) throws CoreException {
		context.pushProblemListener(listener);
		try {
			DeclarationList dl = fileToDeclarationMap.get(inputFile);
			if(dl!=null)
				dl.register(context);
		} catch(Exception e) {
			e.printStackTrace(System.err);
			listener.getProblems().add(new InternalProblem(e.getMessage()));
		}
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
