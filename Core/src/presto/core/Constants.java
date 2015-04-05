package presto.core;

public abstract class Constants {

	public static final String CORE_PLUGIN_ID = "presto.core"; 
	public static final String EDITOR_PLUGIN_ID = "presto.editor"; 

	public static final String PRESTO_NATURES_ID = "presto.natures";
	public static final String SCRIPTS_NATURE_ID = "presto.nature.scripts";
	public static final String LIBRARY_NATURE_ID = "presto.nature.library";
	public static final String APPLICATION_NATURE_ID = "presto.nature.application";

	public static final String NEW_SCRIPTS_PROJECT = "New Presto Scripts Project";
	public static final String NEW_LIBRARY_PROJECT = "New Presto Library Project";
	public static final String NEW_APPLICATION_PROJECT = "New Presto Application Project";
	public static final String CREATING_PROJECT = "Creating project...";
	public static final String CREATING_SAMPLE_SCRIPT = "Creating sample script...";
	public static final String CREATING_SAMPLE_APPLICATION = "Creating sample application...";
	public static final String NEW_PROJECT_ERROR = "Error while creating project";
	public static final String VARIANT_PROJECT_EXISTS = "Another project with same name but different case already exists!";
	public static final String SCRIPTS_PROJECT_DESCRIPTION = "A Presto project where each file is a standalone script";
	public static final String LIBRARY_PROJECT_DESCRIPTION = "A Presto project for reusable attributes, categories and methods";
	public static final String APPLICATION_PROJECT_DESCRIPTION = "A Presto project for a runnable aplication";

	public static final String PRESTO_MARKER_TYPE = "presto.marker";

	public static final String PROBLEM_MARKER_TYPE = "presto.problem.marker";
	public static final String DEBUG_MARKER_TYPE = "presto.debug.marker";

	public static final String DEBUG_MODEL_IDENTIFIER = "presto.debug"; 

	public static final String EDITOR_ID = "presto.editor.$";

	public static final String LIBRARY_PROJECT_REFERENCES = "Referenced libraries";
	public static final String SELECT_PROJECT_REFERENCES = "Select libraries required by this project";
}
