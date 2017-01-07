package prompto.runner;

import prompto.runtime.Interpreter;
import prompto.launcher.LaunchContext;
import prompto.server.AppServer;
import prompto.store.IEclipseCodeStore;

public class RunTarget {

	public static void run(LaunchContext context) {
		try {
			IEclipseCodeStore store = context.getCodeStore();
			switch(context.getRunType()) {
			case APPLI:
				Interpreter.interpretMethod(store.getContext(), context.getMethod().getId(), context.getCmdLineArgs());
				break;
			case SERVER:
				AppServer.main(null);
				break;
			case SCRIPT:
				Interpreter.interpretScript(store.getContext(), context.getCmdLineArgs());
				break;
			case TEST:
				Interpreter.interpretTest(store.getContext(), context.getMethod().getId(), true);
				break;
			}
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}

}
