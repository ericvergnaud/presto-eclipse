package prompto.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;

import prompto.ide.addon.AddOn;
import prompto.launcher.ILaunchHelper;
import prompto.launcher.LaunchContext;

public abstract class Runner {

	public static void run(LaunchContext context) throws CoreException {
		try {
			ILaunchHelper helper = context.getLaunchHelper();
			String[] commands = buildCommands(context);
			ProcessBuilder builder = new ProcessBuilder(commands)
				.directory(new File(context.getDistribution().getDirectory()))
				.inheritIO();
			String processName = context.getConfiguration().getName() + " [" + helper.getRunTypeName() + "]";
			DebugPlugin.newProcess(context.getLaunch(), builder.start(), processName);
		} catch(IOException e) {
			e.printStackTrace(System.err);
			throw new CoreException(Status.CANCEL_STATUS);
		}
	}

	private static String[] buildCommands(LaunchContext context) throws CoreException {
		ILaunchHelper helper = context.getLaunchHelper();
		List<String> commands = new ArrayList<>();
		commands.add("java");
		commands.add("-jar");
		commands.add(helper.getTargetJar(context));
		commands.addAll(helper.getTargetSpecifiers(context));
		commands.add("-resources");
		commands.add(getResourcesAsString(context));
		if(hasAddOns()) {
			commands.add("-addOns");
			commands.add(getAddOnsAsString(context));
		}
		Collection<String> args = helper.getCommandLineArgs(context);
		if(args!=null && !args.isEmpty())
			commands.addAll(args);
		return commands.toArray(new String[commands.size()]);
	}


	public static Collection<String> getCommandLineArgs(LaunchContext context) {
		String args = context.getCmdLineArgs();
		if(args==null || args.trim().isEmpty())
			return Collections.emptyList();
		else
			return Arrays.asList(args.split(" "));
	}

	public static String getProjectName(LaunchContext context) {
		return context.getProject().getName();
	}

	public static String getResourcesAsString(LaunchContext context) throws CoreException {
		ILaunchHelper helper = context.getLaunchHelper();
		return "\""
				+ helper.getSourceFiles(context).stream()
					.map(IFile::getLocation)
					.map(IPath::toOSString)
					.collect(Collectors.joining(","))
				+ "\"";
	}
	
	private static boolean hasAddOns() {
		return !AddOn.loadAll().isEmpty();
	}


	public static String getAddOnsAsString(LaunchContext context) throws CoreException {
		return "\"" 
				+ Arrays.asList(AddOn.allURLs()).stream()
						.map(Object::toString)
						.collect(Collectors.joining(","))
				+ "\"";
	}


}
