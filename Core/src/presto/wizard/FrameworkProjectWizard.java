package presto.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import presto.core.Constants;

public class FrameworkProjectWizard extends PrestoProjectWizard {
 
	@Override
	protected String getTitle() {
		return Constants.NEW_FRAMEWORK_PROJECT;
	}
	
	@Override
	protected String getDescription() {
		return Constants.FRAMEWORK_PROJECT_DESCRIPTION;
	}
	
	@Override
	protected String getNatureId() {
		return Constants.FRAMEWORK_NATURE_ID;
	}
	
	@Override
	protected IRunnableWithProgress buildCreateProjectOperation(final IProject project, final IProjectDescription description) {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					CreateProjectOperation cpo = new CreateProjectOperation(description, Constants.CREATING_PROJECT);
					cpo.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
				} catch (ExecutionException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
	}

}
