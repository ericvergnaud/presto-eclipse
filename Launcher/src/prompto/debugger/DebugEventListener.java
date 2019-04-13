package prompto.debugger;

import org.eclipse.debug.core.DebugEvent;

import prompto.debug.IDebugEvent;
import prompto.debug.IDebugEventListener;
import prompto.debug.IWorker;
import prompto.debug.ResumeReason;
import prompto.debug.SuspendReason;

public class DebugEventListener implements IDebugEventListener {

	DebugTarget target;
	
	public DebugEventListener(DebugTarget target) {
		this.target = target;
		DebuggerUtils.fireCreationEvent(target);
	}

	public void waitConnected() throws InterruptedException {
		synchronized (target) {
			target.wait();
		}
	}

	@Override
	public void handleConnectedEvent(IDebugEvent.Connected event) {
		target.getDebugger().setRemote(event.getHost(), event.getPort());
		synchronized (target) {
			target.notify();
		}
	}
	
	@Override
	public void handleReadyEvent() {
		// TODO 
	}
	
	
	@Override
	public void handleStartedEvent(IWorker worker) {
		// TODO
	}
	

	@Override
	public void handleSuspendedEvent(IWorker worker, SuspendReason reason) {
		DebuggerUtils.fireSuspendEvent(target.getThread(), debugEventFromSuspendReason(reason));
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
	public void handleResumedEvent(IWorker worker, ResumeReason reason) {
		DebuggerUtils.fireResumeEvent(target.getThread(), debugEventFromResumeReason(reason));
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
	public void handleCompletedEvent(IWorker worker) {
		// TODO 
	}

	@Override
	public void handleTerminatedEvent() {
		target.notifyTerminated();
		DebuggerUtils.fireTerminateEvent(target);
	}


}
