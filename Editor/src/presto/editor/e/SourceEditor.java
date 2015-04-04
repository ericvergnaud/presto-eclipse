package presto.editor.e;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;

import presto.editor.base.SourceEditorBase;
import presto.parser.Dialect;

public class SourceEditor extends SourceEditorBase {

	public SourceEditor() {
		super(Dialect.E);
	}

	@Override
	protected IDocumentSetupParticipant newDocumentSetupParticipant() {
		return new DocumentSetupParticipant();
	}

	
}
