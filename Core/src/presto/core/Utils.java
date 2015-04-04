package presto.core;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Combo;

import presto.declaration.IDeclaration;
import presto.declaration.IMethodDeclaration;
import presto.grammar.DeclarationList;
import presto.parser.Dialect;
import presto.parser.IParser;

public abstract class Utils {

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
			IParser parser = dialect.getParserFactory().newParser();
			String path = file.getFullPath().toPortableString();
			InputStream input = file.getContents();
			DeclarationList all = parser.parse(path, input);
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

	public static String getFilePath(IFile file) {
		return file==null ? null : file.getProjectRelativePath().toPortableString();
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


}
