package prompto.launcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import prompto.core.RunType;
import prompto.core.Utils;
import prompto.declaration.IDeclaration;

public class LaunchUtils {

	public static RunType getConfiguredRunType(ILaunchConfiguration configuration) {
		try {
			String value = configuration.getAttribute(LauncherConstants.RUNTYPE, "");
			if(value==null)
				return null;
			else
				return RunType.valueOf(value);
		} catch (CoreException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	public static String getConfiguredHttpPort(ILaunchConfiguration configuration) {
		try {
			String value = configuration.getAttribute(LauncherConstants.HTTP_PORT, "8080");
			return value.isEmpty() ? null : value;
		} catch (CoreException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	
	public static IProject getConfiguredProject(ILaunchConfiguration configuration) {
		try {
			String name = configuration.getAttribute(LauncherConstants.PROJECT, "");
			return name.length()>0 ? Utils.getRoot().getProject(name) : null;
		} catch (CoreException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	public static IFile getConfiguredFile(ILaunchConfiguration configuration, IProject project) {
		try {
			String path = configuration.getAttribute(LauncherConstants.FILE, "");
			if(!path.isEmpty()) {
				RunType runType = getConfiguredRunType(configuration);
				for(IFile file : Utils.getEligibleFiles(project, runType)) {
					if(path.equals(Utils.getFilePath(file)))
						return file;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}
	
	public static IDeclaration getConfiguredMethod( ILaunchConfiguration configuration, IFile file) {
		try {
			String signature = configuration.getAttribute(LauncherConstants.METHOD, "");
			if(signature.isEmpty())
				return null;
			else
				return getConfiguredMethod(configuration, file, signature);
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}
			
	private static IDeclaration getConfiguredMethod(ILaunchConfiguration configuration, IFile file, String signature) {
		RunType runType = getConfiguredRunType(configuration);
		return runType.findMethod(file, signature);
	}
	
	
	public static String getConfiguredCommandLineArguments(ILaunchConfiguration configuration) {
		try {
			return configuration.getAttribute(LauncherConstants.ARGUMENTS, "");
		} catch (CoreException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	public static boolean getConfiguredStopInMain(ILaunchConfiguration configuration) {
		try {
			return configuration.getAttribute(LauncherConstants.STOP_IN_MAIN, true);
		} catch (CoreException e) {
			e.printStackTrace(System.err);
			return true;
		}
	}



}
