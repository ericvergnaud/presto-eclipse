package presto.profiler;

import presto.launcher.LaunchContext;
import presto.runner.RunTarget;

public class ProfileTarget {

	public static void profile(LaunchContext context) {
		RunTarget.run(context); // TODO: profile
	}

}
