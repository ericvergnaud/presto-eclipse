package presto.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

public class TranslatedDocumentProvider extends AbstractDocumentProvider {

	@Override
	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
		return null; // no annotation for now
	}
	
	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		return new Document();
	}
	
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {
		// nothing to do
	}
	
	@Override
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		return null;
	}

}
