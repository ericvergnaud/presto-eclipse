package prompto.runner;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import prompto.launcher.LaunchContext;
import prompto.store.IEclipseCodeStore;

public class SERVER_Runner extends RunnerBase {
	// AppServer.main(null);



	@Override
	protected String getTargetJar(LaunchContext context) {
		return "Server-0.0.1-SNAPSHOT.jar";
	};
	
	@Override
	protected String getTargetType(LaunchContext context) {
		return "-application";
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
	
}
