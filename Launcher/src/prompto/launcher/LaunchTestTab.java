package prompto.launcher;

import prompto.core.Utils.RunType;

public class LaunchTestTab extends LaunchTabBase {
	
	@Override
	protected RunType getRunType() {
		return RunType.TEST;
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
