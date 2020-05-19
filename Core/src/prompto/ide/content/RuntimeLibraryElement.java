package prompto.ide.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import prompto.ide.core.Plugin;
import prompto.ide.distribution.Distribution;
import prompto.ide.utils.ImageUtils;

public class RuntimeLibraryElement extends LibraryElement {

	public RuntimeLibraryElement(IProject parent) {
		super(parent);
	}
	
	@Override
	public IProject getProject() {
		return (IProject)getParent();
	}
	
	@Override
	public String getText() {
		return "Prompto Runtime";
	}	
	
	@Override
	public Image getImage() {
		return ImageUtils.load(Plugin.getDefault().getBundle(), "images/library.gif");
	}
	
	@Override
	public boolean hasChildren() throws CoreException {
		return true;
	}

	@Override
	public Object[] getChildren() throws CoreException {
		Distribution dist = Distribution.getDefaultDistribution();
		if(dist==null)
			return new Object[] { new MissingDistributionElement(this) };
		else
			return getRuntimeFiles(dist);
	}
	
	
	private Object[] getRuntimeFiles(Distribution dist) {
		Path runtimePath = Paths.get(dist.getDirectory(), "Runtime-0.0.1-SNAPSHOT.jar");
		if(!runtimePath.toFile().exists())
			return new Object[] { new MissingDistributionElement(this) };
		else
			return getRuntimeFiles(runtimePath.toFile());
	}

	private Object[] getRuntimeFiles(File runtimeJar) {
		try(InputStream input = new FileInputStream(runtimeJar)) {
			List<Object> children = new ArrayList<>();
			try(ZipInputStream zip = new ZipInputStream(input)) {
				ZipEntry entry = zip.getNextEntry();
				while(entry!=null) {
					if(entry.getName().startsWith("libraries/") && !entry.getName().endsWith("/"))
						children.add(new RuntimeFileElement(this, entry.getName().substring("libraries/".length())));
					entry = zip.getNextEntry();
				}
			}
			return children.toArray();
		} catch(IOException e) {
			return new Object[] { new CorruptDistributionElement(this) };
		}
	}

	static abstract class BrokenDistributionElement extends LibraryElement {
		
		public BrokenDistributionElement(RuntimeLibraryElement parent) {
			super(parent);
		}
		
		@Override
		public boolean hasChildren() throws CoreException {
			return false;
		}
		
		@Override
		public Object[] getChildren() throws CoreException {
			return new Object[0];
		}
		
		@Override
		public Image getImage() {
			return null;
		}
	}
	
	static class MissingDistributionElement extends BrokenDistributionElement {
		
		public MissingDistributionElement(RuntimeLibraryElement parent) {
			super(parent);
		}
		
		@Override
		public String getText() {
			return "No configured runtime!";
		}
	}
	
	
	static class CorruptDistributionElement extends BrokenDistributionElement {
		
		public CorruptDistributionElement(RuntimeLibraryElement parent) {
			super(parent);
		}
		
		@Override
		public String getText() {
			return "Corrupt runtime!";
		}
	}

	

}
