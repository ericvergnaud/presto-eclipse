package presto.launcher;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Combo;

import presto.declaration.IDeclaration;
import presto.declaration.IMethodDeclaration;
import presto.grammar.DeclarationList;
import presto.parser.Dialect;
import presto.parser.IParser;

public class Utils {

	public static IWorkspaceRoot getRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	public static List<IFile> getEligibleFiles(IProject project) {
		List<IFile> files = new LinkedList<IFile>();
		if(project!=null)
			getEligibleFiles(project, files);
		return files;
	}

	public static void getEligibleFiles(IContainer container, List<IFile> files) {
		try {
			for(IResource member : container.members()) {
				if(member instanceof IContainer)
					getEligibleFiles((IContainer)member, files);
				else if(member instanceof IFile) {
					IFile file = (IFile)member;
					String ext = file.getFileExtension().toLowerCase();
					if("ped".equals(ext) || "pod".equals(ext) || "ppd".equals(ext))
						files.add(file);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
	}

	public static List<IMethodDeclaration> getEligibleMethods(IFile file) {
		List<IMethodDeclaration> list = new LinkedList<IMethodDeclaration>();
		if(file!=null) try {
			Dialect dialect = getDialect(file);
			IParser parser = dialect.getParserFactory().newParser(file.getFullPath().toPortableString(),
					file.getContents());
			DeclarationList all = parser.parse();
			for(IDeclaration decl : all) {
				if(isEligible(decl))
					list.add((IMethodDeclaration)decl);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return list;
	}
	
	public static boolean isEligible(IDeclaration declaration) {
		if(!(declaration instanceof IMethodDeclaration))	
			return false;
		return ((IMethodDeclaration)declaration).isEligibleAsMain();
	}

	public static Dialect getDialect(IFile file) {
		return Dialect.valueOf(file.getFileExtension().substring(1, 2).toUpperCase());
	}

	public static IProject getConfiguredProject(ILaunchConfiguration configuration) {
		try {
			String name = configuration.getAttribute(Constants.PROJECT, "");
			return name.length()>0 ? getRoot().getProject(name) : null;
		} catch (CoreException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
	
	public static String getFilePath(IFile file) {
		return file==null ? null : file.getProjectRelativePath().toPortableString();
	}


	public static IFile getConfiguredFile(ILaunchConfiguration configuration, IProject project) {
		try {
			String path = configuration.getAttribute(Constants.FILE, "");
			if(!path.isEmpty()) {
				for(IFile file : getEligibleFiles(project)) {
					if(path.equals(getFilePath(file)))
						return file;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}
	
	public static void selectInCombo(Combo combo, String name) {
		if(!name.isEmpty()) for(int i=0; i<combo.getItems().length; i++) {
			if(name.equals(combo.getItem(i))) {
				combo.select(i);
				return;
			}
		}
	}

	public static String getMethodSignature(IMethodDeclaration method, Dialect dialect) {
		return method==null ? null : method.getSignature(dialect);
	}

	public static IMethodDeclaration getConfiguredMethod( ILaunchConfiguration configuration, IFile file) {
		try {
			String signature = configuration.getAttribute(Constants.METHOD, "");
			if(!signature.isEmpty()) {
				Dialect dialect = getDialect(file);
				for(IMethodDeclaration method : Utils.getEligibleMethods(file)) {
					if(signature.equals(method.getSignature(dialect))) 
						return method;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static String getConfiguredCommandLineArguments(ILaunchConfiguration configuration) {
		try {
			return configuration.getAttribute(Constants.ARGUMENTS, "");
		} catch (CoreException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	public static boolean getConfiguredStopInMain(ILaunchConfiguration configuration) {
		try {
			return configuration.getAttribute(Constants.STOP_IN_MAIN, true);
		} catch (CoreException e) {
			e.printStackTrace(System.err);
			return true;
		}
	}




}
