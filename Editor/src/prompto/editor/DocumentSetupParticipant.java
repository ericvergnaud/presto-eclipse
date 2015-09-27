package prompto.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

import prompto.parser.Dialect;
import prompto.editor.Constants;

public class DocumentSetupParticipant implements IDocumentSetupParticipant {

	protected Dialect dialect;
	
	public DocumentSetupParticipant(Dialect dialect) {
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
		PartitionTokenScanner scanner = new PartitionTokenScanner(dialect);
		return new FastPartitioner(scanner,Constants.PARTITION_NAMES);
	}

}
