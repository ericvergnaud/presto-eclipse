package presto.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import presto.debugger.DebugTarget;
import presto.profiler.ProfileTarget;
import presto.runner.RunTarget;

public class LaunchDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		LaunchContext context = new LaunchContext(configuration, launch);
		switch(mode) {
		case ILaunchManager.RUN_MODE:
			RunTarget.run(context);
			break;
		case ILaunchManager.DEBUG_MODE:
			DebugTarget.debug(context);
			break;
		case ILaunchManager.PROFILE_MODE:
			ProfileTarget.profile(context);
			break;
		default:
			throw new CoreException(Status.CANCEL_STATUS);
		}
	}

}
