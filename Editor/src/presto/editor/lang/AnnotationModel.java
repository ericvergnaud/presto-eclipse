package presto.editor.lang;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

import presto.core.Constants;

public class AnnotationModel extends ResourceMarkerAnnotationModel {

	public AnnotationModel(IResource resource) {
		super(resource);
	}

	@Override
	protected IMarker[] retrieveMarkers() throws CoreException {
		return getResource().findMarkers(Constants.DEBUG_MARKER_TYPE, true, IResource.DEPTH_ZERO);
	}
	
}
