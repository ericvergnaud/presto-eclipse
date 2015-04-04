package presto.editor.base;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

import presto.editor.Plugin;
import presto.editor.prefs.SyntaxColoring.ColorPreference;

public class DamagerRepairer extends DefaultDamagerRepairer {

	public DamagerRepairer(ITokenScanner scanner) {
		super(scanner);
	}
	
	@Override
	protected TextAttribute getTokenTextAttribute(IToken token) {
		Object data = token.getData();
		if (data instanceof String && data.toString().startsWith("presto.partition.")) {
			String part = data.toString().split("\\.")[2].toUpperCase();
			try {
				ColorPreference pref = ColorPreference.valueOf(part);
				return pref.asTextAttribute(Plugin.getPreferenceStore());
			} catch(IllegalArgumentException e) {
				// non-syntax colored
			}
		}
		return fDefaultTextAttribute;
	}
	
}
