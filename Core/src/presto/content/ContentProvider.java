package presto.content;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IProject)
			return new Object[] { new LibrariesRootElement((IProject)parentElement) };
		else if(parentElement instanceof ILibraryElement) try {
			return ((ILibraryElement)parentElement).getChildren();
		} catch( CoreException e) {
			// TODO: what
		}
		return new Object[0];
	}
	
	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof IProject)
			return true;
		else if(element instanceof ILibraryElement) try {
			return ((ILibraryElement)element).hasChildren();
		} catch( CoreException e) {
			// TODO: what
		}
		return false;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		System.out.println("getElements");
		return new Object[0];
	}
	
	@Override
	public Object getParent(Object element) {
		if(element instanceof ILibraryElement)
			return ((ILibraryElement)element).getParent();
		else
			return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
