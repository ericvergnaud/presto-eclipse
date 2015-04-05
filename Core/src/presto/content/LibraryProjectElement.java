package presto.content;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import presto.core.Plugin;
import presto.utils.ImageUtils;

public class LibraryProjectElement extends LibraryElement {

	IProject project;
	
	public LibraryProjectElement(LibrariesRootElement root, IProject project) {
		super(root);
		this.project = project;
	}

	@Override
	public Object[] getChildren() {
		// TODO Auto-generated method stub
		return new Object[0];
	}

	@Override
	public boolean hasChildren() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getText() {
		return project.getName();
	}

	@Override
	public Image getImage() {
		return ImageUtils.load(Plugin.getDefault().getBundle(), "images/library.gif");
	}

}
