package presto.editor.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class Initializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
	    SyntaxColoring.initializeDefaultPreferences();
	}

}
