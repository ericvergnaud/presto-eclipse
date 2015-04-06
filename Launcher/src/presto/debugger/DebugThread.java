package presto.debugger;

import java.lang.Thread.State;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jface.dialogs.MessageDialog;

import presto.core.Utils.RunType;
import presto.debug.Debugger;
import presto.debug.IDebugEventListener;
import presto.debug.ResumeReason;
import presto.debug.StackFrame;
import presto.debug.SuspendReason;
import presto.error.PrestoError;
import presto.launcher.ContextMap;
import presto.launcher.LaunchContext;
import presto.parser.ISection;
import presto.runtime.Context;
import presto.runtime.IContext;
import presto.runtime.Interpreter;
import presto.utils.ShellUtils;

public class DebugThread extends PlatformObject implements IThread, IDebugEventListener {

	DebugTarget target;
	LaunchContext context;
	ContextMap map;
	IBreakpoint[] breakpoints;
	Thread prestoThread;
	Debugger debugger;
	
	public DebugThread(DebugTarget target) {
		this.target = target;
		this.context = target.getContext();
		this.debugger = new Debugger();
		this.debugger.setListener(this);
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
		return debugger.canResume();
	}

	@Override
	public boolean canSuspend() {
		return debugger.canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return debugger.isSuspended();
	}

	@Override
	public void resume() throws DebugException {
		debugger.resume();
	}
	
	@Override
	public void suspend() throws DebugException {
		debugger.suspend();
	}

	@Override
	public boolean canStepInto() {
		return debugger.canStepInto();
	}

	@Override
	public boolean canStepOver() {
		return debugger.canStepOver();
	}

	@Override
	public boolean canStepReturn() {
		return debugger.canStepOut();
	}

	@Override
	public boolean isStepping() {
		return debugger.isStepping();
	}

	@Override
	public void stepInto() throws DebugException {
		breakpoints = null;
		debugger.stepInto();
	}

	@Override
	public void stepOver() throws DebugException {
		breakpoints = null;
		debugger.stepOver();
	}

	@Override
	public void stepReturn() throws DebugException {
		breakpoints = null;
		debugger.stepOut();
	}

	@Override
	public void handleResumeEvent(ResumeReason reason, IContext context, ISection section) {
		breakpoints = null;
		DebuggerUtils.fireResumeEvent(this, debugEventFromResumeReason(reason));
	}
	
	private int debugEventFromResumeReason(ResumeReason reason) {
		switch(reason) {
		case RESUMED:
			return DebugEvent.CLIENT_REQUEST;
		case STEP_INTO:
			return DebugEvent.STEP_INTO;
		case STEP_OVER:
			return DebugEvent.STEP_OVER;
		case STEP_OUT:
			return DebugEvent.STEP_RETURN;
		default:
			return 0;
		}
	}

	@Override
	public void handleSuspendEvent(SuspendReason reason, IContext context, ISection section) {
		DebuggerUtils.fireSuspendEvent(this, debugEventFromSuspendReason(reason));
	}
	
	private int debugEventFromSuspendReason(SuspendReason reason) {
		switch(reason) {
		case STEPPING:
			return DebugEvent.STEP_END;
		case BREAKPOINT :
			return DebugEvent.BREAKPOINT;
		case SUSPENDED:
			return DebugEvent.CLIENT_REQUEST;
		default:
			return 0;
		}
	}

	@Override
	public void handleTerminateEvent() {
		prestoThread = null;
		DebuggerUtils.fireTerminateEvent(this);
		DebuggerUtils.stopListening(target);
		DebuggerUtils.fireTerminateEvent(target);
	}
	
	@Override
	public boolean canTerminate() {
		return !debugger.isTerminated();
	}

	@Override
	public boolean isTerminated() {
		return debugger.isTerminated();
	}


	@Override
	public void terminate() throws DebugException {
		if(prestoThread==null)
			return;
		Thread t = prestoThread;
		prestoThread = null;
		debugger.terminate();
		if(isSuspended())
			resume();
		try {
			t.join();
		} catch(InterruptedException e) {
			
		}
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
	public IStackFrame getTopStackFrame() throws DebugException {
		IStackFrame[] stackFrames = getStackFrames();
		if(stackFrames.length>0)
			return stackFrames[0];
		else
			return null;
	}

	@Override
	public IStackFrame[] getStackFrames() throws DebugException {
		IStackFrame[] stackFrames = new IStackFrame[debugger.getStack().size()];
		int i = 0;
		for(StackFrame frame : debugger.getStack())
			stackFrames[i++] = new StackFrameProxy(this, frame);
		return stackFrames;
	}

	@Override
	public String getName() {
		StringBuilder sb = new StringBuilder();
		if(isTerminated())
			sb.append("<terminated>");
		sb.append("Thread [");
		sb.append(context.getMethod().getName());
		sb.append(']');
		if(!isTerminated()) {
			sb.append(" (");
			sb.append(getStatusString());
			sb.append(')');
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public IBreakpoint[] getBreakpoints() {
		return breakpoints;
	}

	public void start() {
		try {
			map = context.buildContextMap(RunType.SCRIPT);
			connectBreakpoints();
			startThread();
		} catch (Exception e) {
			debugger.terminated();
			handleTerminateEvent();
			MessageDialog.openError(ShellUtils.getShell(), "Fatal error", e.getMessage());
		} 
	}

	private void startThread() {
		prestoThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				DebuggerUtils.fireCreationEvent(target);
				DebuggerUtils.startListening(target);
				DebuggerUtils.fireCreationEvent(DebugThread.this);
				Context threadContext = map.getContext().newLocalContext();
				threadContext.setDebugger(debugger);
				try {
					Interpreter.interpret(threadContext, context.getMethod().getName(), context.getCmdLineArgs());
				} catch(PrestoError error) {
					// TODO
				} 
			}
		}, context.getConfiguration().getName());
		prestoThread.start();
		if(!context.isStopInMain()) {
			while(prestoThread.getState()!=State.WAITING) try {
				Thread.sleep(1);
			} catch (Exception e) {};
			debugger.resume();
		}
	}

	public String getStatusString() {
		return debugger.getStatus().toString();
	}
	
	public void connectBreakpoints() throws CoreException {
		IBreakpoint[] breakpoints = DebuggerUtils.getBreakpoints();
		for(IBreakpoint breakpoint : breakpoints)
			connectBreakpoint(breakpoint);
	}

	public void connectBreakpoint(IBreakpoint breakpoint) throws CoreException {
		if(breakpoint instanceof LineBreakpoint)
			connectLineBreakpoint((LineBreakpoint)breakpoint);
		else
			throw new RuntimeException("Unsupported");
		
	}

	public void connectLineBreakpoint(LineBreakpoint breakpoint) throws CoreException {
		ISection section = map.findSection(breakpoint.getResource(), breakpoint.getLineNumber());
		if(section==null)
			breakpoint.setEnabled(false);
		else {
			breakpoint.setEnabled(true);
			section.setAsBreakpoint(true);
		}
	}

	public void disconnectBreakpoint(IBreakpoint breakpoint) throws CoreException {
		if(breakpoint instanceof LineBreakpoint)
			disconnectLineBreakpoint((LineBreakpoint)breakpoint);
		else
			throw new RuntimeException("Unsupported");
		
	}

	public void disconnectLineBreakpoint(LineBreakpoint breakpoint) throws CoreException {
		ISection section = map.findSection(breakpoint.getResource(), breakpoint.getLineNumber());
		if(section!=null)
			section.setAsBreakpoint(false);
	}

}
