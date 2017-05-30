package prompto.locator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

import prompto.code.IEclipseCodeStore;
import prompto.debugger.StackFrameProxy;
import prompto.declaration.IDeclaration;
import prompto.declaration.IMethodDeclaration;
import prompto.launcher.Plugin;
import prompto.utils.CodeWriter;
import prompto.utils.StoreUtils;

public class RuntimeSourceLookup implements ISourceLookupParticipant {

	@SuppressWarnings("unused")
	private ISourceLookupDirector fDirector;

	@Override
	public void init(ISourceLookupDirector director) {
		fDirector = director;
	}
	
	@Override
	public void dispose() {
		fDirector = null;
	}
	
	@Override
	public void sourceContainersChanged(ISourceLookupDirector director) {
	}
	
	@Override
	public Object[] findSourceElements(Object object) throws CoreException {
		if(object instanceof StackFrameProxy)
			return findSource((StackFrameProxy)object);
		else
			return null;
	}
	
	@Override
	public String getSourceName(Object object) throws CoreException {
		if(object instanceof IFileStore)
			return ((IFileStore)object).getName();
		else
			return null;
	}
	
	private Object[] findSource(StackFrameProxy proxy) {
		if("__store__".equals(proxy.getStackFrame().getFilePath())) {
			IFileStore file = getRuntimeFile(proxy);
			if(file!=null)
				return new Object[] { file };
		}
		return null;
	}

	private IFileStore getRuntimeFile(StackFrameProxy proxy) {
		try {
			IProject project = proxy.getTarget().getProject();
			IEclipseCodeStore store = StoreUtils.fetchStoreFor(project);
			if(store==null)
				return null;
			else {
				Iterator<IDeclaration> decls = store.fetchLatestDeclarations(proxy.getStackFrame().getMethodName());
				while(decls.hasNext()) {
					IDeclaration decl = decls.next();
					if(decl instanceof IMethodDeclaration) {
						// TODO manage proto
						String entryPath = decl.getFilePath();
						IPath path = Plugin.getDefault()
								.getStateLocation()
								.addTrailingSeparator()
								.append(project.getName())
								.addTrailingSeparator()
								.append("runtime")
								.addTrailingSeparator()
								.append(proxy.getStackFrame().getMethodName())
								.addFileExtension(entryPath.substring(entryPath.lastIndexOf('.')+1));
						File file = path.toFile();
						if(!file.exists()) {
							CodeWriter writer = new CodeWriter(decl.getDialect(), store.getProjectContext());
							decl.toDialect(writer);
							file.getParentFile().mkdirs();
							try(OutputStream out = new FileOutputStream(file)) {
								out.write(writer.toString().getBytes());
							}
						}
						return EFS.getStore(file.toURI());
					}
				}
				return null;
			}
		} catch(Exception e) {
			return null;
		}
	}

}
