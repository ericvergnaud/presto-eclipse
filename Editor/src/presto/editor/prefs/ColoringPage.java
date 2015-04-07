package presto.editor.prefs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import presto.editor.ETokenProxy;
import presto.editor.OTokenProxy;
import presto.editor.Plugin;
import presto.editor.STokenProxy;
import presto.editor.prefs.SyntaxColoring.ColorPreference;
import presto.parser.Dialect;
import presto.parser.ILexer;

public class ColoringPage extends PreferencePage implements IWorkbenchPreferencePage {

	List colorList;
	ColorSelector colorSelector;
	Button boldCheckBox;
	Button italicCheckBox;
	Button underlineCheckBox;
	Button eRadio;
	Button oRadio;
	Button pRadio;
	StyledText preview;
	
	static final String previewContent_E = "define id as: Integer attribute\r\n"
			+ "define name as: Text attribute\r\n"
			+ "\r\n"
			+ "define Person as: enumerated category with attributes: id and name, with symbols:\r\n"
			+ "    JOHN with 1 as id and \"John\" as name\r\n"
			+ "    SYLVIA with 2 as id and \"Sylvia\" as name\r\n"
			+ "\r\n"
			+ "/* this is a comment */\r\n"
			+ "define main as: method receiving: Text{} options doing:\r\n"
			+ "    if options[\"what\"] = \"id\":\r\n"
			+ "        for each person in Person.symbols:\r\n"
			+ "            print person.id\r\n"
			+ "    else:\r\n"
			+ "        for each person in Person.symbols:\r\n"
			+ "            print person.name\r\n";
	
	static final String previewContent_O = "attribute id : Integer;\r\n" 
			+ "attribute name : Text;\r\n"
			+ "\r\n"
			+ "enumerated category Person(id , name) {\r\n"
			+ "    JOHN ( id = 1, name = \"John\" ); \r\n"
			+ "    SYLVIA ( id = 2, name = \"Sylvia\" );\r\n"
			+ "}\r\n" 
			+ "\r\n"
			+ "/* this is a comment */\r\n"
			+ "method main ( Text{} options ) {\r\n"
			+ "    if ( options[\"what\"] == \"id\" ) {\r\n"
			+ "        for each ( person in Person.symbols )\r\n"
			+ "            print ( person.id );\r\n"
			+ "    } else {\r\n"
			+ "        for each ( person in Person.symbols )\r\n"
			+ "            print ( person.name );\r\n"
			+ "    }\r\n"
			+ "}\r\n";

	static final String previewContent_S = "attribute id : Integer;\r\n" 
			+ "attribute name : Text;\r\n"
			+ "\r\n"
			+ "enumerated category Person(id , name) {\r\n"
			+ "    JOHN ( id = 1, name = \"John\" ); \r\n"
			+ "    SYLVIA ( id = 2, name = \"Sylvia\" );\r\n"
			+ "}\r\n" 
			+ "\r\n"
			+ "/* this is a comment */\r\n"
			+ "method main ( Text{} options ) {\r\n"
			+ "    if ( options[\"what\"] == \"id\" ) {\r\n"
			+ "        for each ( person in Person.symbols )\r\n"
			+ "            print ( person.id );\r\n"
			+ "    } else {\r\n"
			+ "        for each ( person in Person.symbols )\r\n"
			+ "            print ( person.name );\r\n"
			+ "    }\r\n"
			+ "}\r\n";


	Map<String,TextAttribute> workingPrefs = new HashMap<String, TextAttribute>();
	
	public ColoringPage() {
	}

	/**
	 * @wbp.parser.constructor
	 */
	public ColoringPage(String title) {
		super(title);
	}

