package prompto.runner;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import prompto.launcher.LaunchContext;
import prompto.store.IEclipseCodeStore;

public class TEST_Runner extends RunnerBase {
	// Interpreter.interpretTest(store.getContext(), context.getMethod().getId(), true);

	@Override
	protected String getTargetJar(LaunchContext context) {
		return "Standalone-0.0.1-SNAPSHOT.jar";
	};
	
	@Override
	protected String getTargetType(LaunchContext context) {
		return "-test";
	}
	
	@Override
	protected String getTargetValue(LaunchContext context) {
		return context.getMethod().getName();
	}
	
	@Override
	protected Collection<IFile> getSourceFiles(LaunchContext context) throws CoreException {
		IEclipseCodeStore store = context.getCodeStore();
		return store.getFiles();
	}
	
	@Override
	protected Collection<String> getCommandLineArgs(LaunchContext context) {
		return Collections.emptyList();
	}
	
	

}
