package presto.editor.s;

import org.antlr.v4.runtime.CommonToken;
import org.eclipse.jface.text.rules.IToken;

import presto.editor.base.PartitionTokenScannerBase;
import presto.editor.e.TokenProxy;
import presto.parser.Dialect;
import presto.parser.SLexer;


public class PartitionTokenScanner extends PartitionTokenScannerBase {

	public PartitionTokenScanner() {
		super(Dialect.S);
	}
	
	@Override
	public IToken nextToken() {
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
		return new TokenProxy(token);
	}

}
