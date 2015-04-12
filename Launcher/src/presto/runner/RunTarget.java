package presto.runner;

import presto.core.Utils.RunType;
import presto.launcher.ContextMap;
import presto.launcher.LaunchContext;
import presto.runtime.Interpreter;

public class RunTarget {

	public static void run(LaunchContext context) {
		try {
			ContextMap cm = context.buildContextMap(RunType.APPLI);
			switch(context.getRunType()) {
			case APPLI:
				Interpreter.interpretMethod(cm.getContext(), context.getMethod().getName(), context.getCmdLineArgs());
				break;
			case SCRIPT:
				Interpreter.interpretScript(cm.getContext(), context.getCmdLineArgs());
				break;
			case TEST:
				Interpreter.interpretTest(cm.getContext(), context.getMethod().getName().toString());
				break;
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
