package prompto.debugger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

import prompto.code.IEclipseCodeStore;
import prompto.core.CoreConstants;
import prompto.debug.DebugRequestClient;
import prompto.debug.IDebugger;
import prompto.debug.IStack;
import prompto.debug.ResumeReason;
import prompto.debug.SuspendReason;
import prompto.launcher.ILaunchHelper;
import prompto.launcher.LaunchContext;
import prompto.parser.ISection;
import prompto.runner.Runner;

public class Debugger extends PlatformObject implements IPromptoDebugTarget  {
	
	public static void run(LaunchContext context) throws CoreException {
		Debugger debugger = new Debugger();
		debugger.debug(context);
	}

	LaunchContext context;
	ProcessBuilder builder;
	IProcess process;
	IDebugger debugger;
	
	@Override
	public void debug(LaunchContext context) throws CoreException {
		this.context = context;
		try {
			String[] commands = buildCommands(context);
			ProcessBuilder builder = new ProcessBuilder(commands)
				.directory(new File(context.getDistribution().getDirectory()))
				.inheritIO();
			Process remote = builder.start();
			debugger = new DebugRequestClient(remote, "localhost", 9999, this);
			process = DebugPlugin.newProcess(context.getLaunch(), remote, getName());
			DebuggerUtils.fireCreationEvent(this);
			DebuggerUtils.startListening(this);
			context.getLaunch().addDebugTarget(this);
			debugger.connect();
			connectBreakpoints();
			if(!context.isStopInMain())
				debugger.resume();
		} catch(IOException e) {
			e.printStackTrace(System.err);
			throw new CoreException(Status.CANCEL_STATUS);
		}
	}

	private String[] buildCommands(LaunchContext context) throws CoreException {
		ILaunchHelper helper = context.getLaunchHelper();
		List<String> commands = new ArrayList<>();
		commands.add("java");
		commands.add("-jar");
		commands.add(helper.getTargetJar(context));
		commands.add("-debug_port");
		commands.add("9999");
		commands.addAll(helper.getTargetSpecifiers(context));
		commands.add("-resources");
		commands.add(Runner.getResourcesAsString(context));
		Collection<String> args = Runner.getCommandLineArgs(context);
		if(args!=null && !args.isEmpty())
			commands.addAll(args);
		return commands.toArray(new String[commands.size()]);
	}

	@Override
	public String getModelIdentifier() {
		return CoreConstants.DEBUG_MODEL_IDENTIFIER;
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
		return debugger.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return debugger.isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		debugger.terminate();
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
	public boolean canResume() {
		return debugger.canResume();
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
	public void breakpointAdded(IBreakpoint breakpoint) {
		try {
			connectBreakpoint(breakpoint);
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
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
		IEclipseCodeStore store = context.getCodeStore();
		ISection section = store.findSection(breakpoint.getResource(), breakpoint.getLineNumber());
		breakpoint.setEnabled(section!=null);
		section.setAsBreakpoint(breakpoint.isEnabled());
	}


	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		try {
			disconnectBreakpoint(breakpoint);
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
	}

	public void disconnectBreakpoint(IBreakpoint breakpoint) throws CoreException {
		if(breakpoint instanceof LineBreakpoint)
			disconnectLineBreakpoint((LineBreakpoint)breakpoint);
		else
			throw new RuntimeException("Unsupported");
		
	}

	public void disconnectLineBreakpoint(LineBreakpoint breakpoint) throws CoreException {
		IEclipseCodeStore store = context.getCodeStore();
		ISection section = store.findSection(breakpoint.getResource(), breakpoint.getLineNumber());
		if(section!=null)
			section.setAsBreakpoint(false);
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canDisconnect() {
		return process!=null;
	}

	@Override
	public void disconnect() throws DebugException {
		process = null;
	}

	@Override
	public boolean isDisconnected() {
		// TODO Auto-generated method stub
		return process==null;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return false;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcess getProcess() {
		return process;
	}

	@Override
	public IThread[] getThreads() throws DebugException {
		return new IThread[0];
	}

	@Override
	public boolean hasThreads() throws DebugException {
		return !isTerminated();
	}

	@Override
	public String getName() {
		ILaunchHelper helper = context.getLaunchHelper();
		StringBuilder sb = new StringBuilder();
		if(isTerminated())
			sb.append("<terminated>");
		sb.append(context.getConfiguration().getName());
		sb.append(" [");
		sb.append(helper.getProcessName());
		sb.append("]");
		if(!isTerminated()) {
			sb.append(" <");
			sb.append(debugger.getStatus().toString());
			sb.append('>');
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
	
	@Override
	public void handleResumedEvent(ResumeReason reason) {
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
	public void handleSuspendedEvent(SuspendReason reason) {
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
	public void handleTerminatedEvent() {
		DebuggerUtils.fireTerminateEvent(this);
		DebuggerUtils.stopListening(this);
	}

	@Override
	public boolean isStepping(DebugThread thread) {
		return debugger.isStepping();
	}

	@Override
	public boolean canStepInto(DebugThread thread) {
		return debugger.canStepInto();
	}
	
	@Override
	public void stepInto(DebugThread thread) throws DebugException {
		debugger.stepInto();
	}
	
	@Override
	public boolean canStepOver(DebugThread thread) {
		return debugger.canStepOver();
	}
	
	@Override
	public void stepOver(DebugThread thread) throws DebugException {
		debugger.stepOver();
	}
	
	@Override
	public boolean canStepReturn(DebugThread thread) {
		return debugger.canStepOut();
	}
	
	@Override
	public void stepReturn(DebugThread thread) throws DebugException {
		debugger.stepOut();
	}
	
	
	@Override
	public boolean isSuspended(DebugThread thread) {
		return isSuspended();
	}
	
	@Override
	public boolean canResume(DebugThread thread) {
		return canResume();
	}
	
	@Override
	public boolean canSuspend(DebugThread thread) {
		return canSuspend();
	}
	
	@Override
	public void suspend(DebugThread thread) throws DebugException {
		suspend();
	}
	
	@Override
	public void resume(DebugThread thread) throws DebugException {
		resume();
	}
	
	@Override
	public IStack<?> getStack(DebugThread thread) throws DebugException {
		return debugger.getStack();
	}
	
}
