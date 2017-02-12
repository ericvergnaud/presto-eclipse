package prompto.content;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

public class ProjectFolderElement extends LibraryElement {

	IFolder folder;
	
	public ProjectFolderElement(ILibraryElement parent, IFolder folder) {
		super(parent);
		this.folder = folder;
	}

	@Override
	public String getText() {
		return folder.getName();
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProject getProject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

}
