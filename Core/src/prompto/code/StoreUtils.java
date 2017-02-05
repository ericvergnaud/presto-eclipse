package prompto.code;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Version;

import prompto.code.ICodeStore;
import prompto.code.UpdatableCodeStore;
import prompto.core.CoreConstants;
import prompto.nullstore.NullStoreFactory;
import prompto.store.IStore;
import prompto.store.IStoreFactory.Type;
import prompto.utils.ResourceUtils;

public abstract class StoreUtils {

	public static synchronized IEclipseCodeStore fetchStoreFor(IFile file) throws CoreException {
		IProject project = file.getProject();
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
	
	private static ICodeStore getRuntimeCodeStore(IProject project) throws CoreException {
		if(runtimeCodeStore==null) {
			ResourceUtils.registerResourceLister("bundleresource", StoreUtils::listBundleResourcesAt);
			runtimeCodeStore = new UpdatableCodeStore(getNullStore(), project.getName(), Version.emptyVersion.toString());
		}
		return runtimeCodeStore;
	}
	
	private static IStore getNullStore() throws CoreException {
		try {
			return new NullStoreFactory().newStore(null, Type.CODE);
		} catch(Throwable t) {
			throw new CoreException(Status.CANCEL_STATUS);
		}
	}

	private static Collection<String> listBundleResourcesAt(URL url) throws IOException {
		url = FileLocator.resolve(url);
		return ResourceUtils.listResourcesAt(url);
	}

}
