package presto.utils;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class ImageUtils {

	public static Image load(Bundle bundle, String path) {
		IPath ipath = new Path(path);
		URL url = FileLocator.find(bundle, ipath, null);
		return ImageDescriptor.createFromURL(url).createImage();
	}
}
