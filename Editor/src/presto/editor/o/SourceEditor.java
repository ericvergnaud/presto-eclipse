package presto.editor.o;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;

import presto.editor.base.SourceEditorBase;
import presto.parser.Dialect;

public class SourceEditor extends SourceEditorBase {

	public SourceEditor() {
		super(Dialect.O);
	}

	@Override
	protected IDocumentSetupParticipant newDocumentSetupParticipant() {
		return new DocumentSetupParticipant();
	}

}
