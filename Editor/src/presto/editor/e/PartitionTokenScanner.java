package presto.editor.e;

import org.antlr.v4.runtime.CommonToken;
import org.eclipse.jface.text.rules.IToken;

import presto.editor.lang.PartitionTokenScannerBase;
import presto.parser.Dialect;
import presto.parser.ELexer;


public class PartitionTokenScanner extends PartitionTokenScannerBase {

	public PartitionTokenScanner() {
		super(Dialect.E);
	}
	
	@Override
	public IToken nextToken() {
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
		return new TokenProxy(token);
	}

}
