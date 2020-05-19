package prompto.ide.content;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import prompto.ide.core.Plugin;
import prompto.ide.utils.ImageUtils;

public class RuntimeFileElement extends LibraryElement {
	
	String fileName;
	
	public RuntimeFileElement(RuntimeLibraryElement parent, String fileName) {
		super(parent);
		this.fileName = fileName;
	}

	@Override
	public String getText() {
		return fileName;
	}

	@Override
	public Image getImage() {
		String ext = fileName.split("\\.")[1];
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
