package prompto.runner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import prompto.launcher.LaunchContext;
import prompto.store.IEclipseCodeStore;

public class SERVER_Runner extends RunnerBase {

	@Override
	protected String getProcessName() {
		return "Prompto Server";
	}

	@Override
	protected String getTargetJar(LaunchContext context) {
		return "Server-0.0.1-SNAPSHOT.jar";
	};
	
	@Override
	protected List<String> getTargetSpecifiers(LaunchContext context) {
		return Arrays.asList("-application", getProjectName(context),
				"-http_port", context.getHttpPort(),
				"-serverAboutToStart", context.getMethod().getName());
	}


	@Override
	protected Collection<IFile> getSourceFiles(LaunchContext context) throws CoreException {
		IEclipseCodeStore store = context.getCodeStore();
		return store.getFiles();
	}
	
}
