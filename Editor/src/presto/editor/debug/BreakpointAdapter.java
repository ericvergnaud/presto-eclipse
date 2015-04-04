package presto.editor.debug;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import presto.debugger.DebuggerUtils;
import presto.debugger.LineBreakpoint;
import presto.editor.base.SourceEditorBase;

public class BreakpointAdapter implements IToggleBreakpointsTarget {

	@Override
	public boolean canToggleLineBreakpoints(IWorkbenchPart part, ISelection selection) {
		return getEditor(part)!=null;
	}
	
	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		SourceEditorBase textEditor = getEditor(part);
		if (textEditor != null) {
			final IResource resource = (IResource) textEditor.getEditorInput().getAdapter(IResource.class);
			ITextSelection textSelection = (ITextSelection) selection;
			final int lineNumber = textSelection.getStartLine();
			// the breakpoint needs to be created in a wr to be displayed in the editor
			IWorkspaceRunnable wr = new IWorkspaceRunnable() {
				
				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					IBreakpoint breakpoint = new LineBreakpoint(resource, lineNumber + 1);
					if(!DebuggerUtils.deleteExistingBreakpoint(breakpoint))
						DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
				}
			};
			ISchedulingRule rule = DebuggerUtils.getMarkerRule(resource);
			ResourcesPlugin.getWorkspace().run(wr, rule, 0, null);
		}
	}
	
	@Override
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}

	@Override
	public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}
	
	@Override
	public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}
	
	@Override
	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}
	
	private SourceEditorBase getEditor(IWorkbenchPart part) {
		if (part instanceof SourceEditorBase)
			return (SourceEditorBase) part;
		else
			return null;
	}

}
