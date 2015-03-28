package presto.debugger;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

import presto.launcher.LaunchContext;

public class DebugTarget extends PlatformObject implements IDebugTarget {

	public static void debug(LaunchContext context) {
		DebugTarget target = new DebugTarget(context);
		target.start();
	}

	LaunchContext context;
	DebugThread debugThread;
	IThread[] threads;
	
	public DebugTarget(LaunchContext context) {
		this.context = context;
		this.debugThread = new DebugThread(this);
		this.threads = new IThread[] { debugThread };
		context.getLaunch().addDebugTarget(this);
	}
	
	void start() {
		debugThread.start();
	}
	
	public LaunchContext getContext() {
		return context;
	}

	@Override
	public String getModelIdentifier() {
		return Constants.MODEL_IDENTIFIER;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return this;
	}

	@Override
	public ILaunch getLaunch() {
		return context.getLaunch();
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
	public boolean canTerminate() {
		return debugThread.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return debugThread.isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		debugThread.terminate();
	}
	
	
	@Override
	public boolean canSuspend() {
		return debugThread.canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return debugThread.isSuspended();
	}

	@Override
	public boolean canResume() {
		return debugThread.canResume();
	}

	@Override
	public void resume() throws DebugException {
		debugThread.resume();
	}

	@Override
	public void suspend() throws DebugException {
		debugThread.suspend();
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		try {
			debugThread.connectBreakpoint(breakpoint);
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		try {
			debugThread.disconnectBreakpoint(breakpoint);
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canDisconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void disconnect() throws DebugException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDisconnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcess getProcess() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IThread[] getThreads() throws DebugException {
		return threads;
	}

	@Override
	public boolean hasThreads() throws DebugException {
		return !isTerminated();
	}

	@Override
	public String getName() {
		StringBuilder sb = new StringBuilder();
		if(isTerminated())
			sb.append("<terminated>");
		sb.append("Interpreter");
		if(!isTerminated()) {
			sb.append(" (");
			sb.append(debugThread.getStatusString());
			sb.append(')');
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		// TODO Auto-generated method stub
		return false;
	}


}
