package presto.editor.lang;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import presto.editor.Constants;
import core.parser.Dialect;

public abstract class DocumentSetupParticipantBase implements IDocumentSetupParticipant {

	Dialect dialect;
	
	public DocumentSetupParticipantBase(Dialect dialect) {
		this.dialect = dialect;
	}
	
	@Override
	public void setup(IDocument document) {
		IDocumentPartitioner partitioner = newDocumentPartitioner();
		if (partitioner != null) {
			partitioner.connect(document);
			if (document instanceof IDocumentExtension3) {
				IDocumentExtension3 extension3 = (IDocumentExtension3) document;
				extension3.setDocumentPartitioner(Constants.PARTITION_ID, partitioner);
			} else {
				document.setDocumentPartitioner(partitioner);
			}
		}
	}

	protected IDocumentPartitioner newDocumentPartitioner() {
		IPartitionTokenScanner scanner = newPartitionScanner();
		return new FastPartitioner(scanner, Constants.PARTITION_NAMES);
	}

	protected abstract IPartitionTokenScanner newPartitionScanner();

}
