package prompto.ide.distribution;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.osgi.service.prefs.Preferences;

import prompto.ide.core.Plugin;
import prompto.ide.core.prefs.Initializer;

public class Distribution implements Comparable<Distribution>{

	public static boolean storeAll(Collection<Distribution> distributions) {
		String dists = Distribution.toPrefsString(distributions);
		Preferences prefs = Plugin.getPreferences();
		prefs.put(Initializer.PROMPTO_DISTRIBUTION_JAVA_LIST, dists);
		try {
			prefs.flush();
			return true;
		} catch(Exception e) {
			e.printStackTrace(System.err);
			return false;
		}
	}

	public static Collection<Distribution> loadAll() {
		Preferences prefs = Plugin.getPreferences();
		String pref = prefs.get(prompto.ide.core.prefs.Initializer.PROMPTO_DISTRIBUTION_JAVA_LIST, "");
		return Distribution.fromPrefsString(pref);
	}

	
	public static Distribution getDefaultDistribution() {
		Preferences prefs = Plugin.getPreferences();
		String pref = prefs.get(Initializer.PROMPTO_DISTRIBUTION_JAVA_LIST, "");
		Collection<Distribution> dists = fromPrefsString(pref);
		if(dists.isEmpty())
			return null;
		else
			return dists.iterator().next();
	}
	
	public static String toPrefsString(Collection<Distribution> list) {
		return list.stream()
				.map(Distribution::toString)
				.collect(Collectors.joining(":"));
	}
	
	public static Collection<Distribution> fromPrefsString(String string) {
		if(string.isEmpty()) 
			return Collections.emptyList();
		else 
			return Arrays.asList(string.split(":")).stream()
					.map(Distribution::fromString)
					.collect(Collectors.toList());
	}
	
	
	public static Distribution fromString(String s) {
		String[] parts = s.split("@");
		Version version = Version.parse(parts[0]);
		return new Distribution(version, parts[1]);
	}
	
	Version version;
	String directory;
	
	public Distribution(Version version, String directory) {
		this.version = version;
		this.directory = directory;
	}

	public Version getVersion() {
		return version;
	}
	
	public String getDirectory() {
		return directory;
	}
	
	@Override
	public int compareTo(Distribution o) {
		return version.compareTo(o.version);
	}
	
	@Override
	public int hashCode() {
		return version.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Distribution &&
				version.equals(((Distribution)obj).version);
	}
	
	@Override
	public String toString() {
		return version.toString() + '@' + directory;
	}


}
