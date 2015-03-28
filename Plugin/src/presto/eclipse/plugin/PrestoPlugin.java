package presto.eclipse.plugin;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PrestoPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "presto.eclipse.plugin"; //$NON-NLS-1$

	// The shared instance
	private static PrestoPlugin plugin;
	
	private static IPreferenceStore prefsStore = null;
	
	/**
	 * The constructor
	 */
	public PrestoPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.Plugin#stop(org.osgi.framework.BundleContext)
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
	public static PrestoPlugin getDefault() {
		return plugin;
	}
	
	public static IPreferenceStore getPreferenceStore() {
		if(prefsStore==null)
			prefsStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, plugin.getBundle().getSymbolicName());
		return prefsStore;
	}
	


}
