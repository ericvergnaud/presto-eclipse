package presto.editor.boa;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.rules.IToken;

import presto.editor.lang.PartitionTokenScannerBase;
import core.boa.lexer.BoaLexer;
import core.parser.Dialect;


public class PartitionTokenScanner extends PartitionTokenScannerBase {

	public PartitionTokenScanner() {
		super(Dialect.BOA);
	}
	
	@Override
	public IToken nextToken() {
		CommonToken token = (CommonToken)lexer.nextToken();
		if(token.getType()!=BoaLexer.EOF) {
			if(token.getStartIndex()==token.getStopIndex()) 
				return nextToken();
			setLastToken(token);
		}
		return new TokenProxy(token);
	}

}
