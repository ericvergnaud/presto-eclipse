package prompto.ide.core.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.osgi.service.prefs.Preferences;

import prompto.ide.core.Plugin;



public class Initializer extends AbstractPreferenceInitializer {

	public static final String PROMPTO_DISTRIBUTION_JAVA_LIST = "prompto.distribution.java.list";
	public static final String PROMPTO_DISTRIBUTION_JAVA_DEFAULT = "prompto.distribution.java.default";
	public static final String PROMPTO_ADDON_JAVA_LIST = "prompto.addon.java.list";
	
	@Override
	public void initializeDefaultPreferences() {
	    Preferences prefs = Plugin.getPreferences();
	    prefs.put(PROMPTO_DISTRIBUTION_JAVA_LIST, "");
	    prefs.put(PROMPTO_DISTRIBUTION_JAVA_DEFAULT, "");
	    prefs.put(PROMPTO_ADDON_JAVA_LIST, "");
	}

}
