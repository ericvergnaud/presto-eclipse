package prompto.core;

public enum RunType {
	TEST (new IRunTypeHelper.TestHelper()),
	SERVER (new IRunTypeHelper.ServerHelper()),
	APPLI (new IRunTypeHelper.AppliHelper()),
	SCRIPT (new IRunTypeHelper.ScriptHelper());
	
	IRunTypeHelper helper;
	
	RunType(IRunTypeHelper helper) {
		this.helper = helper;
	}
	
	public String getNature() {
		return helper.getNature();
	}
	
	boolean isSupportedExtension(String ext) {
		return helper.isSupportedExtension(ext);
	}

}