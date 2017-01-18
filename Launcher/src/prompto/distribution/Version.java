package prompto.distribution;

public class Version implements Comparable<Version> {
	
	public static Version parse(String versionString) {
		Version version = new Version();
		String[] sections = versionString.split("-");
		if(sections.length>1)
			version.build = sections[1];
		String[] digits = sections[0].split("\\.");
		version.major = Integer.parseInt(digits[0]);
		version.minor = Integer.parseInt(digits[1]);
		version.fix = Integer.parseInt(digits[2]);
		return version;
	}

	int major;
	int minor;
	int fix;
	String build;
	
	@Override
	public int compareTo(Version o) {
		int cmp = Integer.compareUnsigned(major, o.major);
		if(cmp!=0)
			return cmp;
		cmp = Integer.compareUnsigned(minor, o.minor);
		if(cmp!=0)
			return cmp;
		cmp = Integer.compareUnsigned(fix, o.fix);
		if(cmp!=0)
			return cmp;
		if(build==null)
			return o.build==null ? 0 : -1;
		else
			return build.compareTo(o.build);
	}
	
	@Override
	public String toString() {
		return "" + major + "." + minor + "." + fix
				+ (build==null ? "" : "-" + build);
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Version &&
				this.toString().equals(obj.toString());
	}

}
