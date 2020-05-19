package prompto.ide.content;

import org.eclipse.core.resources.IProject;

public abstract class LibraryElement implements ILibraryElement {

	Object parent;
	
	public LibraryElement(Object parent) {
		this.parent = parent;
	}
	
	@Override
	public Object getParent() {
		return parent;
	}
	
	@Override
	public IProject getProject() {
		return ((ILibraryElement)parent).getProject();
	}

}
