package presto.editor.lang;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import presto.parser.Dialect;
import presto.parser.ILexer;

public abstract class PartitionTokenScannerBase implements IPartitionTokenScanner {

	public static IPartitionTokenScanner newPartitionTokenScanner(Dialect dialect) {
		switch(dialect) {
		case E:
			return new presto.editor.e.PartitionTokenScanner();
		case O:
			return new presto.editor.o.PartitionTokenScanner();
		case P:
			return new presto.editor.p.PartitionTokenScanner();
		default:
			throw new RuntimeException("Unsupported");
		}
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
		return lastToken==null ? 0 : getTokenLength(lastToken);
	}
	
	public int getTokenLength(Token token) {
		return 1+token.getStopIndex()-token.getStartIndex();
	}

	@Override
	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
		setRange(document, offset, length);
	}

}
