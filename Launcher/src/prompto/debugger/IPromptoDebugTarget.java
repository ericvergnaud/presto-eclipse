package prompto.debugger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IDebugTarget;

import prompto.debug.IDebugEventListener;
import prompto.debug.IDebugger;

public interface IPromptoDebugTarget extends IDebugTarget {

	void debug() throws CoreException;
	IFile resolveFile(String filePath) throws CoreException;
	IDebugger getDebugger();
	IDebugEventListener getDebugEventListener();
}
