package prompto.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import prompto.parser.Dialect;
import prompto.utils.CoreUtils;

public class MultiPageEditor extends MultiPageEditorPart {

	Dialect dialect;
	SourceEditor actualEditor; // for the file dialect
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof IFileEditorInput))
			throw new PartInitException("Unsupported input:" + input.getClass().getSimpleName());
		IFile file = ((IFileEditorInput)input).getFile();
		dialect = CoreUtils.getDialect(file);
		setPartName(file.getName());
		super.init(site, input);
	}
	
	@Override
	protected void createPages() {
		for(Dialect d : Dialect.values())
			createPage(d);
		setActivePage(dialect.ordinal());
	}

	private void createPage(Dialect d) {
		try {
			SourceEditor editor = new SourceEditor(d, this);
			if(d==dialect)
				this.actualEditor = editor;
			IEditorInput input = (d==dialect) ? getEditorInput() : new TranslatedInput(dialect, d, getEditorInput());
			int idx = addPage(editor, input);
			setPageText(idx, d.getFriendlyName());
		} catch(PartInitException e) {
			// TODO log
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		actualEditor.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
		// cannot happen
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public SourceEditor getActualSourceEditor() {
		return actualEditor;
	}

}
