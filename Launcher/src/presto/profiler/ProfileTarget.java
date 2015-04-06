package presto.profiler;

import presto.core.Utils.RunType;
import presto.launcher.ContextMap;
import presto.launcher.LaunchContext;
import presto.runtime.Interpreter;

public class ProfileTarget {

	public static void profile(LaunchContext context) {
		try {
			ContextMap cm = context.buildContextMap(RunType.SCRIPT);
			Interpreter.interpret(cm.getContext(), context.getMethod().getName(), context.getCmdLineArgs());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
