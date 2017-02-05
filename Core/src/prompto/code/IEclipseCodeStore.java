package prompto.code;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import prompto.code.ICodeStore;
import prompto.parser.ISection;
import prompto.runtime.Context;

public interface IEclipseCodeStore extends ICodeStore {

	void setProject(IProject project) throws CoreException;
	void setFile(IFile file) throws CoreException;
	Collection<IFile> getFiles() throws CoreException;
	ISection findSection(IResource resource, int lineNumber);
	Context getContext();
}
