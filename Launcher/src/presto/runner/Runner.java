package presto.runner;

import core.runtime.Interpreter;
import presto.launcher.ContextMap;
import presto.launcher.LaunchContext;

public class Runner {

	public static void run(LaunchContext context) {
		try {
			ContextMap cm = context.buildContextMap();
			Interpreter.interpret(cm.getContext(), context.getMethod().getName(), context.getCmdLineArgs());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
