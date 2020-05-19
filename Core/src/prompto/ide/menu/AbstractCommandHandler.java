package prompto.ide.menu;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractCommandHandler extends AbstractHandler {

	protected IProject getProjectFromMenuEvent(ExecutionEvent event) {
		return getProjectFromSelection(HandlerUtil.getActiveMenuSelection(event));
	}
	
	protected IProject getProjectFromSelection(ISelection selection) {
		if(selection instanceof IStructuredSelection)
			return getProjectFromStructuredSelection((IStructuredSelection)selection);
		else
			return null;
	}

	private IProject getProjectFromStructuredSelection(IStructuredSelection selection) {
		Iterator<?> iter = selection.iterator();
		while(iter.hasNext()) {
			Object o = iter.next();
			if(o instanceof IAdaptable) {
				IProject project = (IProject)((IAdaptable)o).getAdapter(IProject.class);
				if(project!=null)
					return project;
			}
		}
		return null;
	}

}
