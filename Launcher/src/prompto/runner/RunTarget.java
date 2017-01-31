package prompto.runner;

import prompto.launcher.LaunchContext;

public class RunTarget {

	public static void run(LaunchContext context) {
		try {
			@SuppressWarnings("unchecked")
			Class<IRunner> klass = (Class<IRunner>)Class.forName("prompto.runner." + context.getRunType().name() + "_Runner");
			IRunner runner = klass.newInstance();
			runner.run(context);
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}

}
