package presto.editor.lang;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import presto.editor.Constants;
import core.parser.Dialect;

public class PresentationReconciler extends org.eclipse.jface.text.presentation.PresentationReconciler {

	public PresentationReconciler(Dialect dialect) {
		IPartitionTokenScanner scanner = PartitionTokenScannerBase.newPartitionTokenScanner(dialect);
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
