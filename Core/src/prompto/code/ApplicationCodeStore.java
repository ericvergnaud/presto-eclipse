package prompto.code;

import prompto.code.ICodeStore;

public class ApplicationCodeStore extends EclipseCodeStore {

	public ApplicationCodeStore(ICodeStore next) {
		super(next);
	}
}
