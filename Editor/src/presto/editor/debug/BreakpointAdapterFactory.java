package presto.editor.debug;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;

import presto.editor.base.SourceEditorBase;

@SuppressWarnings("rawtypes")
public class BreakpointAdapterFactory extends PlatformObject implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType==IToggleBreakpointsTarget.class) {
	     if (adaptableObject instanceof SourceEditorBase) {
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
