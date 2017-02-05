package prompto.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import prompto.debugger.Debugger;
import prompto.profiler.Profiler;
import prompto.runner.Runner;

public class LaunchDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		LaunchContext context = new LaunchContext(configuration, launch);
		switch(mode) {
		case ILaunchManager.RUN_MODE:
			Runner.run(context);
			break;
		case ILaunchManager.DEBUG_MODE:
			Debugger.run(context);
			break;
		case ILaunchManager.PROFILE_MODE:
			Profiler.run(context);
			break;
		default:
			throw new CoreException(Status.CANCEL_STATUS);
		}
	}

}
