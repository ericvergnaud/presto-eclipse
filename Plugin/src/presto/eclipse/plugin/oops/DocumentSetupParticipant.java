package presto.eclipse.plugin.oops;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import presto.eclipse.plugin.lang.DocumentSetupParticipantBase;
import core.runtime.Dialect;

public class DocumentSetupParticipant extends DocumentSetupParticipantBase {

	public DocumentSetupParticipant() {
		super(Dialect.OOPS);
	}

	@Override
	protected IPartitionTokenScanner newPartitionScanner() {
		return new PartitionTokenScanner();
	}


}
