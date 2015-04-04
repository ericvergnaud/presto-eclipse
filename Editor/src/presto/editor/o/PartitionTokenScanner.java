package presto.editor.o;

import org.antlr.v4.runtime.CommonToken;
import org.eclipse.jface.text.rules.IToken;

import presto.editor.base.PartitionTokenScannerBase;
import presto.parser.Dialect;
import presto.parser.OLexer;


public class PartitionTokenScanner extends PartitionTokenScannerBase {

	public PartitionTokenScanner() {
		super(Dialect.O);
	}

	@Override
	public IToken nextToken() {
		CommonToken token = (CommonToken)lexer.nextToken();
		if(token.getType()!=OLexer.EOF)
			setLastToken(token);
		return new TokenProxy(token);
	}

}
