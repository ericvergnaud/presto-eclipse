package prompto.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

import prompto.parser.Dialect;
import prompto.utils.CoreUtils;

public class MultiPageEditor extends MultiPageEditorPart {

	Dialect dialect;
	SourceEditor actualEditor; // for the file dialect
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (input instanceof IFileEditorInput)
			init((IFileEditorInput)input);
		else if(input instanceof IURIEditorInput)
			init((IURIEditorInput)input);
		else	
			throw new PartInitException("Unsupported input:" + input.getClass().getSimpleName());
		super.init(site, input);
	}
	
	private void init(IURIEditorInput input) {
		dialect = CoreUtils.getDialect(input.getURI());
		setPartName(input.getName());
	}

	private void init(IFileEditorInput input) {
		dialect = CoreUtils.getDialect(input.getFile());
		setPartName(input.getFile().getName());
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
