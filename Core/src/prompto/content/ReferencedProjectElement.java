package prompto.content;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import prompto.core.Plugin;
import prompto.utils.ImageUtils;

public class ReferencedProjectElement extends LibraryElement {

	IProject project;
	
	public ReferencedProjectElement(ReferencedProjectsElement root, IProject project) {
		super(root);
		this.project = project;
	}

	@Override
	public Object[] getChildren() throws CoreException {
		List<ILibraryElement> elements = new ArrayList<ILibraryElement>();
		for(IResource member : project.members()) {
			if(member.getName().startsWith("."))
				continue;
			if(member instanceof IFile)
				elements.add(new ProjectFileElement(this, (IFile)member));
			else if(member instanceof IFolder) {
				IFolder folder = (IFolder)member;
				if(folder.members().length>0)
					elements.add(new ProjectFolderElement(this, folder));
			}
		}
		return elements.toArray(new ILibraryElement[elements.size()]);
	}

	@Override
	public boolean hasChildren() throws CoreException {
		return true;
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
