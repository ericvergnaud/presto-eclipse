package presto.editor.boa;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;

import presto.editor.lang.SourceEditorBase;
import core.parser.Dialect;

public class SourceEditor extends SourceEditorBase {

	public SourceEditor() {
		super(Dialect.BOA);
	}

	@Override
	protected IDocumentSetupParticipant newDocumentSetupParticipant() {
		return new DocumentSetupParticipant();
	}

	
}
