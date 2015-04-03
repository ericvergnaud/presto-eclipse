package presto.editor.lang;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import presto.declaration.AttributeDeclaration;
import presto.declaration.CategoryDeclaration;
import presto.declaration.ConcreteCategoryDeclaration;
import presto.declaration.ConcreteMethodDeclaration;
import presto.declaration.IDeclaration;
import presto.declaration.IEnumeratedDeclaration;
import presto.declaration.IMethodDeclaration;
import presto.declaration.TestMethodDeclaration;
import presto.editor.Constants;
import presto.grammar.CategoryMethodDeclarationList;
import presto.grammar.DeclarationList;
import presto.grammar.IdentifierList;
import presto.grammar.Symbol;
import presto.parser.Dialect;
import presto.parser.ErrorCollector;
import presto.parser.IError;
import presto.parser.IErrorListener;
import presto.parser.IParser;
import presto.parser.ISection;
import presto.statement.DeclarationInstruction;
import presto.statement.IStatement;
import presto.statement.StatementList;

public class ContentProvider implements ITreeContentProvider {

	static enum ContentType {
		ATTRIBUTE(Constants.ATTRIBUTE_ICON),
		CATEGORY(Constants.CATEGORY_ICON),
		ENUMERATED(Constants.ENUMERATED_ICON),
		SYMBOL(Constants.SYMBOL_ICON),
		METHOD(Constants.METHOD_ICON);
		
		Image icon;
		
		ContentType(Image icon) {
			this.icon = icon;
		}
		
		public Image getIcon() {
			return icon;
		}
	}
	
	public static class Element {
		Element parent;
		String name;
		ISection section;
		ContentType type;
		Collection<Element> children = new ArrayList<Element>();
		
		@Override
		public String toString() { 
			return name;
		}

		public Image getImage() {
			return type.getIcon();
		}
		
		public ISection getSection() {
			return section;
		}
	}
	
	Dialect dialect;
	IFile file;
	IErrorListener listener;
	IParser parser;
	Element root;
	
