package presto.content;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

public interface ILibraryElement {

	String getText();
	Image getImage();
	IProject getProject();
	Object getParent();
	Object[] getChildren() throws CoreException;
	boolean hasChildren() throws CoreException;

}
