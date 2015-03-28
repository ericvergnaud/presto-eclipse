package presto.editor.lang;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

public class DocumentProvider extends TextFileDocumentProvider {

	@Override
	protected IAnnotationModel createAnnotationModel(IFile file) {
		return new AnnotationModel(file);
	}
}
