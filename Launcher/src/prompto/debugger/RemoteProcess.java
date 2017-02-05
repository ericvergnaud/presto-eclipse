package prompto.debugger;

import org.eclipse.debug.core.model.IProcess;

import prompto.debug.IRemote;

public class RemoteProcess implements IRemote {

	IProcess process;
	
	public RemoteProcess(IProcess process) {
		this.process = process;
	}
	
	@Override
	public boolean isAlive() {
		return !process.isTerminated();
	}

}
