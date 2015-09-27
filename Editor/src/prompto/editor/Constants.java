package prompto.editor;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import prompto.utils.ImageUtils;

public class Constants {

	public static final String BUILDER_ID = "prompto.builder";
	public static final String PARTITION_ID = "prompto.partition";
	public static final String TYPE_PARTITION_NAME = "prompto.partition.type";
	public static final String KEYWORD_PARTITION_NAME = "prompto.partition.keyword";
	public static final String SYMBOL_PARTITION_NAME = "prompto.partition.symbol";
	public static final String LITERAL_PARTITION_NAME = "prompto.partition.literal";
	public static final String BRANCH_PARTITION_NAME = "prompto.partition.branch";
	public static final String LOOP_PARTITION_NAME = "prompto.partition.loop";
	public static final String COMMENT_PARTITION_NAME = "prompto.partition.comment";
	public static final String OTHER_PARTITION_NAME = "prompto.partition.other";
	public static final String[] PARTITION_NAMES = {
									TYPE_PARTITION_NAME,
									KEYWORD_PARTITION_NAME,
									SYMBOL_PARTITION_NAME,
									LITERAL_PARTITION_NAME,
									BRANCH_PARTITION_NAME,
									LOOP_PARTITION_NAME,
									COMMENT_PARTITION_NAME,
									OTHER_PARTITION_NAME
									};
	
	public static final Image ATTRIBUTE_ICON = loadImage("/images/attribute_obj.gif");
	public static final Image CATEGORY_ICON = loadImage("/images/category_obj.gif");
	public static final Image ENUMERATED_ICON = loadImage("/images/enumerated_obj.gif");
	public static final Image SYMBOL_ICON = loadImage("/images/symbol_obj.gif");
	public static final Image METHOD_ICON = loadImage("/images/method_obj.gif");
	
	private static Image loadImage(String path) {
		Bundle bundle = Plugin.getDefault().getBundle();
		return ImageUtils.load(bundle, path);
	}

}
