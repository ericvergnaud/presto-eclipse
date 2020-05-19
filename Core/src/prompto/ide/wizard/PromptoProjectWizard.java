package prompto.ide.wizard;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import prompto.ide.core.CoreConstants;

@SuppressWarnings("restriction")
public abstract class PromptoProjectWizard extends BasicNewResourceWizard implements INewWizard {
 
	WizardNewProjectCreationPage creationPage;

	@Override
	public void addPages() {
		super.addPages();
		creationPage = new WizardNewProjectCreationPage("basicNewProjectPage") { 
			public void createControl(Composite parent) {
				super.createControl(parent);
				createWorkingSetGroup(
						(Composite) getControl(),
						getSelection(),
						new String[] { "org.eclipse.ui.resourceWorkingSetPage" }); 
				createDialectGroup((Composite) getControl());
				Dialog.applyDialogFont(getControl());
			}

			private void createDialectGroup(Composite control) {
				// TODO Auto-generated method stub
				
			}
		}; 
		creationPage.setTitle(getTitle());
		creationPage.setDescription(getDescription());
		this.addPage(creationPage);
	}
	
	protected abstract String getTitle();
	protected abstract String getDescription();

	@Override
	public boolean performFinish() {
		try {
			IProject project = createNewProject();
			selectAndReveal(project);
			return true;
		} catch (CoreException e) {
			return false;
		}
	}

	private IProject createNewProject() throws CoreException {
		IProject project = getProject();
		IProjectDescription description = buildProjectDescription(project);
		IRunnableWithProgress operation = buildCreateProjectOperation(project, description);
		Exception e = runCreateProjectOperation(project, operation);
		if(e!=null)
			return null;
		project.setDescription(description, null);
		setProjectWorkingSets(project);
		return project;
	}

	private void setProjectWorkingSets(IProject project) {
		IWorkingSet[] workingSets = creationPage.getSelectedWorkingSets();
		getWorkbench().getWorkingSetManager().addToWorkingSets(project, workingSets);
	}

	private Exception runCreateProjectOperation(IProject project, IRunnableWithProgress operation) {
		try {
			getContainer().run(true, true, operation);
			return null;
		} catch (InterruptedException e) {
			return e;
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof ExecutionException && t.getCause() instanceof CoreException) {
				manageCoreExceptionStatus(project, (CoreException) t.getCause());
			} else {
				manageThrowableStatus(project, t);
			}
			return e;
		}
	}

	protected abstract IRunnableWithProgress buildCreateProjectOperation(final IProject project, final IProjectDescription description);
	
	protected IProjectDescription buildProjectDescription(IProject project) throws CoreException {
		URI location = creationPage.useDefaults() ? null : creationPage.getLocationURI();
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
		description.setLocationURI(location);
		description.setNatureIds(new String[] { getNatureId() });
		return description;
	}

	protected abstract String getNatureId();

	private IProject getProject() {
		return creationPage.getProjectHandle();
	}

	private void manageThrowableStatus(IProject projectHandle, Throwable t) {
		IStatus status = new Status( IStatus.WARNING, IDEWorkbenchPlugin.IDE_WORKBENCH, 0,
				"Internal error: " + t.getMessage(), t);
		StatusAdapter adapter = new StatusAdapter(status);
		adapter.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, CoreConstants.NEW_PROJECT_ERROR);
		StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.BLOCK);
	}

	private void manageCoreExceptionStatus(IProject projectHandle, CoreException cause) {
		IStatus status;
		if (cause.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
			status = StatusUtil.newStatus(IStatus.WARNING, CoreConstants.VARIANT_PROJECT_EXISTS, cause);
		} else {
			status = StatusUtil.newStatus(cause.getStatus().getSeverity(), CoreConstants.NEW_PROJECT_ERROR, cause);
		}
		StatusAdapter adapter = new StatusAdapter(status);
		adapter.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, CoreConstants.NEW_PROJECT_ERROR);
		StatusManager.getManager().handle(status, StatusManager.BLOCK);
	}

	protected void createProject(final IProject project, final IProjectDescription description, IProgressMonitor monitor) throws InvocationTargetException {
		try {
			CreateProjectOperation cpo = new CreateProjectOperation(description, CoreConstants.CREATING_PROJECT);
			cpo.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
		} catch (ExecutionException e) {
			throw new InvocationTargetException(e);
		}
		
	}

	protected void createSample(IProject project, IProgressMonitor monitor, String resourceName, String message) throws InvocationTargetException {
		try {
			IFile file = project.getFile(resourceName);
			try(InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("samples/" + resourceName)) {
				CreateFileOperation cfo = new CreateFileOperation(file, null, input, message);
				cfo.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
			}
		} catch (ExecutionException | IOException e) {
			throw new InvocationTargetException(e);
		}
	}

	
}
