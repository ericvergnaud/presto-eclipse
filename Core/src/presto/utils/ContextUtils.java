package presto.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import presto.core.Constants;
import presto.runtime.Context;

public abstract class ContextUtils {

	public static Context fetchContext(IFile file) throws CoreException {
		IProject project = file.getProject();
		if(project.hasNature(Constants.SCRIPTS_NATURE_ID))
			return Context.newGlobalContext();
		else {
			QualifiedName key = new QualifiedName(Constants.CORE_PLUGIN_ID, "context");
			Context context = (Context)project.getSessionProperty(key);
			if(context==null) {
				context = Context.newGlobalContext();
				project.setSessionProperty(key, context);
			}
			return context;
		}
	}


}
