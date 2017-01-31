package prompto.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface IRunTypeHelper {

	String getNature();
	boolean isSupportedExtension(String ext);
	
	static Set<String> codeExtensions = new HashSet<>(Arrays.asList("pec", "poc", "pmc"));
	static Set<String> scriptExtensions = new HashSet<>(Arrays.asList("pes", "pos", "pms"));

	static class TestHelper implements IRunTypeHelper {

		@Override
		public String getNature() {
			return null;
		}

		@Override
		public boolean isSupportedExtension(String ext) {
			return codeExtensions.contains(ext.toLowerCase()) || scriptExtensions.contains(ext.toLowerCase());
		}
		

	}

	static class AppliHelper implements IRunTypeHelper {

		@Override
		public String getNature() {
			return CoreConstants.APPLICATION_NATURE_ID;
		}

		@Override
		public boolean isSupportedExtension(String ext) {
			return codeExtensions.contains(ext.toLowerCase());
		}
		
	}

	static class ServerHelper implements IRunTypeHelper {

		@Override
		public String getNature() {
			return CoreConstants.SERVER_NATURE_ID;
		}

		@Override
		public boolean isSupportedExtension(String ext) {
			return codeExtensions.contains(ext.toLowerCase());
		}

	}

	static class ScriptHelper implements IRunTypeHelper {

		@Override
		public String getNature() {
			return CoreConstants.SCRIPTS_NATURE_ID;
		}

		@Override
		public boolean isSupportedExtension(String ext) {
			return scriptExtensions.contains(ext.toLowerCase());
		}

	}

	

}
