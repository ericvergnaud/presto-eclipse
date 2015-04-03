package presto.debugger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import presto.core.Constants;
import presto.parser.ISection;


public class LineBreakpoint extends org.eclipse.debug.core.model.LineBreakpoint {

	public LineBreakpoint() {
	}
	
	public LineBreakpoint(IProject project, ISection section) throws CoreException {
		IMarker marker = DebuggerUtils.createMarker(project, section);
		setMarker(marker);
	}
	
	public LineBreakpoint(IResource resource, int lineNumber) throws CoreException {
		IMarker marker = DebuggerUtils.createMarker(resource, lineNumber);
		setMarker(marker);
	}

	@Override
	public String getModelIdentifier() {
		return Constants.DEBUG_MODEL_IDENTIFIER;
	}
	
	public String getPath() {
		return getMarker().getResource().getFullPath().toPortableString();
	}
	
	@Override
	public boolean equals(Object item) {
		if(item==this)
			return true;
		if(!(item instanceof LineBreakpoint))
			return false;
		LineBreakpoint plb = (LineBreakpoint)item;
		if(!getMarker().getResource().equals(plb.getMarker().getResource()))
			return false;
		try {
			return this.getLineNumber()==plb.getLineNumber();
		} catch (CoreException e) {
			return false;
		}
	}

	public IResource getResource() {
		return getMarker().getResource();
	}

}