package presto.editor.p;

import presto.editor.base.DocumentSetupParticipantBase;
import presto.editor.base.PartitionTokenScannerBase;
import presto.parser.Dialect;

public class DocumentSetupParticipant extends DocumentSetupParticipantBase {

	public DocumentSetupParticipant() {
		super(Dialect.P);
	}

	@Override
	protected PartitionTokenScannerBase newPartitionScanner() {
		return new PartitionTokenScanner();
	}


}
