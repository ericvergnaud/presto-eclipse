package presto.editor.s;

import presto.editor.base.DocumentSetupParticipantBase;
import presto.editor.base.PartitionTokenScannerBase;
import presto.parser.Dialect;

public class DocumentSetupParticipant extends DocumentSetupParticipantBase {

	public DocumentSetupParticipant() {
		super(Dialect.S);
	}

	@Override
	protected PartitionTokenScannerBase newPartitionScanner() {
		return new PartitionTokenScanner();
	}


}
