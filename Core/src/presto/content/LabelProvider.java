package presto.content;

import org.eclipse.swt.graphics.Image;

public class LabelProvider extends org.eclipse.jface.viewers.LabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof ILibraryElement)
			return ((ILibraryElement)element).getText();
		else
			return element.getClass().getSimpleName();
	}
	
	@Override
	public Image getImage(Object element) {
		if(element instanceof ILibraryElement)
			return ((ILibraryElement)element).getImage();
		else
			return null;
	}
	
}
