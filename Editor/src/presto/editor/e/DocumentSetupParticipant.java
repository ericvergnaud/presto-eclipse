package presto.editor.e;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import presto.editor.lang.DocumentSetupParticipantBase;
import presto.parser.Dialect;

public class DocumentSetupParticipant extends DocumentSetupParticipantBase {

	public DocumentSetupParticipant() {
		super(Dialect.E);
	}

	@Override
	protected IPartitionTokenScanner newPartitionScanner() {
		return new PartitionTokenScanner();
	}


}
