package presto.core;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

public class ApplicationProjectWizard extends PrestoProjectWizard {
 

	@Override
	protected String getTitle() {
		return Constants.NEW_APPLICATION_PROJECT;
	};
	
	@Override
	protected String getDescription() {
		return Constants.APPLICATION_PROJECT_DESCRIPTION;
	}

	@Override
	protected String getNatureId() {
		return Constants.APPLICATION_NATURE_ID;
	}
	
	@Override
	protected IRunnableWithProgress buildCreateProjectOperation(final IProject project, final IProjectDescription description) {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					CreateProjectOperation cpo = new CreateProjectOperation(description, Constants.CREATING_PROJECT);
					cpo.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
					IFile file = project.getFile("SampleApplication.ped");
					CreateFileOperation cfo = new CreateFileOperation(file, null, null /*contents*/, Constants.CREATING_SAMPLE_APPLICATION);
					cfo.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
				} catch (ExecutionException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
	}

}
