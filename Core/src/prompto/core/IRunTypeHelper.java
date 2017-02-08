package prompto.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;

import prompto.declaration.IDeclaration;
import prompto.declaration.IMethodDeclaration;
import prompto.declaration.TestMethodDeclaration;
import prompto.parser.Dialect;
import prompto.utils.CoreUtils;

public interface IRunTypeHelper {

	String getNature();
	boolean isSupportedExtension(String ext);
	IDeclaration findMethod(IFile file, String signature);
	
	static Set<String> codeExtensions = new HashSet<>(Arrays.asList("pec", "poc", "pmc"));
	static Set<String> scriptExtensions = new HashSet<>(Arrays.asList("pes", "pos", "pms"));

	static class Test implements IRunTypeHelper {

		@Override
		public String getNature() {
			return null;
		}

		@Override
		public boolean isSupportedExtension(String ext) {
			return codeExtensions.contains(ext.toLowerCase()) || scriptExtensions.contains(ext.toLowerCase());
		}
		
		@Override
		public IDeclaration findMethod(IFile file, String signature) {
			return findTestMethod(file, signature);
		}

	}

	static class Application implements IRunTypeHelper {

		@Override
		public String getNature() {
			return CoreConstants.APPLICATION_NATURE_ID;
		}

		@Override
		public boolean isSupportedExtension(String ext) {
			return codeExtensions.contains(ext.toLowerCase());
		}
		
		@Override
		public IDeclaration findMethod(IFile file, String signature) {
			return findRegularMethod(file, signature);
		}
		
	}

	static class Server implements IRunTypeHelper {

		@Override
		public String getNature() {
			return CoreConstants.SERVER_NATURE_ID;
		}

		@Override
		public boolean isSupportedExtension(String ext) {
			return codeExtensions.contains(ext.toLowerCase());
		}

		@Override
		public IDeclaration findMethod(IFile file, String signature) {
			return findRegularMethod(file, signature);
		}
	}

	static class Script implements IRunTypeHelper {

		@Override
		public String getNature() {
			return CoreConstants.SCRIPTS_NATURE_ID;
		}

		@Override
		public boolean isSupportedExtension(String ext) {
			return scriptExtensions.contains(ext.toLowerCase());
		}

		@Override
		public IDeclaration findMethod(IFile file, String signature) {
			throw new UnsupportedOperationException("Should never get there!");
		}
	}


	static IMethodDeclaration findRegularMethod(IFile file, String signature) {
		Dialect dialect = CoreUtils.getDialect(file);
		for(IMethodDeclaration method : CoreUtils.getEligibleMainMethods(file)) {
			if(signature.equals(method.getSignature(dialect))) 
				return method;
		}
		return null;
	}
	
	static TestMethodDeclaration findTestMethod(IFile file, String signature) {
		for(TestMethodDeclaration method : CoreUtils.getEligibleTestMethods(file)) {
			if(signature.equals(method.getName().toString()))
				return method;
		}
		return null;
	}



}
