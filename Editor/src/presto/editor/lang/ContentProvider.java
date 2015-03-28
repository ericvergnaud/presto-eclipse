package presto.editor.lang;

import java.util.ArrayList;
import java.util.Collection;

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
import presto.editor.Constants;
import presto.grammar.CategoryMethodDeclarationList;
import presto.grammar.DeclarationList;
import presto.grammar.IdentifierList;
import presto.grammar.Symbol;
import presto.parser.Dialect;
import presto.parser.IParser;
import presto.parser.ISection;
import presto.statement.DeclarationInstruction;
import presto.statement.IStatement;

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
	Element root;
	
	public ContentProvider(Dialect dialect) {
		this.dialect = dialect;
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		root = null;
		if(newInput instanceof IDocument) try {
			root = parse(((IDocument)newInput).get());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Element parse(String data) throws Exception {
		IParser parser = dialect.getParserFactory().newParser(data);
		DeclarationList list = parser.parse();
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
		if(decl instanceof ConcreteMethodDeclaration) {
			for(IStatement s : ((ConcreteMethodDeclaration)decl).getStatements()) {
				if(s instanceof DeclarationInstruction) {
					Element child = populateDeclaration(((DeclarationInstruction<?>)s).getDeclaration());
					child.parent = elem;
					elem.children.add(child);
				}
			}
		}
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
