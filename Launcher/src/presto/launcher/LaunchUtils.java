package presto.launcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import presto.core.Utils;
import presto.core.Utils.RunType;
import presto.declaration.IMethodDeclaration;
import presto.parser.Dialect;

public class LaunchUtils {

	public static IProject getConfiguredProject(ILaunchConfiguration configuration) {
		try {
			String name = configuration.getAttribute(Constants.PROJECT, "");
			return name.length()>0 ? Utils.getRoot().getProject(name) : null;
		} catch (CoreException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	public static IFile getConfiguredFile(ILaunchConfiguration configuration, IProject project, RunType runType) {
		try {
			String path = configuration.getAttribute(Constants.FILE, "");
			if(!path.isEmpty()) {
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
	
	public static IMethodDeclaration getConfiguredMethod( ILaunchConfiguration configuration, IFile file) {
		try {
			String signature = configuration.getAttribute(Constants.METHOD, "");
			if(!signature.isEmpty()) {
				Dialect dialect = Utils.getDialect(file);
				for(IMethodDeclaration method : Utils.getEligibleMethods(file)) {
					if(signature.equals(method.getSignature(dialect))) 
						return method;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static String getConfiguredCommandLineArguments(ILaunchConfiguration configuration) {
		try {
			return configuration.getAttribute(Constants.ARGUMENTS, "");
		} catch (CoreException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	public static boolean getConfiguredStopInMain(ILaunchConfiguration configuration) {
		try {
			return configuration.getAttribute(Constants.STOP_IN_MAIN, true);
		} catch (CoreException e) {
			e.printStackTrace(System.err);
			return true;
		}
	}




}
