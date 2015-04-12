package presto.store;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public interface IEclipseCodeStore extends ICodeStore {

	void setProject(IProject project) throws CoreException;
	void setFile(IFile file) throws CoreException;
	Collection<IFile> getFiles() throws CoreException;
}
