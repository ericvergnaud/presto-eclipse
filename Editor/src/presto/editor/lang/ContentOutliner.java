package presto.editor.lang;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import presto.parser.Dialect;

public class ContentOutliner extends ContentOutlinePage implements IDocumentListener {

	Dialect dialect;
	IDocument document;
	
	public ContentOutliner(Dialect dialect, IDocument document) {
		this.dialect = dialect;
		this.document = document;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new ContentProvider(dialect));
		viewer.setLabelProvider(new LabelProvider());
		viewer.addSelectionChangedListener(this);
		viewer.setInput(document);
		document.addDocumentListener(this);
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	@Override
	public void documentChanged(DocumentEvent event) {
		TreeViewer viewer = getTreeViewer();
		viewer.setInput(event.getDocument());
		this.getControl().redraw();
	}

	public IDocument getDocument() {
		return document;
	}
	
}
