package presto.editor.p;

import org.antlr.v4.runtime.CommonToken;
import org.eclipse.jface.text.rules.IToken;

import presto.editor.base.PartitionTokenScannerBase;
import presto.editor.e.TokenProxy;
import presto.parser.Dialect;
import presto.parser.PLexer;


public class PartitionTokenScanner extends PartitionTokenScannerBase {

	public PartitionTokenScanner() {
		super(Dialect.P);
	}
	
	@Override
	public IToken nextToken() {
		CommonToken token = (CommonToken)lexer.nextToken();
		switch(token.getType()) {
			// skip tokens generated from LF_TAB, since they have inconsistent offsets 
			case PLexer.LF:
			case PLexer.INDENT:
			case PLexer.DEDENT:
				return nextToken();
			case PLexer.EOF:
				break;
			default:
				setLastToken(token);
		}
		return new TokenProxy(token);
	}

}
