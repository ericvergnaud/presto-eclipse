package prompto.launcher;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.service.prefs.Preferences;

import prompto.declaration.IDeclaration;
import prompto.distribution.Distribution;
import prompto.code.IEclipseCodeStore;
import prompto.code.StoreUtils;
import prompto.core.RunType;
import prompto.launcher.prefs.Initializer;

public class LaunchContext {

	ILaunch launch;
	ILaunchConfiguration configuration;
	RunType runType;
	String httpPort;
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

	public String getHttpPort() {
		return httpPort;
	}


	public IProject getProject() {
		return project;
	}
	
	public IFile getFile() {
		return file;
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
		httpPort = LaunchUtils.getConfiguredHttpPort(configuration);
		project = LaunchUtils.getConfiguredProject(configuration);
		file = LaunchUtils.getConfiguredFile(configuration, project);
		method = LaunchUtils.getConfiguredMethod(configuration, file);
		cmdLineArgs = LaunchUtils.getConfiguredCommandLineArguments(configuration);
		stopInMain = LaunchUtils.getConfiguredStopInMain(configuration);
	}

	public IEclipseCodeStore getCodeStore() throws CoreException {
		return StoreUtils.fetchStoreFor(file);
	}

	public Distribution getDistribution() {
		// TODO get distribution from "runtime" page in launcher 
		Preferences prefs = Plugin.getPreferences();
		String pref = prefs.get(Initializer.PROMPTO_DISTRIBUTION_JAVA_LIST, "");
		Collection<Distribution> dists = Distribution.fromPrefsString(pref);
		if(dists.isEmpty())
			return null;
		else
			return dists.iterator().next();
	}

	public ILaunchHelper getLaunchHelper() {
		switch(getRunType()) {
		case APPLI:
			return new ILaunchHelper.Application();
		case TEST:
			return new ILaunchHelper.Test();
		case SCRIPT:
			return new ILaunchHelper.Script();
		case SERVER:
			return new ILaunchHelper.Server();
		default:
			throw new UnsupportedOperationException(getRunType().toString());
		}
	}


}
