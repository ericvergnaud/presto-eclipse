package prompto.editor.debug;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;

import prompto.editor.MultiPageEditor;

@SuppressWarnings({"rawtypes"})
public class BreakpointAdapterFactory extends PlatformObject implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType==IToggleBreakpointsTarget.class) {
	     if (adaptableObject instanceof MultiPageEditor) {
	    	 return new BreakpointAdapter();
	     }
		}
	    return super.getAdapter(adapterType);
	} 

	@Override
	public Class[] getAdapterList() {
		return new Class[]{ IToggleBreakpointsTarget.class };
	}

}
