package prompto.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import prompto.parser.Dialect;

public class TranslatedInput implements IEditorInput {

	Dialect from;
	Dialect to;
	IEditorInput input;
	
	public TranslatedInput(Dialect from, Dialect to, IEditorInput input) {
		this.from = from;
		this.to = to;
		this.input = input;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Object getAdapter(Class adapter) {
		return input.getAdapter(adapter);
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return input.getImageDescriptor();
	}

	@Override
	public String getName() {
		return input.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return input.getToolTipText();
	}

}
