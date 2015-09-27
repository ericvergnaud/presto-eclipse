package prompto.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;

import prompto.problem.ProblemManager;

public class DocumentProvider extends TextFileDocumentProvider {

	@Override
	protected IAnnotationModel createAnnotationModel(IFile file) {
		return new AnnotationModel(file);
	}
	
	@Override
	protected void disposeFileInfo(Object element, FileInfo info) {
		/* in case a file editor is closed but not saved, we need to update errors */
		if(element instanceof FileEditorInput) {
			IFile file = ((FileEditorInput)element).getFile();
			ProblemManager.processFile(file);
		}
		super.disposeFileInfo(element, info);
	}
}
