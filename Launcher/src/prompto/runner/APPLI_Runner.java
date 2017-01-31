package prompto.runner;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import prompto.launcher.LaunchContext;
import prompto.store.IEclipseCodeStore;

public class APPLI_Runner extends RunnerBase {

	@Override
	protected String getTargetJar(LaunchContext context) {
		return "Standalone-0.0.1-SNAPSHOT.jar";
	};
	
	@Override
	protected String getTargetType(LaunchContext context) {
		return "-main";
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
