package presto.runner;

import presto.launcher.LaunchContext;
import presto.runtime.Interpreter;
import presto.store.IEclipseCodeStore;

public class RunTarget {

	public static void run(LaunchContext context) {
		try {
			IEclipseCodeStore store = context.getCodeStore();
			switch(context.getRunType()) {
			case APPLI:
				Interpreter.interpretMethod(store.getContext(), context.getMethod().getIdentifier(), context.getCmdLineArgs());
				break;
			case SCRIPT:
				Interpreter.interpretScript(store.getContext(), context.getCmdLineArgs());
				break;
			case TEST:
				Interpreter.interpretTest(store.getContext(), context.getMethod().getIdentifier());
				break;
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
