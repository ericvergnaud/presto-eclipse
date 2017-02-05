package prompto.debugger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;

import prompto.debug.IDebugEventListener;
import prompto.debug.Stack;
import prompto.launcher.LaunchContext;

public interface IPromptoDebugTarget extends IDebugTarget, IDebugEventListener {

	void debug(LaunchContext context) throws CoreException;
	boolean isSuspended(DebugThread thread);
	boolean canResume(DebugThread thread);
	boolean canSuspend(DebugThread thread);
	void resume(DebugThread thread) throws DebugException;
	void suspend(DebugThread thread) throws DebugException;
	boolean isStepping(DebugThread thread);
	boolean canStepInto(DebugThread thread);
	boolean canStepOver(DebugThread thread);
	boolean canStepReturn(DebugThread thread);
	void stepInto(DebugThread thread) throws DebugException;
	void stepOver(DebugThread thread) throws DebugException;
	void stepReturn(DebugThread thread) throws DebugException;
	Stack getStack(DebugThread thread) throws DebugException;

}
