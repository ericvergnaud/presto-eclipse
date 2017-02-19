package prompto.content;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import prompto.utils.ProjectUtils;

public class ContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		try {
			if(parentElement instanceof IProject) {
				List<Object> children = new ArrayList<>();
				children.add(new ReferencedProjectsElement((IProject)parentElement));
				if(ProjectUtils.hasRuntime((IProject)parentElement))
					children.add(new RuntimeLibraryElement((IProject)parentElement));
				return children.toArray();
			} else if(parentElement instanceof ILibraryElement) {
				return ((ILibraryElement)parentElement).getChildren();
			}
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
