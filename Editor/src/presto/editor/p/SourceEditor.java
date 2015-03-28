package presto.editor.p;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;

import presto.editor.lang.SourceEditorBase;
import presto.parser.Dialect;

public class SourceEditor extends SourceEditorBase {

	public SourceEditor() {
		super(Dialect.P);
	}

	@Override
	protected IDocumentSetupParticipant newDocumentSetupParticipant() {
		return new DocumentSetupParticipant();
	}

	
}
