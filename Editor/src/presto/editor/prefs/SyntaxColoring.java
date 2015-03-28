package presto.editor.prefs;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import presto.editor.Plugin;
import presto.editor.utils.ColorConstants;

public class SyntaxColoring {

	public static enum ColorPreference {
		TYPE(ColorConstants.TYPE, SWT.BOLD),
		KEYWORD(ColorConstants.KEYWORD, SWT.BOLD),
		SYMBOL(ColorConstants.SYMBOL, SWT.ITALIC),
		LITERAL(ColorConstants.LITERAL),
		BRANCH(ColorConstants.BRANCH, SWT.BOLD),
		LOOP(ColorConstants.LOOP, SWT.BOLD),
		COMMENT(ColorConstants.COMMENT);
		
		RGB defaultRgb;
		int defaultStyle = SWT.NORMAL;
		TextAttribute attribute = null;
		
		ColorPreference(RGB rgb) {
			this.defaultRgb = rgb;
		}
		
		ColorPreference(RGB rgb,int ... styles) {
			this.defaultRgb = rgb;
			for(int flag : styles)
				defaultStyle |= flag;
		}
		
		@Override
		public String toString() {
			return name().substring(0,1) + name().substring(1).toLowerCase();
		}
		
		public String getRgbPrefName() {
			return SyntaxColoring.class.getSimpleName() + "." +  this.name() + ".rgb";
		}
		
		public String getStylePrefName() {
			return SyntaxColoring.class.getSimpleName() + "." +  this.name() + ".style";
		}

		public void writeDefault(IPreferenceStore store) {
	        PreferenceConverter.setDefault(store, getRgbPrefName(), defaultRgb);
	        FontData font = new FontData();
	        font.setStyle(defaultStyle);
	        PreferenceConverter.setDefault(store, getStylePrefName(), font);
		}

		public void write(IPreferenceStore store, TextAttribute attr) {
	        attribute = attr;
	        PreferenceConverter.setValue(store, getRgbPrefName(), attr.getForeground().getRGB());
	        FontData font = new FontData();
	        font.setStyle(attr.getStyle());
	        PreferenceConverter.setValue(store, getStylePrefName(), font);
		}

		public void setToDefault(IPreferenceStore store) {
			attribute = null;
			store.setToDefault(getRgbPrefName());
			store.setToDefault(getStylePrefName());
		}

		public TextAttribute asTextAttribute(IPreferenceStore store) {
			if(attribute==null)
				attribute = readStored(store);
			return attribute;
		}
		
		private TextAttribute readStored(IPreferenceStore store) {
			RGB rgb = PreferenceConverter.getColor(store, getRgbPrefName());
			Color color = new Color(Display.getCurrent(),rgb);
			FontData font = PreferenceConverter.getFontData(store, getStylePrefName());
			return new TextAttribute(color,null,font.getStyle());
		}

	}
	
	public static void initializeDefaultPreferences() {
	    IPreferenceStore store = Plugin.getPreferenceStore();
	    for (ColorPreference pref : ColorPreference.values()) 
	    	pref.writeDefault(store);
	}

	public static boolean isAffectedByProperty(String property) {
	    for (ColorPreference pref : ColorPreference.values()) {
	    	if(property.equals(pref.getRgbPrefName()))
	    		return true;
	    	if(property.equals(pref.getStylePrefName()))
	    		return true;
	    }
		return false;
	}

}
