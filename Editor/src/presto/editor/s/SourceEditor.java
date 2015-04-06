package presto.editor.s;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;

import presto.editor.base.SourceEditorBase;
import presto.parser.Dialect;

public class SourceEditor extends SourceEditorBase {

	public SourceEditor() {
		super(Dialect.S);
	}

	@Override
	protected IDocumentSetupParticipant newDocumentSetupParticipant() {
		return new DocumentSetupParticipant();
	}

	
}
