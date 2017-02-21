package prompto.locator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;

import prompto.debugger.StackFrameProxy;

public class ProjectSourceLookup extends AbstractSourceLookupParticipant {

	@Override
	public String getSourceName(Object object) throws CoreException {
		if(object instanceof StackFrameProxy) {
			StackFrameProxy proxy = (StackFrameProxy)object;
			if(!"__store__".equals(proxy.getStackFrame().getFilePath()))
				return proxy.getResource().getProjectRelativePath().toPortableString();
		}
		return null;
	}

}
