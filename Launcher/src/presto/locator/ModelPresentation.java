package presto.locator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import presto.core.Constants;
import presto.parser.Dialect;

public class ModelPresentation extends LabelProvider implements IDebugModelPresentation {

	@Override
	public IEditorInput getEditorInput(Object element) {
      if (element instanceof IFile)
          return new FileEditorInput((IFile)element);
       if (element instanceof ILineBreakpoint)
          return new FileEditorInput((IFile)((ILineBreakpoint)element).getMarker().getResource());
       return null;
 	}

	@Override
	public String getEditorId(IEditorInput input, Object element) {
		IResource resource = null;
		if (element instanceof IFile)
			resource = (IFile)element;
		else if(element instanceof ILineBreakpoint)
			resource = ((ILineBreakpoint)element).getMarker().getResource();
		if(resource!=null) {
			String extension = resource.getFileExtension();
			try {
				Dialect.valueOf(extension.toUpperCase());
				return Constants.EDITOR_ID.replace("$", extension);
			} catch(Exception e) {
				//nothing to do
			}
		}
		return null;
	}

	@Override
	public void setAttribute(String attribute, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void computeDetail(IValue value, IValueDetailListener listener) {
		// TODO Auto-generated method stub
		
	}


}
