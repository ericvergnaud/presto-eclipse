package prompto.content;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import prompto.core.Plugin;
import prompto.utils.ImageUtils;

public class ProjectFileElement extends LibraryElement {

	IFile file;
	
	public ProjectFileElement(ReferencedProjectElement project, IFile file) {
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
		return new Object[0];
	}

	@Override
	public boolean hasChildren() throws CoreException {
		return false;
	}

}
