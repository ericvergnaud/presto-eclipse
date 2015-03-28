package presto.locator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

/**
 * Computes the default source lookup path for a PDA launch configuration.
 * The default source lookup path is the folder or project containing 
 * the PDA program being launched. If the program is not specified, the workspace
 * is searched by default.
 */
public class SourcePathComputer implements ISourcePathComputerDelegate {
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourcePathComputerDelegate#computeSourceContainers(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		return new ISourceContainer[]{ new WorkspaceSourceContainer() };
	}
}
