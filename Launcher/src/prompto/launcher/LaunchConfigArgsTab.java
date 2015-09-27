package prompto.launcher;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import prompto.utils.CmdLineParser;
import prompto.utils.ImageUtils;

public class LaunchConfigArgsTab extends AbstractLaunchConfigurationTab {

	Text argsText;
	
	@Override
	public void createControl(Composite parent) {
		Control root = createRoot(parent);
		setControl(root);
	}

	private Control createRoot(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(1, false));
		Control child = createArgsGroup(root);
		child.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		return root;
	}

	private Control createArgsGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setText("Command line arguments");
		Control child = createArgsText(group);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd.heightHint = 60;
		child.setLayoutData(gd);
		return group;	
	}

	private Control createArgsText(Composite parent) {
		argsText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		argsText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				manageControls();
			}
		});
		return argsText;
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// nothing to do
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		String cmdLine = LaunchUtils.getConfiguredCommandLineArguments(configuration);
		argsText.setText(cmdLine);
		manageControls();
	}

	private void manageControls() {
		setErrorMessage(null);
		try {
			CmdLineParser.parse(argsText.getText());
		} catch(Exception e) {
			setErrorMessage("Invalid commend line: " + e.getMessage());
		}
		updateLaunchConfigurationDialog();
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LauncherConstants.ARGUMENTS, argsText.getText());
	}

	@Override
	public String getName() {
		return "Arguments";
	}

	@Override
	public Image getImage() {
		return ImageUtils.load(Plugin.getDefault().getBundle(),"images/configArgs.png");
	}
}
