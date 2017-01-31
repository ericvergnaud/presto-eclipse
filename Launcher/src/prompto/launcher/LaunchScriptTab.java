package prompto.launcher;

import prompto.core.RunType;

public class LaunchScriptTab extends LaunchTabBase {

	@Override
	protected RunType getRunType() {
		return RunType.SCRIPT;
	}
	
	@Override
	protected boolean supportsMethodSelector() {
		return false;
	}
	
	@Override
	protected boolean requiresSelectedMethod() {
		return false;
	}
}
