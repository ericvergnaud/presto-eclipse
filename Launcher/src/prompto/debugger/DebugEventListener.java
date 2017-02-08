package prompto.debugger;

import org.eclipse.debug.core.DebugEvent;

import prompto.debug.IDebugEventListener;
import prompto.debug.ResumeReason;
import prompto.debug.SuspendReason;

public class DebugEventListener implements IDebugEventListener {

	DebugTarget target;
	
	public DebugEventListener(DebugTarget target) {
		this.target = target;
		DebuggerUtils.fireCreationEvent(target);
	}

	@Override
	public void handleResumedEvent(ResumeReason reason) {
		DebuggerUtils.fireResumeEvent(target, debugEventFromResumeReason(reason));
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
		DebuggerUtils.fireSuspendEvent(target, debugEventFromSuspendReason(reason));
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
		target.getDebugger().notifyTerminated();
		DebuggerUtils.fireTerminateEvent(target);
	}


}
