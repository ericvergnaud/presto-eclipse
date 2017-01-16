package prompto.distribution;

public class Version implements Comparable<Version> {
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
}
