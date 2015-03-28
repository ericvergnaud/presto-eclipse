package presto.editor.boa;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import presto.editor.lang.DocumentSetupParticipantBase;
import core.parser.Dialect;

public class DocumentSetupParticipant extends DocumentSetupParticipantBase {

	public DocumentSetupParticipant() {
		super(Dialect.BOA);
	}

	@Override
	protected IPartitionTokenScanner newPartitionScanner() {
		return new PartitionTokenScanner();
	}


}
