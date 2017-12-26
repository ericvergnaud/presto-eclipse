package prompto.code;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import prompto.core.CoreConstants;
import prompto.core.RunType;
import prompto.declaration.IDeclaration;
import prompto.error.PromptoError;
import prompto.parser.Dialect;
import prompto.parser.ISection;
import prompto.problem.ProblemDetector;
import prompto.runtime.Context;
import prompto.utils.CoreUtils;
import prompto.intrinsic.PromptoVersion;

public abstract class EclipseCodeStore extends BaseCodeStore implements IEclipseCodeStore {

	Context projectContext = Context.newGlobalContext();
	Set<IFile> files = Collections.newSetFromMap(new ConcurrentHashMap<IFile, Boolean>()); // creates a concurrent set
	
	protected EclipseCodeStore(ICodeStore runtime) {
		super(runtime);
		registerRuntimeDeclarations(runtime);
	}
	
	private void registerRuntimeDeclarations(ICodeStore runtime) {
		if(runtime!=null) {
			// only called from synchronized StoreUtils.fetchStoreFor 
			ICodeStore.instance.set(this);
			projectContext.fetchAndRegisterAllDeclarations();
		}
	}

	@Override
	public Dialect getModuleDialect() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getModuleName() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ModuleType getModuleType() {
		throw new UnsupportedOperationException();
	}

	
	@Override
	public PromptoVersion getModuleVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Context getProjectContext() {
		return projectContext;
	}
	
	@Override
	public Collection<IFile> getFiles() throws CoreException {
		return files;
	}

	@Override
	public void setProject(IProject project) throws CoreException {
		Set<IProject> projects = new HashSet<IProject>();
		collectProjectLibraries(projects, project);
		collectProjectFiles(projects);
	}
	
	private void collectProjectLibraries(Set<IProject> projects, IProject project) throws CoreException {
		if(!projects.contains(project))
			projects.add(project);
		for(IProject library : project.getReferencedProjects()) {
			if(!projects.contains(library) && library.hasNature(CoreConstants.LIBRARY_NATURE_ID))
				collectProjectLibraries(projects, library);
		}
	}

	private void collectProjectFiles(Set<IProject> projects) {
		for(IProject project : projects)
			collectProjectFiles(project);
	}

	private void collectProjectFiles(IProject project) {
		Set<IFile> files = CoreUtils.getEligibleFiles(project, RunType.APPLI);
		for(IFile file : files)
			this.files.add(file);
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
		return projectContext.findSectionFor(path, lineNumber);
	}
	
	@Override
	public ISection findSection(ISection section) {
		return projectContext.findSection(section);
	}
	
	@Override
	public <T extends Module> T fetchModule(ModuleType type, String name, PromptoVersion version) throws PromptoError {
		return null;
	}
	
	@Override
	public Resource fetchSpecificResource(String path, PromptoVersion version) {
		return null;
	}
	
	@Override
	public void storeDeclarations(Iterable<IDeclaration> declarations, Dialect dialect, PromptoVersion version, Object moduleId) throws PromptoError {
	}
	
	@Override
	public void storeModule(Module module) throws PromptoError {
	}
	
	
	@Override
	public void storeResource(Resource resource, Object moduleId) {
	}
	
}
