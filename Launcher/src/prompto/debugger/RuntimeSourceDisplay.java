package prompto.debugger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.ui.sourcelookup.ISourceDisplay;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;

import prompto.code.IEclipseCodeStore;
import prompto.declaration.IDeclaration;
import prompto.declaration.IMethodDeclaration;
import prompto.launcher.Plugin;
import prompto.utils.CodeWriter;
import prompto.utils.StoreUtils;

public class RuntimeSourceDisplay implements ISourceDisplay {

	@Override
	public void displaySource(Object element, IWorkbenchPage page, boolean forceSourceLookup) {
		displaySource((StackFrameProxy)element, page);
	}

	private void displaySource(StackFrameProxy proxy, IWorkbenchPage page) {
		try {
			IFileStore file = getRuntimeFile(proxy);
			IDE.openEditorOnFileStore(page, file);
		} catch(Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	private IFileStore getRuntimeFile(StackFrameProxy proxy) throws CoreException {
		IProject project = proxy.getTarget().getProject();
		IEclipseCodeStore store = StoreUtils.fetchStoreFor(project);
		if(store==null)
			return null;
		else try {
			Iterator<IDeclaration> decls = store.fetchLatestVersions(proxy.getStackFrame().getMethodName());
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
						CodeWriter writer = new CodeWriter(decl.getDialect(), store.getRuntimeContext());
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
		} catch(Exception e) {
			return null;
		}
	}

}
