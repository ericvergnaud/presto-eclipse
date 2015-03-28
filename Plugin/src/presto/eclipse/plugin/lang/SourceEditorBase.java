package presto.eclipse.plugin.lang;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import presto.eclipse.plugin.PrestoConstants;
import presto.eclipse.plugin.PrestoPlugin;
import presto.eclipse.plugin.lang.ContentProvider.Element;
import presto.eclipse.prefs.SyntaxColoring;
import core.grammar.Section;
// import presto.eclipse.plugin.lang.FormattingStrategy;
import core.runtime.Dialect;

public abstract class SourceEditorBase extends AbstractDecoratedTextEditor {

	Dialect dialect;
	ContentOutliner outliner;
	
	public SourceEditorBase(Dialect dialect) {
		this.dialect = dialect;
	}
	
	@Override
	protected void initializeEditor() {
		setPreferenceStore(PrestoPlugin.getPreferenceStore());
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		partition(input);
		initializeViewer(input);
		initializeOutliner(input);
	}

	private void initializeOutliner(IEditorInput input) {
		IDocument document = getDocumentProvider().getDocument(input);
		outliner = new ContentOutliner(dialect, document);
		outliner.addSelectionChangedListener(new OutlineSelectionChangedListener());
	}
	
	class OutlineSelectionChangedListener implements ISelectionChangedListener {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			ContentOutliner outliner = (ContentOutliner)event.getSource();
			TreeSelection selection = (TreeSelection)event.getSelection();
			Object selected = selection.getFirstElement();
			if(selected instanceof Element) {
				Section s = ((Element)selected).getSection();
				if(s!=null) try {
					select(outliner.getDocument(), s);
				} catch(Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	public void select(IDocument doc, Section s) throws BadLocationException {
		int start = doc.getLineOffset(s.start.line-1) + s.start.column;
		int end = doc.getLineOffset(s.end.line-1) + s.end.column;
		this.selectAndReveal(start, end - start);
	}

	private void initializeViewer(IEditorInput input) {
		SourceViewerConfiguration config = new SourceViewerConfiguration(dialect);
		setSourceViewerConfiguration(config);
		ContentFormatter formatter = config.getContentFormatter(getSourceViewer());
		formatter.setDocumentPartitioning(PrestoConstants.PARTITION_ID);
/*		FormattingStrategy strategy = new FormattingStrategy();
		for(String name : PrestoConstants.PARTITION_NAMES)
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
			if(ext.getDocumentPartitioner(PrestoConstants.PARTITION_ID)==null) {
				IDocumentSetupParticipant participant = newDocumentSetupParticipant();
				participant.setup(doc);
			}
		}
	}

	protected abstract IDocumentSetupParticipant newDocumentSetupParticipant();
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class klass) {
		if(IContentOutlinePage.class==klass)
			return outliner;
		else
			return super.getAdapter(klass);
	}

}
