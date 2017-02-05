package prompto.core;

import org.eclipse.core.resources.IFile;

import prompto.declaration.IDeclaration;

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

	public IDeclaration findMethod(IFile file, String signature) {
		return helper.findMethod(file, signature);
	}

}