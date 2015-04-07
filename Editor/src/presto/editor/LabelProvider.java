package presto.editor;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import presto.editor.ContentProvider.Element;

public class LabelProvider extends BaseLabelProvider implements ILabelProvider {

	@Override
	public String getText(Object element) {
		return element==null ? "" : element.toString();
	}
	
	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return element==null ? null : ((Element)element).getImage();
	}
}
