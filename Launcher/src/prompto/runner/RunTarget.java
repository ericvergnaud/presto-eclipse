package prompto.runner;

import prompto.runtime.Interpreter;
import prompto.launcher.LaunchContext;
import prompto.store.IEclipseCodeStore;

public class RunTarget {

	public static void run(LaunchContext context) {
		try {
			IEclipseCodeStore store = context.getCodeStore();
			switch(context.getRunType()) {
			case APPLI:
				Interpreter.interpretMethod(store.getContext(), context.getMethod().getId(), context.getCmdLineArgs());
				break;
			case SCRIPT:
				Interpreter.interpretScript(store.getContext(), context.getCmdLineArgs());
				break;
			case TEST:
				Interpreter.interpretTest(store.getContext(), context.getMethod().getId());
				break;
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