	public ColoringPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		return createMainControl(parent);
	}
	
	@Override
	protected void performDefaults() {
		IPreferenceStore store = Plugin.getPreferenceStore();
		for(ColorPreference pref  : ColorPreference.values())
			pref.setToDefault(store);
		workingPrefs.clear();
		populatePreview();
		colorSelected();
	}
	
	@Override
	protected void performApply() {
		IPreferenceStore store = Plugin.getPreferenceStore();
		for(ColorPreference pref  : ColorPreference.values()) {
			TextAttribute attr = workingPrefs.get(pref.name());
			if(attr!=null)
				pref.write(store, attr);
		}
		workingPrefs.clear();
	}
	
	@Override
	public boolean performOk() {
		performApply();
		return true;
	}
	
	private Composite createMainControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		control.setLayout(layout);
		createColorAndStyleControls(control);
		createPreview(control);
		populateControls();
		populatePreview();
		colorSelected();
		return control;
	}
	
	private void createPreview(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout layout = new GridLayout();
		control.setLayout(layout);
		createPreviewHeader(control);
		createPreviewText(control);
	}
	
	private void createPreviewHeader(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		control.setLayout(layout);
		createPreviewLabel(control);
		createPreviewE(control);
		createPreviewO(control);
		createPreviewP(control);
	}
	
	private void createPreviewLabel(Composite parent) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText("Preview dialect:");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
	}

	private void createPreviewE(Composite parent) {
		eRadio = new Button(parent, SWT.RADIO);
		eRadio.setText("E");
		eRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		eRadio.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				populatePreview();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}
	
	private void createPreviewO(Composite parent) {
		oRadio = new Button(parent, SWT.RADIO);
		oRadio.setText("O");
		oRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		oRadio.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				populatePreview();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}

	private void createPreviewP(Composite parent) {
		oRadio = new Button(parent, SWT.RADIO);
		oRadio.setText("P");
		oRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		oRadio.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				populatePreview();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}

	private void createPreviewText(Composite parent) {
		preview = new StyledText(parent, SWT.BORDER);
		preview.setMargins(5, 5, 5, 5);
		preview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	private void createColorAndStyleControls(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		control.setLayout(layout);
		createColorList(control);
		createStyleControls(control);
	}

	private void populateControls() {
		for(ColorPreference pref  : ColorPreference.values())
			colorList.add(pref.toString());
		colorList.addSelectionListener(new ColorSelectionListener());
		oRadio.setSelection(false);
		pRadio.setSelection(false);
		eRadio.setSelection(true);
	}
	
	private void populatePreview() {
		Dialect dialect = dialectFromSelection();
		preview.setText(getPreviewContent(dialect));	
		StyleRange[] ranges = collectRanges(dialect);
		preview.setStyleRanges(ranges);
	}
	
	private Dialect dialectFromSelection() {
		if(eRadio.getSelection())
			return Dialect.E;
		else if(oRadio.getSelection())
			return Dialect.O;
		else
			return Dialect.S;
	}

	private String getPreviewContent(Dialect dialect) {
		switch(dialect) {
		case E:
			return previewContent_E;
		case O:
			return previewContent_O;
		case S:
			return previewContent_S;
		default:
			throw new RuntimeException("Unsupported dialect:" + dialect.name());
		}
	}

	private StyleRange[] collectRanges(Dialect dialect) {
		try {
			ILexer lexer = dialect.getParserFactory().newLexer();
			String content = getPreviewContent(dialect);
			lexer.reset(new ByteArrayInputStream(content.getBytes()));
			java.util.List<StyleRange> ranges = new LinkedList<StyleRange>();
			collectRanges(lexer, ranges);
			return ranges.toArray(new StyleRange[ranges.size()]);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void collectRanges(ILexer lexer, java.util.List<StyleRange> ranges) {
		Token token = lexer.nextToken();
		while(token.getType()!=(-1)) {
			StyleRange range = collectRange(lexer.getDialect(), token);
			if(range!=null)
				ranges.add(range);
			token = lexer.nextToken();
		}
	}
	
	private StyleRange collectRange(Dialect dialect, Token token) {
		int length = token.getText()==null ? 0 : token.getText().length();
		if(length==0)
			return null;
		String partition = getPartition(dialect, token);
		String type = partition.split("\\.")[2].toUpperCase();
		if("OTHER".equals(type))
			return null;
		else {
			TextAttribute attr = workingPrefs.get(type);
			if(attr==null)
				attr = ColorPreference.valueOf(type).asTextAttribute(Plugin.getPreferenceStore());
			int offset = ((CommonToken)token).getStartIndex();
			StyleRange range = new StyleRange(offset, length, attr.getForeground(), attr.getBackground());
			range.fontStyle = attr.getStyle() & (SWT.BOLD | SWT.ITALIC);
			range.underline = (attr.getStyle() & TextAttribute.UNDERLINE)!=0;
			range.underlineStyle = SWT.UNDERLINE_SINGLE;
			return range;
		}
	}
	
	private String getPartition(Dialect dialect, Token token) {
		switch(dialect) {
		case E:
			return new ETokenProxy(token).getData().toString();
		case O:
			return new OTokenProxy(token).getData().toString();
		case S:
		default:
			return new STokenProxy(token).getData().toString();
		}
	}

	class ColorSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			colorSelected();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			colorSelected();
		}

	}

	private void colorSelected() {
		TextAttribute pref = getSelectedAttribute();
		if(pref==null)
			noColorSelected();
		else
			colorSelected(pref);
	}
	
	private String getSelectedName() {
		int idx = colorList.getSelectionIndex();
		if(idx==-1)
			return null;
		else
			return colorList.getItem(idx);
	}

	private TextAttribute getSelectedAttribute() {
		String name = getSelectedName(); 
		if(name==null)
			return null;
		TextAttribute pref = workingPrefs.get(name.toUpperCase());
		if(pref==null)
			pref = ColorPreference.valueOf(name.toUpperCase()).asTextAttribute(Plugin.getPreferenceStore());
		return pref;
	}
	
	private void setSelectedAttribute(TextAttribute attr) {
		String name = getSelectedName(); 
		workingPrefs.put(name.toUpperCase(), attr);
		populatePreview();
	}

	private void noColorSelected() {
		colorSelector.setEnabled(false);
		boldCheckBox.setSelection(false);
		boldCheckBox.setEnabled(false);
		italicCheckBox.setSelection(false);
		italicCheckBox.setEnabled(false);
		underlineCheckBox.setSelection(false);
		underlineCheckBox.setEnabled(false);
	}

	private void colorSelected(TextAttribute pref) {
		colorSelector.setColorValue(pref.getForeground().getRGB());
		colorSelector.setEnabled(true);
		boldCheckBox.setSelection((pref.getStyle() & SWT.BOLD)!=0);
		boldCheckBox.setEnabled(true);
		italicCheckBox.setSelection((pref.getStyle() & SWT.ITALIC)!=0);
		italicCheckBox.setEnabled(true);
		underlineCheckBox.setSelection((pref.getStyle() & TextAttribute.UNDERLINE)!=0);
		underlineCheckBox.setEnabled(true);
	}

	private void createColorList(Composite parent) {
		colorList = new List(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		GridData grid = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		grid.heightHint = convertHeightInCharsToPixels(12);
		colorList.setLayoutData(grid);
	}

	private void createStyleControls(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		GridData grid = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		control.setLayoutData(grid);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		control.setLayout(layout);
		control.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		createColorControl(control);
		createBoldControl(control);
		createItalicControl(control);
		createUnderlineControl(control);
	}

	private void createBoldControl(Composite parent) {
		boldCheckBox = createCheckBox(parent,"Bold");
		boldCheckBox.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TextAttribute pref = getSelectedAttribute();
				toggleStyleFlag(pref, SWT.BOLD, boldCheckBox.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}

	private void createItalicControl(Composite parent) {
		italicCheckBox = createCheckBox(parent,"Italic");
		italicCheckBox.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TextAttribute pref = getSelectedAttribute();
				toggleStyleFlag(pref, SWT.ITALIC, italicCheckBox.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}

	private void createUnderlineControl(Composite parent) {
		underlineCheckBox = createCheckBox(parent,"Underline");
		underlineCheckBox.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TextAttribute pref = getSelectedAttribute();
				toggleStyleFlag(pref, TextAttribute.UNDERLINE, underlineCheckBox.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}

	private void toggleStyleFlag(TextAttribute attr, int styleFlag, boolean set) {
		int style = attr.getStyle();
		if(set)
			style |= styleFlag;
		else
			style &= ~styleFlag;
		attr = new TextAttribute(attr.getForeground(), null, style);
		setSelectedAttribute(attr);
	}

	private Button createCheckBox(Composite parent, String label) {
		Button checkBox = new Button(parent, SWT.CHECK);
		checkBox.setText(label);
		return checkBox;
	}

	private void createColorControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		control.setLayout(layout);
		control.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		createColorLabel(control);
		createColorSelector(control);
	}

	private void createColorLabel(Composite parent) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText("Color:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
	}

	private void createColorSelector(Composite parent) {
		colorSelector = new ColorSelector(parent);
		Button colorButton = colorSelector.getButton();
		colorButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		colorButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Color color = new Color(Display.getCurrent(),colorSelector.getColorValue());
				TextAttribute attr = getSelectedAttribute();
				attr = new TextAttribute(color, null, attr.getStyle());
				setSelectedAttribute(attr);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}


}
