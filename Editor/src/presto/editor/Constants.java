package presto.editor;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import presto.utils.ImageUtils;

public class Constants {

	public static final String NATURE_ID = "presto.nature";
	public static final String BUILDER_ID = "presto.builder";
	public static final String PARTITION_ID = "presto.partition";
	public static final String TYPE_PARTITION_NAME = "presto.partition.type";
	public static final String KEYWORD_PARTITION_NAME = "presto.partition.keyword";
	public static final String SYMBOL_PARTITION_NAME = "presto.partition.symbol";
	public static final String LITERAL_PARTITION_NAME = "presto.partition.literal";
	public static final String BRANCH_PARTITION_NAME = "presto.partition.branch";
	public static final String LOOP_PARTITION_NAME = "presto.partition.loop";
	public static final String COMMENT_PARTITION_NAME = "presto.partition.comment";
	public static final String OTHER_PARTITION_NAME = "presto.partition.other";
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
