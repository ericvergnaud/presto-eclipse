package prompto.store;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import prompto.core.CoreConstants;
import prompto.core.Utils;
import prompto.core.Utils.RunType;
import prompto.declaration.IDeclaration;
import prompto.parser.ISection;
import prompto.problem.ProblemDetector;
import prompto.runtime.Context;

public abstract class EclipseCodeStore implements IEclipseCodeStore {

	Context context = Context.newGlobalContext();
	Set<IFile> files = Collections.newSetFromMap(new ConcurrentHashMap<IFile, Boolean>()); // creates a concurrent set
	
	@Override
	public IDeclaration fetch(String name) {
		// currently, an eclipse code store can only access workspace files
		// all declarations are already registered in the context
		return null;
	}
	
	@Override
	public Context getContext() {
		return context;
	}
	
	@Override
	public Collection<IFile> getFiles() throws CoreException {
		return files;
	}

	@Override
	public void setProject(IProject project) throws CoreException {
		Set<IProject> projects = new HashSet<IProject>();
		collectPromptoLibraries();
		collectProjectLibraries(projects, project);
		collectProjectFiles(projects);
	}
	
	private void collectProjectLibraries(Set<IProject> projects, IProject project) throws CoreException {
		if(projects.contains(project))
			return;
		projects.add(project);
		for(IProject library : project.getReferencedProjects()) {
			if(library.hasNature(CoreConstants.LIBRARY_NATURE_ID)) {
				collectProjectLibraries(projects, library);
			}
		}
	}

	private void collectProjectFiles(Set<IProject> projects) {
		for(IProject project : projects)
			collectProjectFiles(project);
	}

	private void collectProjectFiles(IProject project) {
		List<IFile> files = Utils.getEligibleFiles(project, RunType.APPLI);
		for(IFile file : files)
			this.files.add(file);
	}

	private void collectPromptoLibraries() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFile(IFile file) throws CoreException {
		files.add(file);
		ProblemDetector.fileAdded(file);
	}

	@Override
	public ISection findSection(IResource resource, int lineNumber) {
		if(!(resource instanceof IFile))
			return null;
		String path = ((IFile)resource).getFullPath().toPortableString();
		return context.findSectionFor(path, lineNumber);
	}
}
