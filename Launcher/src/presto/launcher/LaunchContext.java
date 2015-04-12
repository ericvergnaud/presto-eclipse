package presto.launcher;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import presto.core.Utils;
import presto.core.Utils.RunType;
import presto.declaration.IDeclaration;
import presto.error.SyntaxError;
import presto.grammar.DeclarationList;
import presto.parser.Dialect;
import presto.parser.IParser;
import presto.runtime.Context;

public class LaunchContext {

	ILaunch launch;
	ILaunchConfiguration configuration;
	RunType runType;
	IProject project;
	IFile file;
	IDeclaration method;
	String cmdLineArgs;
	boolean stopInMain;
	
	public LaunchContext(ILaunchConfiguration configuration, ILaunch launch) {
		this.configuration = configuration;
		this.launch = launch;
		readConfiguration();
	}
	
	public ILaunchConfiguration getConfiguration() {
		return configuration;
	}
	
	public RunType getRunType() {
		return runType;
	}

	public IProject getProject() {
		return project;
	}
	
	public ILaunch getLaunch() {
		return launch;
	}
	
	public IDeclaration getMethod() {
		return method;
	}
	
	public String getCmdLineArgs() {
		return cmdLineArgs;
	}
	
	public boolean isStopInMain() {
		return stopInMain;
	}
	
	private void readConfiguration() {
		runType = LaunchUtils.getConfiguredRunType(configuration);
		project = LaunchUtils.getConfiguredProject(configuration);
		file = LaunchUtils.getConfiguredFile(configuration, project);
		method = LaunchUtils.getConfiguredMethod(configuration, file);
		cmdLineArgs = LaunchUtils.getConfiguredCommandLineArguments(configuration);
		stopInMain = LaunchUtils.getConfiguredStopInMain(configuration);
	}

	public ContextMap buildContextMap(RunType runType) throws Exception {
		ContextMap cm = populateContextMap(project, runType);
		registerContextMap(cm);
		checkContextMap(cm);
		return cm;
	}
	
	private void checkContextMap(ContextMap cm) throws Exception {
		for(Entry<IFile, DeclarationList> entry : cm.map.entrySet()) try {
			entry.getValue().check(cm.getContext());
		} catch (SyntaxError e) {
			// TODO
			e.printStackTrace(System.err);
			throw e;
		}
	}

	private void registerContextMap(ContextMap cm) throws Exception {
		for(Entry<IFile, DeclarationList> entry : cm.map.entrySet()) try {
			entry.getValue().register(cm.getContext());
		} catch (SyntaxError e) {
			// TODO
			e.printStackTrace(System.err);
			throw e;
		}
	}

	private ContextMap populateContextMap(IProject project, RunType runType) throws Exception {
		ContextMap cm = new ContextMap();
		cm.setContext(Context.newGlobalContext());
		cm.map = parseEligibleFiles(project, runType);
		return cm;
	}

	private Map<IFile, DeclarationList> parseEligibleFiles(IProject project, RunType runType) throws Exception {
		Map<IFile,DeclarationList> map = new HashMap<IFile, DeclarationList>();
		for(IFile file : Utils.getEligibleFiles(project, runType)) 
			map.put(file, parse(file));
		return map;
	}

	private DeclarationList parse(IFile file) throws Exception {
		Dialect dialect = Utils.getDialect(file);
		try {
			IParser parser = dialect.getParserFactory().newParser();
			String path = file.getFullPath().toPortableString();
			InputStream input = file.getContents();
			return parser.parse(path, input);
		} catch (Exception e) {
			// TODO
			e.printStackTrace(System.err);
			throw e;
		}
	}


}
