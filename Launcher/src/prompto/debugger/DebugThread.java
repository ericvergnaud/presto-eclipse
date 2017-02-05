package prompto.debugger;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;

import prompto.debug.Stack;

public class DebugThread extends PlatformObject implements IThread {
	
	IPromptoDebugTarget target;
	
	public DebugThread(IPromptoDebugTarget target) {
		this.target = target;
	}

	@Override
	public String getModelIdentifier() {
		return target.getModelIdentifier();
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return target;
	}

	@Override
	public ILaunch getLaunch() {
		return target.getLaunch();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (adapter == IDebugElement.class)
			return this;
		else if(adapter==ILaunch.class)
			return getLaunch();
		else
			return super.getAdapter(adapter);
	}

	@Override
	public boolean canResume() {
		return target.canResume(this);
	}

	@Override
	public boolean canSuspend() {
		return target.canSuspend(this);
	}

	@Override
	public boolean isSuspended() {
		return target.isSuspended(this);
	}

	@Override
	public void resume() throws DebugException {
		target.resume(this);
	}
	
	@Override
	public void suspend() throws DebugException {
		target.suspend(this);
	}

	@Override
	public boolean canStepInto() {
		return target.canStepInto(this);
	}

	@Override
	public boolean canStepOver() {
		return target.canStepOver(this);
	}

	@Override
	public boolean canStepReturn() {
		return target.canStepReturn(this);
	}

	@Override
	public boolean isStepping() {
		return target.isStepping(this);
	}

	@Override
	public void stepInto() throws DebugException {
		target.stepInto(this);
	}

	@Override
	public void stepOver() throws DebugException {
		target.stepOver(this);
	}

	@Override
	public void stepReturn() throws DebugException {
		target.stepReturn(this);
	}


	@Override
	public boolean canTerminate() {
		return target.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return target.isTerminated();
	}


	@Override
	public void terminate() throws DebugException {
		target.terminate();
	}

	@Override
	public int getPriority() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStackFrames() throws DebugException {
		return isSuspended();
	}

	@Override
	public StackFrameProxy getTopStackFrame() throws DebugException {
		StackFrameProxy[] stackFrames = getStackFrames();
		if(stackFrames.length>0)
			return stackFrames[0];
		else
			return null;
	}

	@Override
	public StackFrameProxy[] getStackFrames() throws DebugException {
		Stack stack = target.getStack(this);
		return stack.stream()
				.map((f)->new StackFrameProxy(this, f))
				.toArray((l)->new StackFrameProxy[l]);
	}

	@Override
	public String getName() throws DebugException {
		return "<anonymous>";
	}

	@Override
	public IBreakpoint[] getBreakpoints() {
		return DebuggerUtils.getBreakpoints();
	}





}

