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
		if(token.getType()!=ELexer.EOF) {
			if(token.getStartIndex()==token.getStopIndex()) 
				return nextToken();
			setLastToken(token);
		}
		return new TokenProxy(token);
	}

}
