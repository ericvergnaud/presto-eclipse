package presto.locator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;

import presto.debugger.StackFrameProxy;

public class SourceLookupParticipant extends AbstractSourceLookupParticipant {

	@Override
	public String getSourceName(Object object) throws CoreException {
		if(object instanceof StackFrameProxy) {
			StackFrameProxy proxy = (StackFrameProxy)object;
			String fullPath = proxy.getStackFrame().getPath();
			// need to remove workspace segment since we're using the workspace container  
			IPath path = new Path(fullPath);
			path = path.removeFirstSegments(1);
			return path.toPortableString();
		}
		return null;
	}

}
