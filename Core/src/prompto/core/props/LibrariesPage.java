package prompto.core.props;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import prompto.core.LibraryNature;

public class LibrariesPage extends PropertyPage {

	Button excludeRuntime;
	
	@Override
	protected Control createContents(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		control.setLayout(layout);
		excludeRuntime = new Button(control, SWT.CHECK);
		excludeRuntime.setText("Exclude Prompto Runtime Library");
		excludeRuntime.setSelection(isExcludeRuntime());
		return control;
	}

	private boolean isExcludeRuntime() {
		try {
			IProject project = (IProject) getElement().getAdapter(IProject.class);
			String value = project.getPersistentProperty(LibraryNature.EXCLUDE_RUNTIME_PROPERTY);
			return value==null ? false : Boolean.valueOf(value);
		} catch(CoreException e) {
			return false;
		}
	}

	private void setExcludeRuntime(boolean exclude) {
		try {
			IProject project = (IProject) getElement().getAdapter(IProject.class);
			project.setPersistentProperty(LibraryNature.EXCLUDE_RUNTIME_PROPERTY, String.valueOf(exclude));
		} catch(CoreException e) {
			// TODO
		}
	}

	@Override
	protected void performDefaults() {
		excludeRuntime.setSelection(false);
	}
	
	@Override
	protected void performApply() {
		boolean exclude = excludeRuntime.getSelection();
		setExcludeRuntime(exclude);
	}
	
	
	@Override
	public boolean performOk() {
		performApply();
		return true;
	}
	
}
