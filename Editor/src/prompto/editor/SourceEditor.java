package prompto.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;




// import prompto.eclipse.plugin.lang.FormattingStrategy;
import prompto.parser.Dialect;
import prompto.parser.ISection;
import prompto.editor.ContentProvider.Element;
import prompto.editor.prefs.SyntaxColoring;
import prompto.ide.problem.ProblemDetector;

public class SourceEditor extends AbstractDecoratedTextEditor {

	Dialect dialect;
	MultiPageEditor parent;
	ContentOutliner outliner;
	
	public SourceEditor(Dialect dialect, MultiPageEditor parent) {
		this.dialect = dialect;
		this.parent = parent;
	}
	
	public Dialect getDialect() {
		return dialect;
	}
	
	public SourceEditor getActualSourceEditor() {
		return parent.getActualSourceEditor();
	}
	
	@Override
	public boolean isEditable() {
		if(getEditorInput() instanceof TranslatedInput)
			return false;
		else
			return super.isEditable();
	}
	
	@Override
	public boolean isDirty() {
		if(getEditorInput() instanceof TranslatedInput)
			return false;
		else
			return super.isDirty();
	}
	
	@Override
	protected void setDocumentProvider(IEditorInput input) {
		if(input instanceof TranslatedInput)
			setDocumentProvider(new TranslatedDocumentProvider());
		else
			super.setDocumentProvider(input);
	}
	
	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		partition(input);
		initializeViewer(input);
		initializeOutliner(input);
		initializeProblemDetector(input);
	}

	private void initializeOutliner(IEditorInput input) {
		IFile file = getFile(input);
		IDocument document = getDocumentProvider().getDocument(input);
		outliner = new ContentOutliner(dialect, file, document);
		outliner.addSelectionChangedListener(new OutlineSelectionChangedListener());
	}
	
	private void initializeProblemDetector(IEditorInput input) {
		IFile file = getFile(input);
		IDocument document = getDocumentProvider().getDocument(input);
		document.addDocumentListener(new ProblemDetectorListener(file));
	}
	
	static class ProblemDetectorListener implements IDocumentListener {

		IFile file;
		
		public ProblemDetectorListener(IFile file) {
			this.file = file;
		}
	
		@Override public void documentAboutToBeChanged(DocumentEvent event) {}
		
		@Override
		public void documentChanged(DocumentEvent event) {
			if(file!=null) try {
				ProblemDetector.documentChanged(file, event.getDocument());
			} catch(Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}

	private IFile getFile(IEditorInput input) {
		if(input instanceof IFileEditorInput)
			return ((IFileEditorInput)input).getFile();
		else
			return null;
	}

	class OutlineSelectionChangedListener implements ISelectionChangedListener {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			ContentOutliner outliner = (ContentOutliner)event.getSource();
			TreeSelection selection = (TreeSelection)event.getSelection();
			Object selected = selection.getFirstElement();
			if(selected instanceof Element) {
				ISection s = ((Element)selected).getSection();
				if(s!=null) try {
					select(outliner.getDocument(), s);
				} catch(Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	public void select(IDocument doc, ISection s) throws BadLocationException {
		int start = doc.getLineOffset(s.getStart().getLine()-1) + s.getStart().getColumn();
		int end = doc.getLineOffset(s.getEnd().getLine()-1) + s.getEnd().getColumn();
		this.selectAndReveal(start, end - start);
	}

	private void initializeViewer(IEditorInput input) {
		SourceViewerConfiguration config = new SourceViewerConfiguration(dialect);
		setSourceViewerConfiguration(config);
		ContentFormatter formatter = config.getContentFormatter(getSourceViewer());
		formatter.setDocumentPartitioning(Constants.PARTITION_ID);
/*		FormattingStrategy strategy = new FormattingStrategy();
		for(String name : PromptoConstants.PARTITION_NAMES)
			formatter.setFormattingStrategy(strategy, name); */
	}

	@Override
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		if(SyntaxColoring.isAffectedByProperty(event.getProperty()))
			return true;
		else
			return super.affectsTextPresentation(event);
	};
	
	private void partition(IEditorInput input) {
		final IDocument doc = getDocumentProvider().getDocument(input);
		partition(doc);
	}

	private void partition(IDocument doc) {
		if(doc instanceof IDocumentExtension3) {
			IDocumentExtension3 ext = (IDocumentExtension3)doc;
			if(ext.getDocumentPartitioner(Constants.PARTITION_ID)==null) {
				IDocumentSetupParticipant participant = new DocumentSetupParticipant(dialect);
				participant.setup(doc);
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T getAdapter(Class<T> klass) {
		if(IContentOutlinePage.class==klass)
			return (T)outliner;
		else
			return super.getAdapter(klass);
	}

	public IDocument getDocument() {
		return getDocumentProvider().getDocument(getEditorInput());
	}

	public IFile getFile() {
		IEditorInput input = getEditorInput();
		if(input instanceof IFileEditorInput)
			return ((IFileEditorInput)input).getFile();
		else
			return null;
	}

}
