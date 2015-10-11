package prompto.editor;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import prompto.parser.Dialect;
import prompto.problem.ProblemManager;

public class ContentOutliner extends ContentOutlinePage implements IDocumentListener {

	Dialect dialect;
	IFile file;
	IDocument document;
	
	public ContentOutliner(Dialect dialect, IFile file, IDocument document) {
		this.dialect = dialect;
		this.file = file;
		this.document = document;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new ContentProvider(dialect, file));
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
		Control control = viewer.getControl();
		if(control!=null && !control.isDisposed()) {
			viewer.setInput(event.getDocument());
			this.getControl().redraw();
		}
		if(file!=null) try {
			String data = event.getDocument().get();
			ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
			ProblemManager.processFile(file, input);
			input.close();
		} catch(Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public IDocument getDocument() {
		return document;
	}
	
}
