package presto.editor.oops;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import presto.editor.lang.DocumentSetupParticipantBase;
import core.parser.Dialect;

public class DocumentSetupParticipant extends DocumentSetupParticipantBase {

	public DocumentSetupParticipant() {
		super(Dialect.OOPS);
	}

	@Override
	protected IPartitionTokenScanner newPartitionScanner() {
		return new PartitionTokenScanner();
	}


}
