package prompto.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import prompto.core.CoreConstants;

public class ScriptsProjectWizard extends PromptoProjectWizard {
 

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
				createProject(project, description, monitor);;
				createSample(project, monitor, "script.pec", CoreConstants.CREATING_SAMPLE_SCRIPT);
			}

		};
	}

}
