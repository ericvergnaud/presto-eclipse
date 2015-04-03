package presto.editor.o;

import presto.editor.lang.DocumentSetupParticipantBase;
import presto.editor.lang.PartitionTokenScannerBase;
import presto.parser.Dialect;

public class DocumentSetupParticipant extends DocumentSetupParticipantBase {

	public DocumentSetupParticipant() {
		super(Dialect.O);
	}

	@Override
	protected PartitionTokenScannerBase newPartitionScanner() {
		return new PartitionTokenScanner();
	}


}
