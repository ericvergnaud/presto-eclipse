package prompto.runner;

import org.eclipse.core.runtime.CoreException;

import prompto.launcher.LaunchContext;

public interface IRunner {

	void run(LaunchContext context) throws CoreException;

}
