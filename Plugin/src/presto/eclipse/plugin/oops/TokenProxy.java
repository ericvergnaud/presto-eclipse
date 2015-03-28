package presto.eclipse.plugin.oops;

import static core.oops.lexer.OopsLexer.*;

import org.antlr.runtime.Token;
import org.eclipse.jface.text.rules.IToken;

import presto.eclipse.plugin.PrestoConstants;

public class TokenProxy implements IToken {

	Token token;

	public TokenProxy(Token token) {
		this.token = token;
	}

	@Override
	public boolean isUndefined() {
		return false;
	}

	@Override
	public boolean isWhitespace() {
		return token.getType() == WS;
	}

	@Override
	public boolean isEOF() {
		return token.getType() == EOF;
	}

	@Override
	public boolean isOther() {
		return true;
	}

	@Override
	public Object getData() {
		switch (token.getType()) {
		case BOOLEAN:
		case CHARACTER:
		case DATE:
		case DATETIME:
		case DECIMAL:
		case DOCUMENT:
		case INTEGER:
		case METHOD_T:
		case PERIOD:
		case RESOURCE:
		case TEXT:
		case TIME:
			return PrestoConstants.TYPE_PARTITION_NAME;
		case CHAR_LITERAL:
		case DATETIME_LITERAL:
		case DATE_LITERAL:
		case DECIMAL_LITERAL:
		case HEXA_LITERAL:
		case INTEGER_LITERAL:
		case PERIOD_LITERAL:
		case TEXT_LITERAL:
		case TIME_LITERAL:
			return PrestoConstants.LITERAL_PARTITION_NAME;
		case FOR:
		case EACH:
		case DO:
		case WHILE:
			return PrestoConstants.LOOP_PARTITION_NAME;
		case ELSE:
		case IF:
		case FINALLY:
		case RETURN:
		case SWITCH:
			return PrestoConstants.BRANCH_PARTITION_NAME;
		case SYMBOL_IDENTIFIER:
			return PrestoConstants.SYMBOL_PARTITION_NAME;
		case ABSTRACT:
		case ALL:
		case ANY:
		case ATTRIBUTE:
		case BOOLEAN_LITERAL:
		case CATEGORY:
		case CLOSE:
		case CODE:
		case CONTAINS:
		case CSHARP:
		case ENUMERATED:
		case EXECUTE:
		case FETCH:
		case FROM:
		case GETTER:
		case IN:
		case JAVA:
		case JAVASCRIPT:
		case MAPPINGS:
		case MATCHING:
		case METHOD:
		case NATIVE:
		case NOT:
		case NULL_LITERAL:
		case OPEN:
		case PYTHON:
		case THROW:
		case READ:
		case SETTER:
		case SORTED:
		case TO:
		case VBNET:
		case WHERE:
		case WITH:
		case WRITE:
			return PrestoConstants.KEYWORD_PARTITION_NAME;
		case COMMENT:
			return PrestoConstants.COMMENT_PARTITION_NAME;
		default:
			return PrestoConstants.OTHER_PARTITION_NAME;

		}
	}

}
