package presto.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.model.WorkbenchContentProvider;

import presto.core.Constants;

public class LibraryProjectWizard extends PrestoProjectWizard {
 
	WizardNewProjectLibrariesPage referencePage;
	
	@Override
	protected String getTitle() {
		return Constants.NEW_LIBRARY_PROJECT;
	}
	
	@Override
	protected String getDescription() {
		return Constants.LIBRARY_PROJECT_DESCRIPTION;
	}
	
	@Override
	protected String getNatureId() {
		return Constants.LIBRARY_NATURE_ID;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		// only add page if there are already projects in the workspace
		if(getExistingLibraryProjects().length > 0) {
			referencePage = new WizardNewProjectLibrariesPage();
			this.addPage(referencePage);
		}
	}
	
	@Override
	protected IProjectDescription buildProjectDescription(IProject project) throws CoreException {
		IProjectDescription description = super.buildProjectDescription(project);
		if(referencePage!=null)
			description.setReferencedProjects(referencePage.getReferencedProjects());
		return description;
	}
	
	
	
	private IProject[] getExistingLibraryProjects() {
		List<IProject> libraries = new ArrayList<IProject>();
		for ( IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			try {
				if(project.hasNature(Constants.LIBRARY_NATURE_ID))
					libraries.add(project);
			} catch(CoreException e) {
				// TODO: what?
			}
		}
		return libraries.toArray(new IProject[libraries.size()]);
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
	
	class WizardNewProjectLibrariesPage extends WizardNewProjectReferencePage {
		
		public WizardNewProjectLibrariesPage() {
			super("basicReferenceProjectPage");
			setTitle(Constants.LIBRARY_PROJECT_REFERENCES);
			setDescription(Constants.SELECT_PROJECT_REFERENCES);
		}
		
		@Override
		protected IStructuredContentProvider getContentProvider() {
	        return new WorkbenchContentProvider() {
	            public Object[] getChildren(Object element) {
	                if (!(element instanceof IWorkspace)) {
						return new Object[0];
					}
	                return getExistingLibraryProjects();
	            }
	        };
		}
		
		@Override
		public void createControl(Composite parent) {
			super.createControl(parent);
			Composite control = (Composite)getControl();
			Label label = (Label)control.getChildren()[0];
			label.setText(Constants.LIBRARY_PROJECT_REFERENCES);
		}
	}

}
