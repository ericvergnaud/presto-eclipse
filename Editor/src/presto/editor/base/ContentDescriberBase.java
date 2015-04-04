package presto.editor.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

import presto.editor.Plugin;

public abstract class ContentDescriberBase implements ITextContentDescriber {

	public static final QualifiedName PRESTO_VALID = new QualifiedName( Plugin.PLUGIN_ID, "valid"); //$NON-NLS-1$

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