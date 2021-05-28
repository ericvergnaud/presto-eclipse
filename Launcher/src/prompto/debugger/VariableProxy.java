package prompto.debugger;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import prompto.ide.core.CoreConstants;

public class VariableProxy extends PlatformObject implements IVariable {

	StackFrameProxy frame;
	prompto.debug.variable.IVariable variable;
	
	public VariableProxy(StackFrameProxy frame, prompto.debug.variable.IVariable variable) {
		this.frame = frame;
		this.variable = variable;
	}
	
	@Override
	@SuppressWarnings({ "unchecked" })
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == IDebugElement.class)
			return (T) this;
		else if(adapter==ILaunch.class)
			return (T) getLaunch();
		else
			return super.getAdapter(adapter);
	}
	
	@Override
	public IDebugTarget getDebugTarget() {
		return frame.getDebugTarget();
	}
	
	@Override
	public ILaunch getLaunch() {
		return frame.getLaunch();
	}

	@Override
	public String getModelIdentifier() {
		return CoreConstants.DEBUG_MODEL_IDENTIFIER;
	}
	
	@Override
	public String getName() throws DebugException {
		return variable.getName();
	}
	
	@Override
	public String getReferenceTypeName() throws DebugException {
		return variable.getTypeName();
	}
	
	@Override
	public IValue getValue() throws DebugException {
		return new ValueProxy(this, variable.getValue());
	}
	
	@Override
	public boolean supportsValueModification() {
		return false;
	}
	
	@Override
	public boolean hasValueChanged() throws DebugException {
		return true;
	}

	@Override
	public void setValue(String expression) throws DebugException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setValue(IValue value) throws DebugException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean verifyValue(String expression) throws DebugException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean verifyValue(IValue value) throws DebugException {
		throw new UnsupportedOperationException();
	}
	
	
	
}
