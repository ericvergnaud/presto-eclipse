package prompto.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

import prompto.core.CoreConstants;

public class ServerProjectWizard extends PromptoProjectWizard {
 

	@Override
	protected String getTitle() {
		return CoreConstants.NEW_SERVER_PROJECT;
	};
	
	@Override
	protected String getDescription() {
		return CoreConstants.SERVER_PROJECT_DESCRIPTION;
	}

	@Override
	protected String getNatureId() {
		return CoreConstants.SERVER_NATURE_ID;
	}
	
	@Override
	protected IRunnableWithProgress buildCreateProjectOperation(final IProject project, final IProjectDescription description) {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					CreateProjectOperation cpo = new CreateProjectOperation(description, CoreConstants.CREATING_PROJECT);
					cpo.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
					IFile file = project.getFile("service.pec");
					String contents = "define main as method receiving Text{} args doing:\n"
							+ "\tprint \"Hello\"\n";
					try(InputStream input = new ByteArrayInputStream(contents.getBytes())) {
						CreateFileOperation cfo = new CreateFileOperation(file, null, input, CoreConstants.CREATING_SAMPLE_SERVER);
						cfo.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
					}
				} catch (ExecutionException | IOException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
	}

}
