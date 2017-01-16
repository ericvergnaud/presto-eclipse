package prompto.store;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;

import prompto.core.CoreConstants;

public abstract class StoreUtils {

	public static synchronized IEclipseCodeStore fetchStoreFor(IFile file) throws CoreException {
		IProject project = file.getProject();
		if(project.hasNature(CoreConstants.SCRIPTS_NATURE_ID))
			return new ScriptCodeStore();
		else {
			QualifiedName key = new QualifiedName(CoreConstants.CORE_PLUGIN_ID, "code_store");
			Object obj = project.getSessionProperty(key);
			if(obj instanceof IEclipseCodeStore)
				return (IEclipseCodeStore)obj;
			if(project.hasNature(CoreConstants.LIBRARY_NATURE_ID))
				obj = new LibraryCodeStore();
			else if(project.hasNature(CoreConstants.SERVER_NATURE_ID))
				obj = new ServerCodeStore();
			else if(project.hasNature(CoreConstants.APPLICATION_NATURE_ID))
				obj = new ApplicationCodeStore();
			else
				throw new CoreException(Status.CANCEL_STATUS);
			project.setSessionProperty(key, obj);
			((IEclipseCodeStore)obj).setProject(project);
			return (IEclipseCodeStore)obj;
		}
	}

}
