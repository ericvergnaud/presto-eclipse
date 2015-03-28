package presto.eclipse.plugin.boa;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.rules.IToken;

import core.boa.lexer.BoaLexer;
import core.runtime.Dialect;
import presto.eclipse.plugin.lang.PartitionTokenScannerBase;


public class PartitionTokenScanner extends PartitionTokenScannerBase {

	public PartitionTokenScanner() {
		super(Dialect.BOA);
	}
	
	@Override
	public IToken nextToken() {
		CommonToken token = (CommonToken)lexer.nextToken();
		// skip start less INDENT/DEDENT/LF tokens
		if(token.getStartIndex()==0) switch(token.getType()) {
		case BoaLexer.INDENT:
		case BoaLexer.DEDENT:
		case BoaLexer.LF:
			return nextToken();
		}
		if(token.getType()!=BoaLexer.EOF)
			setLastToken(token);
		return new TokenProxy(token);
	}

}
