package presto.content;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import presto.core.Plugin;
import presto.utils.ImageUtils;

public class LibraryFileElement extends LibraryElement {

	IFile file;
	
	public LibraryFileElement(LibraryProjectElement project, IFile file) {
		super(project);
		this.file = file;
	}

	@Override
	public String getText() {
		return file.getName();
	}

	@Override
	public Image getImage() {
		String ext = file.getFileExtension();
		return ImageUtils.load(Plugin.getDefault().getBundle(), "images/" + ext + "_obj.gif");
	}

	@Override
	public Object[] getChildren() throws CoreException {
		// TODO Auto-generated method stub
		return new Object[0];
	}

	@Override
	public boolean hasChildren() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

}
