package prompto.debugger;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;

public class DebugThread extends PlatformObject implements IThread {
	
	IPromptoDebugTarget target;
	prompto.debug.IThread thread;
	
	public DebugThread(IPromptoDebugTarget target, prompto.debug.IThread thread) {
		this.target = target;
		this.thread = thread;
	}
	
	public IPromptoDebugTarget getTarget() {
		return target;
	}
	
	@Override
	public String toString() {
		return "Prompto main";
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
	public IBreakpoint[] getBreakpoints() {
		return DebuggerUtils.getBreakpoints();
	}

	@Override
	public boolean canResume() {
		return target.getDebugger().canResume(thread);
	}

	@Override
	public void resume() throws DebugException {
		target.getDebugger().resume(thread);
	}
	
	@Override
	public boolean canSuspend() {
		return target.getDebugger().canSuspend(thread);
	}

	@Override
	public boolean isSuspended() {
		return target.getDebugger().isSuspended(thread);
	}

	@Override
	public void suspend() throws DebugException {
		target.getDebugger().suspend(thread);
	}

	@Override
	public boolean canStepInto() {
		return target.getDebugger().canStepInto(thread);
	}

	@Override
	public boolean canStepOver() {
		return target.getDebugger().canStepOver(thread);
	}

	@Override
	public boolean canStepReturn() {
		return target.getDebugger().canStepOut(thread);
	}

	@Override
	public boolean isStepping() {
		return target.getDebugger().isStepping(thread);
	}

	@Override
	public void stepInto() throws DebugException {
		target.getDebugger().stepInto(thread);
	}

	@Override
	public void stepOver() throws DebugException {
		target.getDebugger().stepOver(thread);
	}

	@Override
	public void stepReturn() throws DebugException {
		target.getDebugger().stepOut(thread);
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
		if(target.isTerminated())
			return new StackFrameProxy[0];
		List<StackFrameProxy> frames = target.getDebugger().getStack(thread).stream()
				.map((f)->new StackFrameProxy(this, f))
				.collect(Collectors.toList());
		return frames.toArray(new StackFrameProxy[frames.size()]);
	}

	@Override
	public String getName() throws DebugException {
		return "<anonymous>";
	}


}

