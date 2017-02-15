package prompto.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.eclipse.ui.model.WorkbenchContentProvider;

import prompto.core.CoreConstants;
import prompto.core.LibraryNature;

public class LibraryProjectWizard extends PromptoProjectWizard {
 
	WizardNewProjectLibrariesPage referencePage;
	
	@Override
	protected String getTitle() {
		return CoreConstants.NEW_LIBRARY_PROJECT;
	}
	
	@Override
	protected String getDescription() {
		return CoreConstants.LIBRARY_PROJECT_DESCRIPTION;
	}
	
	@Override
	protected String getNatureId() {
		return CoreConstants.LIBRARY_NATURE_ID;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		referencePage = new WizardNewProjectLibrariesPage();
		this.addPage(referencePage);
	}
	
	@Override
	protected IProjectDescription buildProjectDescription(IProject project) throws CoreException {
		IProjectDescription description = super.buildProjectDescription(project);
		description.setReferencedProjects(referencePage.getReferencedProjects());
		return description;
	}
	
	
	
	private IProject[] getExistingLibraryProjects() {
		List<IProject> libraries = new ArrayList<IProject>();
		for ( IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			try {
				if(project.hasNature(CoreConstants.LIBRARY_NATURE_ID))
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
				createProject(project, description, monitor);
				setExcludeRuntime(project, referencePage.excludeRuntime.getSelection(), monitor);
			}

		};
	}
	
	private void setExcludeRuntime(IProject project, boolean selection, IProgressMonitor monitor) throws InvocationTargetException {
		try {
			project.setPersistentProperty(LibraryNature.EXCLUDE_RUNTIME_PROPERTY, String.valueOf(selection));
		} catch(CoreException e) {
			throw new InvocationTargetException(e);
		}
		
	}

	class WizardNewProjectLibrariesPage extends WizardNewProjectReferencePage {
		
		Button excludeRuntime;
		
		public WizardNewProjectLibrariesPage() {
			super("basicReferenceProjectPage");
			setTitle(CoreConstants.LIBRARY_PROJECT_REFERENCES);
			setDescription(CoreConstants.SELECT_PROJECT_REFERENCES);
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
			label.setText(CoreConstants.LIBRARY_PROJECT_REFERENCES);
			excludeRuntime = new Button(control, SWT.CHECK);
			excludeRuntime.setText("Exclude Prompto Runtime Library");
		}
	}

}
