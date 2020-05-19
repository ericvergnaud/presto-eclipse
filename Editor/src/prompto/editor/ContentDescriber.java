package prompto.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

import prompto.ide.core.CoreConstants;

public class ContentDescriber implements ITextContentDescriber {

	public static final QualifiedName PROMPTO_VALID = new QualifiedName( CoreConstants.EDITOR_PLUGIN_ID, "valid"); 
	
	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		return describe(new InputStreamReader(contents), description);
	}

	@Override
	public int describe(Reader contents, IContentDescription description) throws IOException {
		return INDETERMINATE;
	}

	@Override
	public QualifiedName[] getSupportedOptions() {
		return new QualifiedName[] { PROMPTO_VALID };
	}


	
}