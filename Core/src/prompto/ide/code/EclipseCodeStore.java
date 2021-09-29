package prompto.ide.code;

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

import prompto.code.BaseCodeStore;
import prompto.code.Dependency;
import prompto.code.ICodeStore;
import prompto.code.Module;
import prompto.code.ModuleType;
import prompto.code.Resource;
import prompto.declaration.IDeclaration;
import prompto.error.PromptoError;
import prompto.ide.core.CoreConstants;
import prompto.ide.core.RunType;
import prompto.ide.problem.ProblemDetector;
import prompto.ide.utils.CoreUtils;
import prompto.parser.Dialect;
import prompto.parser.ICodeSection;
import prompto.parser.ISection;
import prompto.parser.Location;
import prompto.parser.Section;
import prompto.runtime.Context;
import prompto.intrinsic.PromptoVersion;

public abstract class EclipseCodeStore extends BaseCodeStore implements IEclipseCodeStore {

	Context projectContext = Context.newGlobalsContext();
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
	public ICodeSection findSection(IResource resource, int lineNumber) {
		if(!(resource instanceof IFile))
			return null;
		String path = ((IFile)resource).getFullPath().toPortableString();
		Section section = new Section(path, new Location(0, lineNumber, 0), new Location(0, lineNumber, 0), Dialect.E, false);
		return projectContext.locateCodeSection(section);
	}
	
	@Override
	public ISection findSection(ISection section) {
		ICodeSection cs = projectContext.locateCodeSection(section);
		return cs!=null ? cs.getSection() : null;
	}
	
	
	@Override
	protected Module fetchModule(String name, PromptoVersion version) {
		return null;
	}
	
	@Override
	public Object fetchVersionedModuleDbId(String name, PromptoVersion version) throws PromptoError {
		return null;
	}
	
	@Override
	public <T extends Module> T fetchVersionedModule(ModuleType type, String name, PromptoVersion version) throws PromptoError {
		return null;
	}
	
	@Override
	public Resource fetchVersionedResource(String path, PromptoVersion version) {
		return null;
	}
	
	@Override
	public Iterable<Module> fetchAllModules() throws PromptoError {
		return null;
	}
	
	@Override
	public void storeDeclarations(Iterable<IDeclaration> declarations, Dialect dialect, PromptoVersion version, Object moduleId) throws PromptoError {
	}
	
	@Override
	public Iterable<IDeclaration> fetchDeclarationsWithAnnotations(Set<String> annotations) {
		return null;
	}
	
	@Override
	public void storeDependency(Dependency dependency) {
	}
	
	@Override
	public void dropModule(Module module) {
		
	}
	
	@Override
	public void storeModule(Module module) throws PromptoError {
	}
	
	
	@Override
	public void storeResource(Resource resource, Object moduleId) {
	}
	
	@Override
	protected void doFetchLatestResourcesWithMimeTypes(List<Resource> arg0, Set<String> arg1) {
		// TODO Auto-generated method stub
		
	}


	
}
