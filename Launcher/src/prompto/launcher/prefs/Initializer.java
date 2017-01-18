package prompto.launcher.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.osgi.service.prefs.Preferences;

import prompto.launcher.Plugin;

public class Initializer extends AbstractPreferenceInitializer {
	
	static final String PROMPTO_DISTRIBUTION_JAVA_LIST = "prompto.distribution.java.list";
	static final String PROMPTO_DISTRIBUTION_JAVA_ACTIVE = "prompto.distribution.java.active";
	
	@Override
	public void initializeDefaultPreferences() {
	    Preferences prefs = Plugin.getPreferences();
	    prefs.put(PROMPTO_DISTRIBUTION_JAVA_LIST, "");
	    prefs.put(PROMPTO_DISTRIBUTION_JAVA_ACTIVE, "");
	}

}
