package presto.editor.oops;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.rules.IToken;

import presto.editor.lang.PartitionTokenScannerBase;
import core.oops.lexer.OopsLexer;
import core.parser.Dialect;


public class PartitionTokenScanner extends PartitionTokenScannerBase {

	public PartitionTokenScanner() {
		super(Dialect.OOPS);
	}

	@Override
	public IToken nextToken() {
		CommonToken token = (CommonToken)lexer.nextToken();
		if(token.getType()!=OopsLexer.EOF)
			setLastToken(token);
		return new TokenProxy(token);
	}

}
