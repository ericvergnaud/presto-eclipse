package presto.editor.s;

import org.antlr.v4.runtime.Token;
import org.eclipse.jface.text.rules.IToken;

import presto.editor.Constants;
import presto.parser.ELexer;
import static presto.parser.SLexer.*;

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
		switch( token.getType() ) {
		case ELexer.LF:
		case ELexer.TAB:
		case ELexer.LF_TAB:
		case ELexer.WS:
			return true;
		default:
			return false;
		}
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
		case ALWAYS:
		case ELSE:
		case FINALLY:
		case IF:
		case OTHERWISE:
		case RETURN:
		case SWITCH:
		case WHEN:
			return Constants.BRANCH_PARTITION_NAME;
		case SYMBOL_IDENTIFIER:
			return Constants.SYMBOL_PARTITION_NAME;
		case ABSTRACT:
		case ALL:
		case AND:
		case ANY:
		case AS:
		case ATTR:
		case ATTRIBUTE:
		case ATTRIBUTES:
		case BOOLEAN_LITERAL:
		case CATEGORY:
		case CLASS:
		case CLOSE:
		case CODE:
		case CONTAINS:
		case CSHARP:
		case DEF:
		case DEFAULT:
		case DEFINE:
		case DOING:
		case ENUMERATED:
		case EXCEPT:
		case EXECUTE:
		case EXPECTING:
		case EXTENDS:
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
		case MODULO:	
		case NATIVE:
		case NOT:
		case NOTHING:
		case NULL:
		case ON:
		case OPEN:
		case OPERATOR:
		case OR:
		case PASS:
		case PYTHON2:
		case PYTHON3:
		case RAISE:
		case READ:
		case RECEIVING:
		case RETURNING:
		case SELF:
		case SETTER:
		case SINGLETON:
		case SORTED:
		case TEST:
		case THIS:
		case THROW:
		case TO:
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
