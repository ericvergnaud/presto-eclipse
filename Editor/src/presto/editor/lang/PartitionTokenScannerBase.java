package presto.editor.lang;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import core.parser.Dialect;
import core.parser.ILexer;

public abstract class PartitionTokenScannerBase implements IPartitionTokenScanner {

	public static IPartitionTokenScanner newPartitionTokenScanner(Dialect dialect) {
		if(dialect==Dialect.BOA)
			return new presto.editor.boa.PartitionTokenScanner();
		else
			return new presto.editor.oops.PartitionTokenScanner();
	}
	
	protected Dialect dialect;
	protected ILexer lexer;
	private CommonToken lastToken;
	private int startOffset;
	
	public PartitionTokenScannerBase(Dialect dialect) {
		this.dialect = dialect;
	}

	@Override
	public void setRange(IDocument document, int offset, int length) {
		this.startOffset = offset;
		try {
			String data = document.get(offset, length);
			lexer = dialect.getParserFactory().newLexer(data);
		} catch (BadLocationException e) {
		}

	}

	protected void setLastToken(CommonToken token) {
		this.lastToken = token;
	}
	
	@Override
	public int getTokenOffset() {
		return startOffset + getLastTokenOffset();
	}
	
	private int getLastTokenOffset() {
		return lastToken==null ? 0 : lastToken.getStartIndex();
	}

	@Override
	public int getTokenLength() {
		return lastToken==null ? 0 : 1+lastToken.getStopIndex()-lastToken.getStartIndex();
	}

	@Override
	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
		setRange(document, offset, length);
	}

}
