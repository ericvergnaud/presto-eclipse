package presto.editor.boa;

import org.antlr.runtime.Token;
import org.eclipse.jface.text.rules.IToken;

import presto.editor.Constants;
import static core.boa.lexer.IndentingLexer.*;

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
			return Constants.TYPE_PARTITION_NAME;
		case CHAR_LITERAL:
		case DATETIME_LITERAL:
		case DATE_LITERAL:
		case DECIMAL_LITERAL:
		case HEXA_LITERAL:
		case INTEGER_LITERAL:
		case PERIOD_LITERAL:
		case TEXT_LITERAL:
		case TIME_LITERAL:
			return Constants.LITERAL_PARTITION_NAME;
		case FOR:
		case EACH:
		case DO:
		case WHILE:
			return Constants.LOOP_PARTITION_NAME;
		case IF:
		case ELSE:
		case OTHERWISE:
		case ALWAYS:
		case SWITCH:
		case WHEN:
		case RETURN:
			return Constants.BRANCH_PARTITION_NAME;
		case SYMBOL_IDENTIFIER:
			return Constants.SYMBOL_PARTITION_NAME;
		case ABSTRACT:
		case ALL:
		case AND:
		case ANY:
		case AS:
		case ATTRIBUTE:
		case ATTRIBUTES:
		case BOOLEAN_LITERAL:
		case CATEGORY:
		case CLOSE:
		case CODE:
		case CONTAINS:
		case CSHARP:
		case DEFINE:
		case DOING:
		case ENUMERATED:
		case EXECUTE:
		case FETCH:
		case FROM:
		case GETTER:
		case IN:
		case INVOKE:
		case IS:
		case JAVA:
		case JAVASCRIPT:
		case MAPPINGS:
		case MATCHING:
		case METHOD:
		case METHODS:
		case NATIVE:
		case NOT:
		case NULL_LITERAL:
		case ON:
		case OPEN:
		case OR:
		case PYTHON:
		case RAISE:
		case READ:
		case RECEIVING:
		case RETURNING:
		case SETTER:
		case SORTED:
		case TO:
		case VBNET:
		case WHERE:
		case WITH:
		case WRITE:
			return Constants.KEYWORD_PARTITION_NAME;
		case COMMENT:
			return Constants.COMMENT_PARTITION_NAME;
		default:
			return Constants.OTHER_PARTITION_NAME;

		}
	}

}
