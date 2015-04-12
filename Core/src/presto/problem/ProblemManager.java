package presto.problem;

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

import presto.core.Utils;
import presto.grammar.DeclarationList;
import presto.parser.Dialect;
import presto.parser.IParser;
import presto.parser.IProblem;
import presto.parser.IProblemListener;
import presto.parser.ProblemCollector;
import presto.runtime.Context;
import presto.store.IEclipseCodeStore;
import presto.store.StoreUtils;

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
	Map<IFile, DeclarationList> declarationsMap = new HashMap<IFile, DeclarationList>();
	
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
		for(IFile file : store.getFiles()) {
			if(editedInput!=null && file.equals(editedFile))
				continue; // already managed
			clearProblemMarkers(file);
		}
		for(IFile file : store.getFiles()) {
			if(editedInput!=null && file.equals(editedFile))
				continue; // already managed
			Collection<IProblem> problems = parseDeclarations(file, null);
			createProblemMarkers(file, problems);
		}
		for(IFile file : store.getFiles()) {
			if(editedInput!=null && file.equals(editedFile))
				continue; // already managed
			Collection<IProblem> problems = registerDeclarations(file);
			createProblemMarkers(file, problems);
		}
		for(IFile file : store.getFiles()) {
			if(editedInput!=null && file.equals(editedFile))
				continue; // already managed
			Collection<IProblem> problems = checkDeclarations(file);
			createProblemMarkers(file, problems);
		}
	}

	private Collection<IProblem> checkDeclarations(IFile inputFile) {
		try {
			IProblemListener listener = new ProblemCollector();
			context.setProblemListener(listener);
			DeclarationList dl = declarationsMap.get(inputFile);
			dl.check(context);
			return listener.getProblems();
		} catch (Exception e) {
			IProblem problem = new InternalProblem(e.getMessage());
			List<IProblem> problems = new ArrayList<IProblem>();
			problems.add(problem);
			return problems;
		} 
	}

	private Collection<IProblem> registerDeclarations(IFile inputFile) {
		try {
			String path = inputFile.getFullPath().toPortableString();
			IProblemListener listener = new ProblemCollector();
			context.setProblemListener(listener);
			context.unregister(path);
			DeclarationList dl = declarationsMap.get(inputFile);
			dl.register(context);
			return listener.getProblems();
		} catch (Exception e) {
			IProblem problem = new InternalProblem(e.getMessage());
			List<IProblem> problems = new ArrayList<IProblem>();
			problems.add(problem);
			return problems;
		} 
	}

	private Collection<IProblem> parseDeclarations(IFile inputFile, InputStream inputStream) {
		Dialect dialect = Utils.getDialect(inputFile);
		IParser parser = dialect.getParserFactory().newParser();
		IProblemListener listener = new ProblemCollector();
		parser.setProblemListener(listener);
		String path = inputFile.getFullPath().toPortableString();
		InputStream input = inputStream;
		try {
			if(input==null)
				input = inputFile.getContents();
			DeclarationList dl = parser.parse(path, input);
			declarationsMap.put(inputFile, dl);
			return listener.getProblems();
		} catch (Exception e) {
			IProblem problem = new InternalProblem(e.getMessage());
			List<IProblem> problems = new ArrayList<IProblem>();
			problems.add(problem);
			return problems;
		} finally {
			if(inputStream==null) try {
				input.close();
			} catch(IOException e) {
				e.printStackTrace(System.err);
			}
		}
	}

	private void manageLatestInput() throws CoreException {
		if(editedInput!=null)
			manageProblems(editedFile, editedInput);
	}

	private void manageProblems(IFile file, InputStream input) throws CoreException {
		try {
			clearProblemMarkers(file);
			Collection<IProblem> problems = parseDeclarations(file, input);
			createProblemMarkers(file, problems);
			problems = registerDeclarations(file);
			createProblemMarkers(file, problems);
			problems = checkDeclarations(file);
			createProblemMarkers(file, problems);
		} catch (CoreException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(IStatus.ERROR, file.getFullPath(), e.getMessage(), e);
		}
	}
	
	private void clearProblemMarkers(IFile file) throws CoreException {
		for(IMarker marker : file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
			if(!marker.exists())
				continue;
			// System.out.println("Removing " + marker.toString());
			marker.delete();
		}
		
	}

	private void createProblemMarkers(IFile file, Collection<IProblem> problems) throws CoreException {
		for(IProblem problem : problems) {
			if(problemMarkerAlreadyExists(file, problem))
				continue;
			// no marker found, create one
			createProblemMarker(file, problem);
		}
	}

	private void createProblemMarker(IFile file, IProblem problem) throws CoreException {
		System.out.println("char start:" + problem.getStartIndex());
		IMarker marker = file.createMarker("presto.problem.marker");
		marker.setAttribute(IMarker.SEVERITY, problem.getType().ordinal());
		marker.setAttribute(IMarker.CHAR_START, problem.getStartIndex());
		marker.setAttribute(IMarker.CHAR_END, problem.getEndIndex());
		marker.setAttribute(IMarker.LINE_NUMBER, problem.getStartLine());
		marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
	}

	private boolean problemMarkerAlreadyExists(IFile file, IProblem problem) throws CoreException {
		for(IMarker marker : file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
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
