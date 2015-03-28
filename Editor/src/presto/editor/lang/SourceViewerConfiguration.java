package presto.editor.lang;

import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import presto.editor.Constants;
import presto.parser.Dialect;

public class SourceViewerConfiguration extends TextSourceViewerConfiguration {

	ContentFormatter formatter = new ContentFormatter();
	Dialect dialect;
	
	public SourceViewerConfiguration(Dialect dialect) {
		this.dialect = dialect;
	}
	
	@Override
	public ContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		return formatter;
	}
	
	@Override
	public IPresentationReconciler getPresentationReconciler( ISourceViewer sourceViewer) {
		return new PresentationReconciler(dialect);
	}
	
	@Override
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return Constants.PARTITION_ID;
	}
	
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return Constants.PARTITION_NAMES;
	}
}
 