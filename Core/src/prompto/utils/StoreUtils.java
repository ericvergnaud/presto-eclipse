package prompto.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Version;

import prompto.code.ApplicationCodeStore;
import prompto.code.ICodeStore;
import prompto.code.IEclipseCodeStore;
import prompto.code.LibraryCodeStore;
import prompto.code.ResourceCodeStore;
import prompto.code.ScriptCodeStore;
import prompto.code.ServerCodeStore;
import prompto.code.UpdatableCodeStore;
import prompto.code.ICodeStore.ModuleType;
import prompto.core.CoreConstants;
import prompto.distribution.Distribution;
import prompto.nullstore.NullStoreFactory;
import prompto.store.IStore;
import prompto.store.IStoreFactory.Type;

public abstract class StoreUtils {

	public static IEclipseCodeStore setStoreFor(IFile file) throws CoreException {
		IEclipseCodeStore store = fetchStoreFor(file);
		ICodeStore.instance.set(store);	
		return store;
	}

	public static synchronized IEclipseCodeStore fetchStoreFor(IFile file) throws CoreException {
		return fetchStoreFor(file.getProject());
	}
	
	public static synchronized IEclipseCodeStore fetchStoreFor(IProject project) throws CoreException {
			if(project.hasNature(CoreConstants.SCRIPTS_NATURE_ID))
			return new ScriptCodeStore(getRuntimeCodeStore(project));
		else {
			QualifiedName key = new QualifiedName(CoreConstants.CORE_PLUGIN_ID, "code_store");
			Object obj = project.getSessionProperty(key);
			if(obj instanceof IEclipseCodeStore)
				return (IEclipseCodeStore)obj;
			if(project.hasNature(CoreConstants.LIBRARY_NATURE_ID))
				obj = new LibraryCodeStore(getRuntimeCodeStore(project));
			else if(project.hasNature(CoreConstants.SERVER_NATURE_ID))
				obj = new ServerCodeStore(getRuntimeCodeStore(project));
			else if(project.hasNature(CoreConstants.APPLICATION_NATURE_ID))
				obj = new ApplicationCodeStore(getRuntimeCodeStore(project));
			else
				throw new CoreException(Status.CANCEL_STATUS);
			project.setSessionProperty(key, obj);
			((IEclipseCodeStore)obj).setProject(project);
			return (IEclipseCodeStore)obj;
		}
	}
	private static ICodeStore runtimeCodeStore = null;
	
	public static ICodeStore getRuntimeCodeStore(IProject project) throws CoreException {
		if(!ProjectUtils.hasRuntime(project))
			return null;
		if(runtimeCodeStore==null)
			runtimeCodeStore = new UpdatableCodeStore(getNullStore(), project.getName(), Version.emptyVersion.toString()) {
				@Override protected ICodeStore bootstrapRuntime() {
					return bootstrapRuntimeFromDistribution();
				}
			};
		return runtimeCodeStore;
	}
	
	private static ICodeStore bootstrapRuntimeFromDistribution() {
		Distribution dist = Distribution.getDefaultDistribution();
		if(dist==null)
			return null;
		File jarFile = Paths.get(dist.getDirectory(), "Runtime-0.0.1-SNAPSHOT.jar").toFile();
		if(!jarFile.exists())
			return null;
		ICodeStore runtime = null;
		try(InputStream input = new FileInputStream(jarFile)) {
			try(ZipInputStream zip = new ZipInputStream(input)) {
				ZipEntry entry = zip.getNextEntry();
				while(entry!=null) {
					if(entry.getName().startsWith("libraries/") && !entry.getName().endsWith("/")) {
						String jarUrl = "jar:" + jarFile.toURI().toURL() + "!/" + entry.getName();
						runtime = new ResourceCodeStore(runtime, ModuleType.LIBRARY, jarUrl, "1.0.0");
					}
					entry = zip.getNextEntry();
				}
			}
		} catch(IOException e) {
			e.printStackTrace(System.err);
		}
		return runtime;
	}
	
	private static IStore getNullStore() throws CoreException {
		try {
			return new NullStoreFactory().newStore(null, Type.CODE);
		} catch(Throwable t) {
			throw new CoreException(Status.CANCEL_STATUS);
		}
	}


}
