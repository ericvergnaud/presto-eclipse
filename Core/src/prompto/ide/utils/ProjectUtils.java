package prompto.ide.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import prompto.ide.core.CoreConstants;
import prompto.ide.core.LibraryNature;

public abstract class ProjectUtils {

	public static boolean hasRuntime(IProject project) throws CoreException {
		if(project.hasNature(CoreConstants.LIBRARY_NATURE_ID)) {
			Object prop = project.getPersistentProperty(LibraryNature.EXCLUDE_RUNTIME_PROPERTY);
			if(prop!=null && Boolean.valueOf(prop.toString()))
				return false;	
		}
		return true;
	}

}
