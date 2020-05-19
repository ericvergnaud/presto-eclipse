package prompto.locator;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import prompto.ide.core.CoreConstants;
import prompto.parser.Dialect;

public class ModelPresentation extends LabelProvider implements IDebugModelPresentation {

	@Override
	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile)
			return new FileEditorInput((IFile) element);
		if (element instanceof IFileStore)
			return new FileStoreEditorInput((IFileStore) element);
		if (element instanceof ILineBreakpoint)
			return new FileEditorInput((IFile) ((ILineBreakpoint) element)
					.getMarker().getResource());
		return null;
	}

	@Override
	public String getEditorId(IEditorInput input, Object element) {
		String extension = null;
		if (element instanceof IFile)
			extension = ((IFile) element).getFileExtension();
		else if (element instanceof IFileStore) {
			String fileName = ((IFileStore) element).fetchInfo().getName();
			extension = fileName.substring(fileName.lastIndexOf('.')+1);
		} else if (element instanceof ILineBreakpoint)
			extension = ((ILineBreakpoint) element).getMarker().getResource().getFileExtension();
		if (extension != null) {
			try {
				Dialect.valueOf(extension.substring(1, 2).toUpperCase());
				return CoreConstants.EDITOR_GENERIC_ID;
			} catch (Exception e) {
				// nothing to do
			}
		}
		return null;
	}

	@Override
	public void setAttribute(String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void computeDetail(IValue value, IValueDetailListener listener) {
		// TODO Auto-generated method stub

	}

}
