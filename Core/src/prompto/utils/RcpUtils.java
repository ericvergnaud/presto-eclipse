package prompto.utils;

import org.eclipse.swt.widgets.Combo;

public class RcpUtils {

	public static void selectInCombo(Combo combo, String name) {
		if(!name.isEmpty()) for(int i=0; i<combo.getItems().length; i++) {
			if(name.equals(combo.getItem(i))) {
				combo.select(i);
				return;
			}
		}
	}

}
