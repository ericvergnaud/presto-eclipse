package prompto.ide.addon;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.prefs.Preferences;

import prompto.ide.core.Plugin;
import prompto.ide.core.prefs.Initializer;

public abstract class AddOn {

	public static boolean storeAll(Collection<String> addOns) {
		String dists = AddOn.toPrefsString(addOns);
		Preferences prefs = Plugin.getPreferences();
		prefs.put(Initializer.PROMPTO_ADDON_JAVA_LIST, dists);
		try {
			prefs.flush();
			return true;
		} catch(Exception e) {
			e.printStackTrace(System.err);
			return false;
		}
	}

	public static Collection<String> loadAll() {
		Preferences prefs = Plugin.getPreferences();
		String pref = prefs.get(prompto.ide.core.prefs.Initializer.PROMPTO_ADDON_JAVA_LIST, "");
		return AddOn.fromPrefsString(pref);
	}
	
	public static URL[] allURLs() {
		Collection<String> all = loadAll();
		if(all==null || all.isEmpty())
			return null;
		List<URL> urls = new ArrayList<URL>();
		for(String s : all) try {
			File file = new File(s); 
			urls.add(file.toURI().toURL());
		} catch(MalformedURLException e) {
			return null;
		}
		return urls.toArray(new URL[urls.size()]);
	}

	public static String toPrefsString(Collection<String> list) {
		return list.stream()
				.collect(Collectors.joining(":"));
	}
	
	public static Collection<String> fromPrefsString(String string) {
		if(string.isEmpty()) 
			return Collections.emptyList();
		else 
			return Arrays.asList(string.split(":"));
	}
}
