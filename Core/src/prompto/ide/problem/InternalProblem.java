package prompto.ide.problem;

import prompto.problem.IProblem;

public class InternalProblem implements IProblem {

	String message;
	
	public InternalProblem(String message) {
		this.message = message;
	}
	
	@Override
	public String getPath() {
		return "<INTERNAL>";
	}
	
	@Override
	public Type getType() {
		return Type.ERROR;
	}
	
	@Override
	public int getStartLine() {
		return 0;
	}
	
	@Override
	public int getStartColumn() {
		return 0;
	}
	
	@Override
	public int getStartIndex() {
		return 0;
	}
	
	@Override
	public int getEndIndex() {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
