package prompto.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;

import prompto.launcher.LaunchContext;

public abstract class RunnerBase implements IRunner {

	@Override
	public void run(LaunchContext context) throws CoreException {
		try {
			String[] commands = buildCommands(context);
			ProcessBuilder builder = new ProcessBuilder(commands)
				.directory(new File(context.getDistribution().getDirectory()))
				.inheritIO();
			String processName = context.getConfiguration().getName() + " [" + getProcessName() + "]";
			DebugPlugin.newProcess(context.getLaunch(), builder.start(), processName);
		} catch(IOException e) {
			e.printStackTrace(System.err);
			throw new CoreException(Status.CANCEL_STATUS);
		}
	}

	private String[] buildCommands(LaunchContext context) throws CoreException {
		List<String> commands = new ArrayList<>();
		commands.add("java");
		commands.add("-jar");
		commands.add(getTargetJar(context));
		commands.addAll(getTargetSpecifiers(context));
		commands.add("-resources");
		commands.add(getResourcesAsString(context));
		Collection<String> args = getCommandLineArgs(context);
		if(args!=null && !args.isEmpty())
			commands.addAll(args);
		return commands.toArray(new String[commands.size()]);
	}


	protected Collection<String> getCommandLineArgs(LaunchContext context) {
		String args = context.getCmdLineArgs();
		if(args==null || args.trim().isEmpty())
			return Collections.emptyList();
		else
			return Arrays.asList(args.split(" "));
	}

	public String getProjectName(LaunchContext context) {
		return context.getProject().getName();
	}

	protected abstract String getProcessName();
	protected abstract Collection<IFile> getSourceFiles(LaunchContext context) throws CoreException;
	protected abstract String getTargetJar(LaunchContext context);
	protected abstract List<String> getTargetSpecifiers(LaunchContext context);

	private String getResourcesAsString(LaunchContext context) throws CoreException {
		StringBuilder sb = new StringBuilder();
		sb.append('"');
		getSourceFiles(context).forEach((file)->{
			sb.append(file.getLocation().toOSString());
			sb.append(',');
		});
		if(sb.length()>1)
			sb.setLength(sb.length()-1);
		sb.append('"');
		return sb.toString();
	}

}
