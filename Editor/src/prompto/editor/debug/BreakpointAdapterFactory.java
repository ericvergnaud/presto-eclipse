package prompto.editor.debug;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;

import prompto.editor.MultiPageEditor;

@SuppressWarnings("unchecked")
public class BreakpointAdapterFactory extends PlatformObject implements IAdapterFactory {

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if(adapterType==IToggleBreakpointsTarget.class) {
	     if (adaptableObject instanceof MultiPageEditor) {
	    	 return (T)new BreakpointAdapter();
	     }
		}
	    return super.getAdapter(adapterType);
	} 

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[]{ IToggleBreakpointsTarget.class };
	}

}
