package prompto.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import prompto.parser.Dialect;
import prompto.editor.Constants;

public class PresentationReconciler extends org.eclipse.jface.text.presentation.PresentationReconciler {

	public PresentationReconciler(Dialect dialect) {
		IPartitionTokenScanner scanner = new PartitionTokenScanner(dialect);
		this.setDocumentPartitioning(Constants.PARTITION_ID);
		DamagerRepairer dr = new DamagerRepairer(scanner);
		this.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		this.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		for(String name : Constants.PARTITION_NAMES) {
			this.setDamager(dr, name);
			this.setRepairer(dr, name);
		}
	}

}
