package prompto.debugger;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import prompto.core.CoreConstants;

public class ValueProxy extends PlatformObject implements IValue {

	VariableProxy variable;
	prompto.debug.IValue value;
	
	public ValueProxy(VariableProxy variable, prompto.debug.IValue value) {
		this.variable = variable;
		this.value = value;
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
		return variable.getDebugTarget();
	}
	
	@Override
	public ILaunch getLaunch() {
		return variable.getLaunch();
	}

	@Override
	public String getModelIdentifier() {
		return CoreConstants.DEBUG_MODEL_IDENTIFIER;
	}
	
	@Override
	public String getReferenceTypeName() throws DebugException {
		return value.getTypeName();
	}
	
	@Override
	public String getValueString() throws DebugException {
		return value.getValueString();
	}
	
	@Override
	public boolean hasVariables() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public IVariable[] getVariables() throws DebugException {
		// TODO Auto-generated method stub
		return new IVariable[0];
	}

	@Override
	public boolean isAllocated() throws DebugException {
		// TODO Auto-generated method stub
		return true;
	}
	
}
