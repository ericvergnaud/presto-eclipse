package presto.content;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import presto.core.Constants;
import presto.core.Plugin;
import presto.utils.ImageUtils;

public class LibrariesRootElement extends LibraryElement {

	public LibrariesRootElement(IProject parent) {
		super(parent);
	}
	
	@Override
	public IProject getProject() {
		return (IProject)getParent();
	}
	
	@Override
	public String getText() {
		return "Presto Libraries";
	}	
	
	@Override
	public Image getImage() {
		return ImageUtils.load(Plugin.getDefault().getBundle(), "images/library.gif");
	}
	
	@Override
	public boolean hasChildren() throws CoreException {
		IProject project = getProject();
		if(project.hasNature(Constants.LIBRARY_NATURE_ID))
			return project.getReferencedProjects().length>0;
		else
			return true;
	}

	@Override
	public Object[] getChildren() throws CoreException {
		IProject project = getProject();
		if(project.hasNature(Constants.LIBRARY_NATURE_ID))
			return getReferencedLibraries(project);
		else
			return getPrestoLibraries();
	}
	
	private Object[] getPrestoLibraries() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object[] getReferencedLibraries(IProject project) throws CoreException {
		// double check that referenced projects are Presto libraries
		List<ILibraryElement> libraries = new ArrayList<ILibraryElement>();
		for(IProject ref : project.getReferencedProjects()) {
			if(ref.hasNature(Constants.LIBRARY_NATURE_ID))
				libraries.add(new LibraryProjectElement(this, ref));
		}
		return libraries.toArray(new ILibraryElement[libraries.size()]);
	}
}
