package presto.editor.e;

import presto.editor.lang.DocumentSetupParticipantBase;
import presto.editor.lang.PartitionTokenScannerBase;
import presto.parser.Dialect;

public class DocumentSetupParticipant extends DocumentSetupParticipantBase {

	public DocumentSetupParticipant() {
		super(Dialect.E);
	}

	@Override
	protected PartitionTokenScannerBase newPartitionScanner() {
		return new PartitionTokenScanner();
	}


}
