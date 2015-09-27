package prompto.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import prompto.core.Utils.RunType;

public class ScriptConfigTabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		setTabs( new ILaunchConfigurationTab[] {
			new LaunchConfigMainTab(RunType.SCRIPT),
			new LaunchConfigArgsTab(),
			new CommonTab()
		} );
	}

}
