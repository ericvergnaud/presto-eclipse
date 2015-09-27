package prompto.launcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import prompto.declaration.IDeclaration;
import prompto.core.Utils.RunType;
import prompto.store.IEclipseCodeStore;
import prompto.store.StoreUtils;

public class LaunchContext {

	ILaunch launch;
	ILaunchConfiguration configuration;
	RunType runType;
	IProject project;
	IFile file;
	IDeclaration method;
	String cmdLineArgs;
	boolean stopInMain;
	
	public LaunchContext(ILaunchConfiguration configuration, ILaunch launch) {
		this.configuration = configuration;
		this.launch = launch;
		readConfiguration();
	}
	
	public ILaunchConfiguration getConfiguration() {
		return configuration;
	}
	
	public RunType getRunType() {
		return runType;
	}

	public IProject getProject() {
		return project;
	}
	
	public ILaunch getLaunch() {
		return launch;
	}
	
	public IDeclaration getMethod() {
		return method;
	}
	
	public String getCmdLineArgs() {
		return cmdLineArgs;
	}
	
	public boolean isStopInMain() {
		return stopInMain;
	}
	
	private void readConfiguration() {
		runType = LaunchUtils.getConfiguredRunType(configuration);
		project = LaunchUtils.getConfiguredProject(configuration);
		file = LaunchUtils.getConfiguredFile(configuration, project);
		method = LaunchUtils.getConfiguredMethod(configuration, file);
		cmdLineArgs = LaunchUtils.getConfiguredCommandLineArguments(configuration);
		stopInMain = LaunchUtils.getConfiguredStopInMain(configuration);
	}

	public IEclipseCodeStore getCodeStore() throws CoreException {
		return StoreUtils.fetchStoreFor(file);
	}



}
