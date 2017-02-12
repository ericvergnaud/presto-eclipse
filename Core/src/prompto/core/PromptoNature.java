package prompto.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class PromptoNature implements IProjectNature {
	
	private IProject project;

	@Override
	public void configure() throws CoreException {
	}

	@Override
	public void deconfigure() throws CoreException {
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}
	
	/*
	public static void addConfigureRuntimeCommandToBuildSpec(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		addConfigureRuntimeCommandToBuildSpec(description);
		project.setDescription(description, null);
	}
	
	public static void addConfigureRuntimeCommandToBuildSpec(IProjectDescription description) throws CoreException {
		int scriptCommandIndex = getCommandIndex(description.getBuildSpec(), CoreConstants.CONFIGURE_RUNTIME_COMMAND_ID);
		if (scriptCommandIndex == -1) {
			// Add a Java command to the build spec
			ICommand command = description.newCommand();
			command.setBuilderName(CoreConstants.CONFIGURE_RUNTIME_COMMAND_ID);
			addScriptCommand(description, command);
		}
	}

	protected static int getCommandIndex(ICommand[] buildSpec, String name) {
		for (int i = 0; i < buildSpec.length; ++i) {
			if (buildSpec[i].getBuilderName().equals(name)) {
				return i;
			}
		}
		return -1;
	}

	private static void addScriptCommand(IProjectDescription description, ICommand newCommand) throws CoreException {
		ICommand[] oldBuildSpec = description.getBuildSpec();
		int oldScriptCommandIndex = getCommandIndex(oldBuildSpec, newCommand.getBuilderName());
		ICommand[] newCommands;

		if (oldScriptCommandIndex == -1) {
			// Add a Java build spec before other builders (1FWJK7I)
			newCommands = new ICommand[oldBuildSpec.length + 1];
			System.arraycopy(oldBuildSpec, 0, newCommands, 1, oldBuildSpec.length);
			newCommands[0] = newCommand;
		} else {
			oldBuildSpec[oldScriptCommandIndex] = newCommand;
			newCommands = oldBuildSpec;
		}

		// Commit the spec change into the project
		description.setBuildSpec(newCommands);
	}	
	
	public static void removeConfigureRuntimeCommandFromBuildSpec(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		if(removeConfigureRuntimeCommandFromBuildSpec(description))
			project.setDescription(description, null);
	}
	
	public static boolean removeConfigureRuntimeCommandFromBuildSpec(IProjectDescription description) throws CoreException {
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(CoreConstants.CONFIGURE_RUNTIME_COMMAND_ID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
				description.setBuildSpec(newCommands);
				return true;
			}
		}
		return false;
	}
	*/

}
