package presto.editor.lang;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import presto.editor.Constants;
import core.grammar.AttributeDeclaration;
import core.grammar.CategoryDeclaration;
import core.grammar.CategoryMethodDeclarationList;
import core.grammar.ConcreteCategoryDeclaration;
import core.grammar.ConcreteMethodDeclaration;
import core.grammar.Declaration;
import core.grammar.DeclarationInstruction;
import core.grammar.DeclarationList;
import core.grammar.EnumeratedDeclaration;
import core.grammar.Identifier;
import core.grammar.IdentifierList;
import core.grammar.MethodDeclaration;
import core.grammar.Statement;
import core.grammar.Symbol;
import core.parser.Dialect;
import core.parser.IParser;
import core.parser.ISection;

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
		for(Declaration decl : list) {
			Element elem = populateDeclaration(decl);
			elem.parent = root;
			root.children.add(elem);
		}
		return root;
	}

	private Element populateDeclaration(Declaration decl) {
		if(decl instanceof AttributeDeclaration)
			return populateAttribute((AttributeDeclaration)decl);
		else if(decl instanceof CategoryDeclaration)
			return populateCategory((CategoryDeclaration)decl);
		else if(decl instanceof EnumeratedDeclaration)
			return populateEnumerated((EnumeratedDeclaration)decl);
		else if(decl instanceof MethodDeclaration)
			return populateMethod((MethodDeclaration)decl);
		else
			throw new RuntimeException("Unsupported:" + decl.getClass().getName());
	}

	private Element populateEnumerated(EnumeratedDeclaration decl) {
		Element elem = new Element();
		elem.name = decl.getName();
		elem.section = decl;
		elem.type = ContentType.ENUMERATED;
		populateSymbols(elem, decl);
		return elem;
	}

	private void populateSymbols(Element elem, EnumeratedDeclaration decl) {
		for(Symbol s : decl.getSymbols()) {
			Element child = new Element();
			child.name = s.getName();
			child.section = s;
			child.type = ContentType.SYMBOL;
			elem.children.add(child);
		}
	}

	private Element populateMethod(MethodDeclaration decl) {
		Element elem = new Element();
		elem.name = decl.getName();
		elem.section = decl;
		elem.type = ContentType.METHOD;
		if(decl instanceof ConcreteMethodDeclaration) {
			for(Statement s : ((ConcreteMethodDeclaration)decl).getStatements()) {
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
		elem.type = decl instanceof EnumeratedDeclaration ? ContentType.ENUMERATED : ContentType.CATEGORY;
		populateInherited(elem, decl.getDerivedFrom());
		if(decl.getAttributes()!=null) for(Identifier name : decl.getAttributes()) {
			Element child = populateAttribute(name);
			child.parent = elem;
			elem.children.add(child);
		}
		if(decl instanceof EnumeratedDeclaration) {
			populateSymbols(elem, (EnumeratedDeclaration)decl);
		}
		if(decl instanceof ConcreteCategoryDeclaration) {
			CategoryMethodDeclarationList methods = ((ConcreteCategoryDeclaration)decl).getMethods();
			if(methods!=null) for(MethodDeclaration method : methods) {
				Element child = populateMethod((MethodDeclaration)method);
				child.parent = elem;
				elem.children.add(child);
			}
		}
		return elem;
	}
	
	private void populateInherited(Element elem, IdentifierList names) {
		if(names!=null) for(Identifier name : names) {
			Element child = new Element();
			child.name = name.getName();
			child.section = name;
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
	
	private Element populateAttribute(Identifier name) {
		Element elem = new Element();
		elem.name = name.getName();
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
