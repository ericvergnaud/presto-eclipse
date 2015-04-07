package presto.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

import presto.core.Constants;

public class ContentDescriber implements ITextContentDescriber {

	public static final QualifiedName PRESTO_VALID = new QualifiedName( Constants.EDITOR_PLUGIN_ID, "valid"); 
	
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
		return new QualifiedName[] { PRESTO_VALID };
	}


	
}