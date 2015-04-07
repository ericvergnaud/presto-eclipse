package presto.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;

import presto.parser.Dialect;
import presto.parser.ELexer;
import presto.parser.ILexer;
import presto.parser.OLexer;
import presto.parser.SLexer;

public class PartitionTokenScanner implements IPartitionTokenScanner {

	protected Dialect dialect;
	protected ILexer lexer;
	private CommonToken lastToken;
	private int startOffset;
	
	public PartitionTokenScanner(Dialect dialect) {
		this.dialect = dialect;
		this.lexer = dialect.getParserFactory().newLexer();
	}

	@Override
	public void setRange(IDocument document, int offset, int length) {
		this.lastToken = null;
		this.startOffset = offset;
		try {
			String data = document.get(offset, length);
			lexer.reset(new ByteArrayInputStream(data.getBytes()));
		} catch (BadLocationException | IOException e) {
		}
	}

	@Override
	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
		setRange(document, offset, length);
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
	public IToken nextToken() {
		switch(dialect) {
		case E:
			return nextEToken();
		case O:
			return nextOToken();
		case S:
			return nextSToken();
		default:
			throw new RuntimeException("Unsupported:" + dialect.name());	
		}
	}
	
	public IToken nextEToken() {
		CommonToken token = (CommonToken)lexer.nextToken();
		switch(token.getType()) {
			// skip tokens generated from LF_TAB, since they have inconsistent offsets 
			case ELexer.LF:
			case ELexer.INDENT:
			case ELexer.DEDENT:
				return nextToken();
			case ELexer.EOF:
				break;
			default:
				setLastToken(token);
		}
		return new ETokenProxy(token);
	}

	public IToken nextOToken() {
		CommonToken token = (CommonToken)lexer.nextToken();
		if(token.getType()!=OLexer.EOF)
			setLastToken(token);
		return new OTokenProxy(token);
	}

	public IToken nextSToken() {
		CommonToken token = (CommonToken)lexer.nextToken();
		switch(token.getType()) {
			// skip tokens generated from LF_TAB, since they have inconsistent offsets 
			case SLexer.LF:
			case SLexer.INDENT:
			case SLexer.DEDENT:
				return nextToken();
			case SLexer.EOF:
				break;
			default:
				setLastToken(token);
		}
		return new ETokenProxy(token);
	}



}
