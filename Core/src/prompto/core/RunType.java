package prompto.core;

import org.eclipse.core.resources.IFile;

import prompto.declaration.IDeclaration;

public enum RunType {
	TEST (new IRunTypeHelper.Test()),
	SERVER (new IRunTypeHelper.Server()),
	APPLI (new IRunTypeHelper.Application()),
	SCRIPT (new IRunTypeHelper.Script());
	
	IRunTypeHelper helper;
	
	RunType(IRunTypeHelper helper) {
		this.helper = helper;
	}
	
	public String getNature() {
		return helper.getNature();
	}
	
	public boolean isSupportedExtension(String ext) {
		return helper.isSupportedExtension(ext);
	}

	public IDeclaration findMethod(IFile file, String signature) {
		return helper.findMethod(file, signature);
	}

}