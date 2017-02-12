package prompto.core;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;

/**
 * The activator class controls the plug-in life cycle
 */
public class Plugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "prompto.core"; //$NON-NLS-1$

	// The shared instance
	private static Plugin plugin;
	
	/**
	 * The constructor
	 */
	public Plugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Plugin getDefault() {
		return plugin;
	}
	
	public static Preferences getPreferences() {
		return ConfigurationScope.INSTANCE.getNode(PLUGIN_ID);
	}

}
