package prompto.locator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;

import prompto.debugger.StackFrameProxy;

public class SourceLookupParticipant extends AbstractSourceLookupParticipant {

	@Override
	public String getSourceName(Object object) throws CoreException {
		if(object instanceof StackFrameProxy) {
			StackFrameProxy proxy = (StackFrameProxy)object;
			return proxy.getResource().getProjectRelativePath().toPortableString();
		}
		return null;
	}

}
