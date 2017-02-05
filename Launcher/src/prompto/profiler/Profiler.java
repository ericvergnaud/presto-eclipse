package prompto.profiler;

import org.eclipse.core.runtime.CoreException;

import prompto.launcher.LaunchContext;
import prompto.runner.Runner;

public class Profiler {

	public static void run(LaunchContext context) throws CoreException {
		Runner.run(context); // TODO: profile
	}

}