	public ContentProvider(Dialect dialect, IFile file) {
		this.dialect = dialect;
		this.file = file;
		this.listener = new ErrorCollector();
		this.parser = dialect.getParserFactory().newParser();
		this.parser.setErrorListener(this.listener);
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(newInput instanceof IDocument) try {
			IDocument doc = (IDocument)newInput;
			inputChanged(doc);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void inputChanged(IDocument document) throws Exception {
		String data = document.get();
		InputStream input = new ByteArrayInputStream(data.getBytes());
		root = parseRoot(file.getName(), input);
		resetErrorMarkers(document);
	}

	private void resetErrorMarkers(final IDocument document) throws CoreException {
		// work on copy of error list in runnable
		final Collection<IError> errors = new ArrayList<IError>(listener.getErrors());
		try { 
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					clearErrorMarkers();
					createErrorMarkers(document, errors);
				}
			}, null);
		} catch (CoreException e) {
			// TODO, but what?
			e.printStackTrace(System.err);
		}
	}

	public void clearErrorMarkers() throws CoreException {
		for(IMarker marker : file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
			if(!marker.exists())
				continue;
			// System.out.println("Removing " + marker.toString());
			marker.delete();
		}
		
	}

	public void createErrorMarkers(final IDocument document, final Collection<IError> errors) throws CoreException {
		for(IError error : errors) {
			if(errorMarkerAlreadyExists(error))
				continue;
			createErrorMarker(document, error);
		}
	}

	private void createErrorMarker(IDocument document, IError error) throws CoreException {
		// no marker found, create one
		IMarker marker = file.createMarker("presto.problem.marker");
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		marker.setAttribute(IMarker.CHAR_START, error.getStartIndex());
		marker.setAttribute(IMarker.CHAR_END, error.getEndIndex());
		marker.setAttribute(IMarker.LINE_NUMBER, safeGetLineOfOffset(document, error.getStartIndex()));
		marker.setAttribute(IMarker.MESSAGE, error.getMessage());
	}

	private int safeGetLineOfOffset(IDocument document, int startIndex) {
		try {
			return 1 + document.getLineOfOffset(startIndex);
		} catch(BadLocationException e) {
			return 1;
		}
	}

	private boolean errorMarkerAlreadyExists(IError error) throws CoreException {
		for(IMarker marker : file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
			if(!marker.exists())
				continue;
			int start = marker.getAttribute(IMarker.CHAR_START, 0);
			int end = marker.getAttribute(IMarker.CHAR_END, 0);
			String msg = marker.getAttribute(IMarker.MESSAGE, "");
			if(start==error.getStartIndex()
				&& end==error.getEndIndex()
				&& msg.equalsIgnoreCase(error.getMessage()))
				return true; // marker already exists
		}
		return false;
	}

	private Element parseRoot(String path, InputStream input) throws Exception {
		DeclarationList list = parser.parse(path, input);
		return populateDeclarationList(list);
	}

	private Element populateDeclarationList(DeclarationList list) {
		Element root = new Element();
		for(IDeclaration decl : list) {
			Element elem = populateDeclaration(decl);
			elem.parent = root;
			root.children.add(elem);
		}
		return root;
	}

	private Element populateDeclaration(IDeclaration decl) {
		if(decl instanceof AttributeDeclaration)
			return populateAttribute((AttributeDeclaration)decl);
		else if(decl instanceof CategoryDeclaration)
			return populateCategory((CategoryDeclaration)decl);
		else if(decl instanceof IEnumeratedDeclaration)
			return populateEnumerated((IEnumeratedDeclaration)decl);
		else if(decl instanceof IMethodDeclaration)
			return populateMethod((IMethodDeclaration)decl);
		else if(decl instanceof TestMethodDeclaration)
			return populateTest((TestMethodDeclaration)decl);
		else
			throw new RuntimeException("Unsupported:" + decl.getClass().getName());
	}

	private Element populateEnumerated(IEnumeratedDeclaration decl) {
		Element elem = new Element();
		elem.name = decl.getName();
		elem.section = decl;
		elem.type = ContentType.ENUMERATED;
		populateSymbols(elem, decl);
		return elem;
	}

	private void populateSymbols(Element elem, IEnumeratedDeclaration decl) {
		for(Symbol s : decl.getSymbols()) {
			Element child = new Element();
			child.name = s.getName();
			child.section = s;
			child.type = ContentType.SYMBOL;
			elem.children.add(child);
		}
	}

	private Element populateMethod(IMethodDeclaration decl) {
		Element elem = new Element();
		elem.name = decl.getName();
		elem.section = decl;
		elem.type = ContentType.METHOD;
		if(decl instanceof ConcreteMethodDeclaration) 
			populateStatements(elem, ((ConcreteMethodDeclaration)decl).getStatements());
		return elem;
}	
	private void populateStatements(Element elem, StatementList statements) {
		for(IStatement s : statements) {
			if(s instanceof DeclarationInstruction) {
				Element child = populateDeclaration(((DeclarationInstruction<?>)s).getDeclaration());
				child.parent = elem;
				elem.children.add(child);
			}
		}
	}
	
	private Element populateTest(TestMethodDeclaration decl) {
		Element elem = new Element();
		elem.name = decl.getName();
		elem.section = decl;
		elem.type = ContentType.METHOD;
		populateStatements(elem, decl.getStatements());
		return elem;
	}

	private Element populateCategory(CategoryDeclaration decl) {
		Element elem = new Element();
		elem.name = decl.getName();
		elem.section = decl;
		elem.type = decl instanceof IEnumeratedDeclaration ? ContentType.ENUMERATED : ContentType.CATEGORY;
		populateInherited(elem, decl.getDerivedFrom());
		if(decl.getAttributes()!=null) for(String name : decl.getAttributes()) {
			Element child = populateAttribute(name);
			child.parent = elem;
			elem.children.add(child);
		}
		if(decl instanceof IEnumeratedDeclaration) {
			populateSymbols(elem, (IEnumeratedDeclaration)decl);
		}
		if(decl instanceof ConcreteCategoryDeclaration) {
			CategoryMethodDeclarationList methods = ((ConcreteCategoryDeclaration)decl).getMethods();
			if(methods!=null) for(IMethodDeclaration method : methods) {
				Element child = populateMethod((IMethodDeclaration)method);
				child.parent = elem;
				elem.children.add(child);
			}
		}
		return elem;
	}
	
	private void populateInherited(Element elem, IdentifierList names) {
		if(names!=null) for(String name : names) {
			Element child = new Element();
			child.name = name;
			child.section = null; // TODO
			child.type = ContentType.CATEGORY;
			elem.children.add(child);
		}
	}

	private Element populateAttribute(AttributeDeclaration decl) {
		Element elem = new Element();
		elem.name = decl.getName();
		elem.section = decl;
		elem.type = ContentType.ATTRIBUTE;
		return elem;
	}
	
	private Element populateAttribute(String name) {
		Element elem = new Element();
		elem.name = name;
		elem.section = null; // TODO name;
		elem.type = ContentType.ATTRIBUTE;
		return elem;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return root==null ? new Object[0] : root.children.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return ((Element)parentElement).children.toArray();
	}

	@Override
	public Object getParent(Object element) {
		return ((Element)element).parent;
	}

	@Override
	public boolean hasChildren(Object element) {
		return !((Element)element).children.isEmpty();
	}

}
