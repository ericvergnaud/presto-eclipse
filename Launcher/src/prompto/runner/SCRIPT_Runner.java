package prompto.runner;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import prompto.launcher.LaunchContext;

public class SCRIPT_Runner extends RunnerBase {
	// Interpreter.interpretScript(store.getContext(), context.getCmdLineArgs());

	@Override
	protected String getTargetJar(LaunchContext context) {
		return "Standalone-0.0.1-SNAPSHOT.jar";
	}
	
	@Override
	protected String getTargetType(LaunchContext context) {
		return "-script";
	}
	
	@Override
	protected String getTargetValue(LaunchContext context) {
		return context.getFile().getName();
	}
	
	@Override
	protected Collection<IFile> getSourceFiles(LaunchContext context) throws CoreException {
		return Arrays.asList(context.getFile());
	}
	
}
