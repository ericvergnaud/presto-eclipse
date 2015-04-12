package presto.editor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

import presto.core.CoreConstants;

public class AnnotationModel extends ResourceMarkerAnnotationModel {

	public AnnotationModel(IResource resource) {
		super(resource);
	}

	@Override
	protected IMarker[] retrieveMarkers() throws CoreException {
		return getResource().findMarkers(CoreConstants.DEBUG_MARKER_TYPE, true, IResource.DEPTH_ZERO);
	}
	
}
