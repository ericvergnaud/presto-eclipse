package presto.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import presto.debugger.DebugTarget;
import presto.runner.Runner;

public class LaunchDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		LaunchContext context = new LaunchContext(configuration, launch);
		if(ILaunchManager.RUN_MODE.equals(mode))
			Runner.run(context);
		else if(ILaunchManager.DEBUG_MODE.equals(mode))
			DebugTarget.debug(context);
	}

}
