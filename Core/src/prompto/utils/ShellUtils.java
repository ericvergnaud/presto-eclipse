package prompto.utils;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ShellUtils {

	public static Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}
}
