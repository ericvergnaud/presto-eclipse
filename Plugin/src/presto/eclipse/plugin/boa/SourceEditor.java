package presto.eclipse.plugin.boa;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;

import presto.eclipse.plugin.lang.SourceEditorBase;
import core.runtime.Dialect;

public class SourceEditor extends SourceEditorBase {

	public SourceEditor() {
		super(Dialect.BOA);
	}

	@Override
	protected IDocumentSetupParticipant newDocumentSetupParticipant() {
		return new DocumentSetupParticipant();
	}

	
}
