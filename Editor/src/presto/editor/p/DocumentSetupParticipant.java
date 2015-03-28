package presto.editor.p;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import presto.editor.lang.DocumentSetupParticipantBase;
import presto.parser.Dialect;

public class DocumentSetupParticipant extends DocumentSetupParticipantBase {

	public DocumentSetupParticipant() {
		super(Dialect.P);
	}

	@Override
	protected IPartitionTokenScanner newPartitionScanner() {
		return new PartitionTokenScanner();
	}


}
