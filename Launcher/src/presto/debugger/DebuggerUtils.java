package presto.debugger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;

import presto.core.CoreConstants;
import presto.parser.ISection;

public class DebuggerUtils {

	/**
	 * Fires a debug event.
	 * 
	 * @param event
	 *            debug event to fire
	 */
	public static void fireEvent(DebugEvent event) {
		// System.out.println(event.toString());
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}

	/**
	 * Fires a change event for this debug element with the specified detail
	 * code.
	 * 
	 * @param detail
	 *            detail code for the change event, such as
	 *            <code>DebugEvent.STATE</code> or
	 *            <code>DebugEvent.CONTENT</code>
	 */
	public static void fireChangeEvent(Object source, int detail) {
		fireEvent(new DebugEvent(source, DebugEvent.CHANGE, detail));
	}

	/**
	 * Fires a creation event for this debug element.
	 */
	public static void fireCreationEvent(Object source) {
		fireEvent(new DebugEvent(source, DebugEvent.CREATE));
	}

	/**
	 * Fires a resume for this debug element with the specified detail code.
	 * 
	 * @param detail
	 *            detail code for the resume event, such as
	 *            <code>DebugEvent.STEP_OVER</code>
	 */
	public static void fireResumeEvent(Object source, int detail) {
		fireEvent(new DebugEvent(source, DebugEvent.RESUME, detail));
	}

	/**
	 * Fires a suspend event for this debug element with the specified detail
	 * code.
	 * 
	 * @param detail
	 *            detail code for the suspend event, such as
	 *            <code>DebugEvent.BREAKPOINT</code>
	 */
	public static void fireSuspendEvent(Object source, int detail) {
		fireEvent(new DebugEvent(source, DebugEvent.SUSPEND, detail));
	}

	/**
	 * Fires a terminate event for this debug element.
	 */
	public static void fireTerminateEvent(Object source) {
		fireEvent(new DebugEvent(source, DebugEvent.TERMINATE));
	}

	public static void startListening(IBreakpointListener listener) {
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(listener);
	}

	public static void stopListening(IBreakpointListener listener) {
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(listener);
	}

	public static String getProjectRelativePath(IProject project, ISection section) {
		String path = section.getPath();
		String projectPath = project.getFullPath().toPortableString();
		if (path.startsWith(projectPath))
			path = path.substring(projectPath.length());
		return path;
	}

	public static IMarker createMarker(IProject project, ISection section) throws CoreException {
		String path = getProjectRelativePath(project, section);
		IResource resource = project.findMember(path);
		return createMarker(resource, section);
	}

	public static IMarker createMarker(IResource resource, ISection section) throws CoreException {
		return createMarker(resource, section.getStart().getLine());
	}

	public static IMarker createMarker(IResource resource, int lineNumber) throws CoreException {
		IMarker marker = resource.createMarker(CoreConstants.DEBUG_MARKER_TYPE);
		marker.setAttribute(IBreakpoint.ID, CoreConstants.DEBUG_MODEL_IDENTIFIER);
		marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		marker.setAttribute(IBreakpoint.ENABLED, true);
		marker.setAttribute(IMarker.MESSAGE, "coucou");
		return marker;
	}

	public static IBreakpoint[] getBreakpoints() {
		return DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(CoreConstants.DEBUG_MODEL_IDENTIFIER);
	}

	public static boolean deleteExistingBreakpoint(IBreakpoint breakpoint) throws CoreException {
		IBreakpoint[] breakpoints = getBreakpoints();
		for (int i = 0; i < breakpoints.length; i++) {
			if (breakpoint.equals(breakpoints[i])) {
				breakpoints[i].delete();
				return true;
			}
		}
		return false;
	}

	public static ISchedulingRule getMarkerRule(IResource resource) {
        	IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
        	return ruleFactory.markerRule(resource);
	}

}
