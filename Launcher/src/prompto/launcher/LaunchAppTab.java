package prompto.launcher;

import prompto.ide.core.RunType;

public class LaunchAppTab extends LaunchTabBase {

	@Override
	protected RunType getRunType() {
		return RunType.APPLI;
	}
	
	@Override
	protected boolean supportsMethodSelector() {
		return true;
	}
	
	@Override
	protected boolean requiresSelectedMethod() {
		return true;
	}


}
