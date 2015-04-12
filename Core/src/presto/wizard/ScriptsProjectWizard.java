package presto.wizard;

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

import presto.core.CoreConstants;

public class ScriptsProjectWizard extends PrestoProjectWizard {
 

	@Override
	protected String getTitle() {
		return CoreConstants.NEW_SCRIPTS_PROJECT;
	};
	
	@Override
	protected String getDescription() {
		return CoreConstants.SCRIPTS_PROJECT_DESCRIPTION;
	}

	@Override
	protected String getNatureId() {
		return CoreConstants.SCRIPTS_NATURE_ID;
	}
	
	@Override
	protected IRunnableWithProgress buildCreateProjectOperation(final IProject project, final IProjectDescription description) {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					createProject(monitor);
					createSampleScript(monitor);
				} catch (ExecutionException e) {
					throw new InvocationTargetException(e);
				}
			}

			private void createSampleScript(IProgressMonitor monitor) throws ExecutionException {
				IFile file = project.getFile("SampleScript.ped");
				CreateFileOperation cfo = new CreateFileOperation(file, null, null /*contents*/, CoreConstants.CREATING_SAMPLE_SCRIPT);
				cfo.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
			}

			private void createProject(IProgressMonitor monitor) throws ExecutionException {
				CreateProjectOperation cpo = new CreateProjectOperation(description, CoreConstants.CREATING_PROJECT);
				cpo.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
			}
		};
	}

}
