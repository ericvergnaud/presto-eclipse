package prompto.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import prompto.declaration.AttributeDeclaration;
import prompto.declaration.CategoryDeclaration;
import prompto.declaration.ConcreteCategoryDeclaration;
import prompto.declaration.ConcreteMethodDeclaration;
import prompto.declaration.IDeclaration;
import prompto.declaration.IEnumeratedDeclaration;
import prompto.declaration.IMethodDeclaration;
import prompto.declaration.TestMethodDeclaration;
import prompto.declaration.DeclarationList;
import prompto.grammar.Identifier;
import prompto.grammar.MethodDeclarationList;
import prompto.grammar.Symbol;
import prompto.parser.Dialect;
import prompto.parser.IParser;
import prompto.parser.ISection;
import prompto.statement.DeclarationInstruction;
import prompto.statement.IStatement;
import prompto.statement.StatementList;
import prompto.utils.IdentifierList;

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
	IParser parser;
	Element root;
	
	public ContentProvider(Dialect dialect, IFile file) {
		this.dialect = dialect;
		this.file = file;
		this.parser = dialect.getParserFactory().newParser();
		this.parser.setProblemListener(null);
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
		if(file!=null) {
			String data = document.get();
			ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
			root = parseRoot(file.getName(), input);
			input.close();
		}
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
		elem.name = decl.getIdentifier().getName();
		elem.section = decl;
		elem.type = ContentType.ENUMERATED;
		populateSymbols(elem, decl);
		return elem;
	}

	private void populateSymbols(Element elem, IEnumeratedDeclaration decl) {
		for(Symbol s : decl.getSymbols()) {
			Element child = new Element();
			child.name = s.getIdentifier().getName();
			child.section = s;
			child.type = ContentType.SYMBOL;
			elem.children.add(child);
		}
	}

	private Element populateMethod(IMethodDeclaration decl) {
		try {
			Element elem = new Element();
			elem.name = decl.getIdentifier().getName();
			elem.section = decl;
			elem.type = ContentType.METHOD;
			if(decl instanceof ConcreteMethodDeclaration) 
				populateStatements(elem, ((ConcreteMethodDeclaration)decl).getStatements());
			return elem;
		} catch(Throwable t) {
			return null;
		}
	}	
	
	private void populateStatements(Element elem, StatementList statements) {
		if(statements==null)
			return;
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
		elem.name = decl.getName().toString();
		elem.section = decl;
		elem.type = ContentType.METHOD;
		populateStatements(elem, decl.getStatements());
		return elem;
	}

	private Element populateCategory(CategoryDeclaration decl) {
		Element elem = new Element();
		elem.name = decl.getName().toString();
		elem.section = decl;
		elem.type = decl instanceof IEnumeratedDeclaration ? ContentType.ENUMERATED : ContentType.CATEGORY;
		populateInherited(elem, decl.getDerivedFrom());
		if(decl.getAttributes()!=null) for(Identifier name : decl.getAttributes()) {
			Element child = populateAttribute(name);
			child.parent = elem;
			elem.children.add(child);
		}
		if(decl instanceof IEnumeratedDeclaration) {
			populateSymbols(elem, (IEnumeratedDeclaration)decl);
		}
		if(decl instanceof ConcreteCategoryDeclaration) {
			MethodDeclarationList methods = ((ConcreteCategoryDeclaration)decl).getMethods();
			if(methods!=null) for(IMethodDeclaration method : methods) {
				if(method!=null) {
					Element child = populateMethod((IMethodDeclaration)method);
					child.parent = elem;
					elem.children.add(child);
				}
			}
		}
		return elem;
	}
	
	private void populateInherited(Element elem, IdentifierList names) {
		if(names!=null) for(Identifier name : names) {
			Element child = new Element();
			child.name = name.toString();
			child.section = name;
			child.type = ContentType.CATEGORY;
			elem.children.add(child);
		}
	}

	private Element populateAttribute(AttributeDeclaration decl) {
		Element elem = new Element();
		elem.name = decl.getName().toString();
		elem.section = decl;
		elem.type = ContentType.ATTRIBUTE;
		return elem;
	}
	
	private Element populateAttribute(Identifier name) {
		Element elem = new Element();
		elem.name = name.toString();
		elem.section = name;
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
