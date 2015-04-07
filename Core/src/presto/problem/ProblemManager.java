package presto.problem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import presto.utils.ContextUtils;

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
	
	IFile file;
	InputStream input;
	
	private ProblemManager(IFile file, InputStream input) {
		this.file = file;
		this.input = input;
	}

	private void manageProblems() throws CoreException {
		try {
			clearProblemMarkers();
			Collection<IProblem> problems = detectProblems();
			createProblemMarkers(problems);
		} catch (CoreException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(IStatus.ERROR, file.getFullPath(), e.getMessage(), e);
		}
	}
	

	private Collection<IProblem> detectProblems() throws IOException {
		Dialect dialect = Utils.getDialect(file);
		IParser parser = dialect.getParserFactory().newParser();
		IProblemListener listener = new ProblemCollector();
		parser.setProblemListener(listener);
		String path = file.getFullPath().toPortableString();
		InputStream input = this.input;
		try {
			if(input==null)
				input = file.getContents();
			DeclarationList dl = parser.parse(path, input);
			Context context = ContextUtils.fetchContext(file);
			context.setProblemListener(listener);
			context.unregister(path);
			dl.register(context);
			return listener.getProblems();
		} catch (Exception e) {
			IProblem problem = new InternalProblem(e.getMessage());
			List<IProblem> problems = new ArrayList<IProblem>();
			problems.add(problem);
			return problems;
		} finally {
			if(this.input==null)
				input.close();
		}
	}

	private void clearProblemMarkers() throws CoreException {
		for(IMarker marker : file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
			if(!marker.exists())
				continue;
			// System.out.println("Removing " + marker.toString());
			marker.delete();
		}
		
	}

	private void createProblemMarkers(Collection<IProblem> problems) throws CoreException {
		for(IProblem problem : problems) {
			if(problemMarkerAlreadyExists(problem))
				continue;
			createProblemMarker(problem);
		}
	}

	private void createProblemMarker( IProblem problem) throws CoreException {
		// no marker found, create one
		IMarker marker = file.createMarker("presto.problem.marker");
		marker.setAttribute(IMarker.SEVERITY, problem.getType().ordinal());
		marker.setAttribute(IMarker.CHAR_START, problem.getStartIndex());
		marker.setAttribute(IMarker.CHAR_END, problem.getEndIndex());
		marker.setAttribute(IMarker.LINE_NUMBER, problem.getStartLine());
		marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
	}

	private boolean problemMarkerAlreadyExists(IProblem problem) throws CoreException {
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
