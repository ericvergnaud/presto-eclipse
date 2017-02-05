package prompto.runner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import prompto.code.IEclipseCodeStore;
import prompto.launcher.LaunchContext;

public class APPLI_Runner extends RunnerBase {

	@Override
	protected String getProcessName() {
		return "Prompto Application";
	}

	@Override
	protected String getTargetJar(LaunchContext context) {
		return "Standalone-0.0.1-SNAPSHOT.jar";
	};
	
	@Override
	protected List<String> getTargetSpecifiers(LaunchContext context) {
		return Arrays.asList("-application", getProjectName(context),
				"-mainMethod", context.getMethod().getName());
	}
	
	@Override
	protected Collection<IFile> getSourceFiles(LaunchContext context) throws CoreException {
		IEclipseCodeStore store = context.getCodeStore();
		return store.getFiles();
	}
	

	
	
}
