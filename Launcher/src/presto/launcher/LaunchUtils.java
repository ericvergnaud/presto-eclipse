package presto.launcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import presto.core.Utils;
import presto.core.Utils.RunType;
import presto.declaration.IDeclaration;
import presto.declaration.IMethodDeclaration;
import presto.declaration.TestMethodDeclaration;
import presto.parser.Dialect;

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
		switch( getConfiguredRunType(configuration)) {
		case TEST:
			return getConfiguredTestMethod(file, signature);
		case APPLI:
			return getConfiguredMainMethod(file, signature);
		default:
			return null; // TODO log ? throw ?
		}
	}

	private static IMethodDeclaration getConfiguredMainMethod(IFile file, String signature) {
		Dialect dialect = Utils.getDialect(file);
		for(IMethodDeclaration method : Utils.getEligibleMainMethods(file)) {
			if(signature.equals(method.getSignature(dialect))) 
				return method;
		}
		return null;
	}

	private static TestMethodDeclaration getConfiguredTestMethod(IFile file, String signature) {
		for(TestMethodDeclaration method : Utils.getEligibleTestMethods(file)) {
			if(signature.equals(method.getName()))
				return method;
		}
		return null;
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
