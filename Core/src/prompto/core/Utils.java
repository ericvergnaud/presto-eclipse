package prompto.core;

import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Combo;

import prompto.declaration.IDeclaration;
import prompto.declaration.IMethodDeclaration;
import prompto.declaration.TestMethodDeclaration;
import prompto.declaration.DeclarationList;
import prompto.parser.Dialect;
import prompto.parser.IParser;

public abstract class Utils {

	public static IWorkspaceRoot getRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	public static enum RunType {
		TEST (null, "pec", "poc", "pmc", "pes", "pos", "pms"),
		SERVER (CoreConstants.SERVER_NATURE_ID, "pec", "poc", "pmc"),
		APPLI (CoreConstants.APPLICATION_NATURE_ID, "pec", "poc", "pmc"),
		SCRIPT (CoreConstants.SCRIPTS_NATURE_ID, "pes", "pos", "pms");
		
		String nature;
		Set<String> supportedExtensions = new HashSet<String>();
		
		RunType(String nature, String ... extensions) {
			this.nature = nature;
			for(String extension : extensions)
				supportedExtensions.add(extension);
		}
		
		public String getNature() {
			return nature;
		}
		
		boolean isSupportedExtension(String ext) {
			return supportedExtensions.contains(ext.toLowerCase());
		}
	}
	
	public static List<IFile> getEligibleFiles(IProject project, RunType type) {
		List<IFile> files = new LinkedList<IFile>();
		if(project!=null)
			getEligibleFiles(project, files,type);
		return files;
	}

	public static void getEligibleFiles(IContainer container, List<IFile> files, RunType type) {
		try {
			for(IResource member : container.members()) {
				if(member instanceof IContainer)
					getEligibleFiles((IContainer)member, files,type);
				else if(member instanceof IFile) {
					IFile file = (IFile)member;
					String ext = file.getFileExtension();
					if(type.isSupportedExtension(ext))
						files.add(file);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
	}

	public static List<IMethodDeclaration> getEligibleMainMethods(IFile file) {
		List<IMethodDeclaration> list = new LinkedList<IMethodDeclaration>();
		if(file!=null) try {
			Dialect dialect = getDialect(file);
			IParser parser = dialect.getParserFactory().newParser();
			String path = file.getFullPath().toPortableString();
			InputStream input = file.getContents();
			DeclarationList all = parser.parse(path, input);
			for(IDeclaration decl : all) {
				if(isEligibleAsMain(decl))
					list.add((IMethodDeclaration)decl);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return list;
	}
	
	public static boolean isEligibleAsMain(IDeclaration declaration) {
		if(!(declaration instanceof IMethodDeclaration))	
			return false;
		return ((IMethodDeclaration)declaration).isEligibleAsMain();
	}

	public static List<TestMethodDeclaration> getEligibleTestMethods(IFile file) {
		List<TestMethodDeclaration> list = new LinkedList<TestMethodDeclaration>();
		if(file!=null) try {
			Dialect dialect = getDialect(file);
			IParser parser = dialect.getParserFactory().newParser();
			String path = file.getFullPath().toPortableString();
			InputStream input = file.getContents();
			DeclarationList all = parser.parse(path, input);
			for(IDeclaration decl : all) {
				if(isEligibleAsTest(decl))
					list.add((TestMethodDeclaration)decl);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return list;
	}
	
	public static boolean isEligibleAsTest(IDeclaration declaration) {
		return declaration instanceof TestMethodDeclaration;
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
