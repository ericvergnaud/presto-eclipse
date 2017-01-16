package prompto.launcher.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import prompto.launcher.Plugin;

public class Initializer extends AbstractPreferenceInitializer {
	
	static final String PROMPTO_RUNTIME_JAVA_LIST = "prompto.runtime.java.list";
	static final String PROMPTO_RUNTIME_JAVA_ACTIVE = "prompto.runtime.java.active";
	static final String PROMPTO_RUNTIME_ACTIVE = "prompto.runtime.active";
	
	@Override
	public void initializeDefaultPreferences() {
	    IPreferenceStore store = Plugin.getPreferenceStore();
	    store.setValue(PROMPTO_RUNTIME_JAVA_LIST, "");
	    store.setValue(PROMPTO_RUNTIME_JAVA_ACTIVE, "");
	    store.setValue(PROMPTO_RUNTIME_ACTIVE, "");
	}

}
