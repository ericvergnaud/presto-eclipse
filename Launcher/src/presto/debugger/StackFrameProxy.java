package presto.debugger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

import core.debug.StackFrame;

public class StackFrameProxy extends PlatformObject implements IStackFrame {

	DebugThread thread;
	StackFrame frame;
	
	public StackFrameProxy(DebugThread thread, StackFrame frame) {
		this.thread = thread;
		this.frame = frame;
	}

	@Override
	public String getModelIdentifier() {
		return Constants.MODEL_IDENTIFIER;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return thread.getDebugTarget();
	}

	@Override
	public ILaunch getLaunch() {
		return thread.getLaunch();
	}

	@Override
	public IThread getThread() {
		return thread;
	}
	
	public StackFrame getStackFrame() {
		return frame;
	}


	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (adapter == IDebugElement.class)
			return this;
		else if(adapter==ILaunch.class)
			return getLaunch();
		else if(adapter==IResource.class)
			return getFile();
		return super.getAdapter(adapter);
	}
	
	IFile file = null;

	private IFile getFile() {
		if(file==null) {
			try {
				// need a project relative path
				IPath path = new Path(frame.getPath()).removeFirstSegments(1);
				file = (IFile)new WorkspaceSourceContainer().findSourceElements(path.toPortableString())[0];
			} catch (CoreException e) {
			}
		}
		return file;
	}

	@Override
	public boolean canStepInto() {
		return thread.canStepInto();
	}

	@Override
	public boolean canStepOver() {
		return thread.canStepOver();
	}

	@Override
	public boolean canStepReturn() {
		return thread.canStepReturn();
	}

	@Override
	public boolean isStepping() {
		return thread.isStepping();
	}

	@Override
	public void stepInto() throws DebugException {
		thread.stepInto();
	}

	@Override
	public void stepOver() throws DebugException {
		thread.stepOver();
	}

	@Override
	public void stepReturn() throws DebugException {
		thread.stepReturn();
	}

	@Override
	public boolean canResume() {
		return thread.canResume();
	}

	@Override
	public boolean canSuspend() {
		return thread.canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return thread.isSuspended();
	}

	@Override
	public void resume() throws DebugException {
		thread.resume();
	}

	@Override
	public void suspend() throws DebugException {
		thread.suspend();
	}

	@Override
	public boolean canTerminate() {
		return thread.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return thread.isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		thread.terminate();
	}

	@Override
	public IVariable[] getVariables() throws DebugException {
		return new IVariable[0];
	}

	@Override
	public boolean hasVariables() throws DebugException {
		return true;
	}

	@Override
	public int getLineNumber() throws DebugException {
		return frame.getLine();
	}

	@Override
	public int getCharStart() throws DebugException {
		return frame.getCharStart();
	}

	@Override
	public int getCharEnd() throws DebugException {
		return frame.getCharEnd();
	}

	@Override
	public String getName() {
		return frame.toString();
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return null;
	}

	@Override
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(!(obj instanceof StackFrameProxy))
			return false;
		StackFrameProxy sfp = (StackFrameProxy)obj;
		return this.thread.equals(sfp.thread)
				&& this.frame.equals(sfp.frame);
	}

}
