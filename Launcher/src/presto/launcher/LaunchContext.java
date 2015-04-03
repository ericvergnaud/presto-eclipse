package presto.launcher;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import presto.declaration.IMethodDeclaration;
import presto.error.SyntaxError;
import presto.grammar.DeclarationList;
import presto.parser.Dialect;
import presto.parser.IParser;
import presto.runtime.Context;

public class LaunchContext {

	ILaunch launch;
	ILaunchConfiguration configuration;
	IProject project;
	IFile file;
	IMethodDeclaration method;
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
	
	public IProject getProject() {
		return project;
	}
	
	public ILaunch getLaunch() {
		return launch;
	}
	
	public IMethodDeclaration getMethod() {
		return method;
	}
	
	public String getCmdLineArgs() {
		return cmdLineArgs;
	}
	
	public boolean isStopInMain() {
		return stopInMain;
	}
	
	private void readConfiguration() {
		project = Utils.getConfiguredProject(configuration);
		file = Utils.getConfiguredFile(configuration, project);
		method = Utils.getConfiguredMethod(configuration, file);
		cmdLineArgs = Utils.getConfiguredCommandLineArguments(configuration);
		stopInMain = Utils.getConfiguredStopInMain(configuration);
	}

	public ContextMap buildContextMap() throws Exception {
		ContextMap cm = populateContextMap(project);
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

	private ContextMap populateContextMap(IProject project) throws Exception {
		ContextMap cm = new ContextMap();
		cm.setContext(Context.newGlobalContext());
		cm.map = parseEligibleFiles(project);
		return cm;
	}

	private Map<IFile, DeclarationList> parseEligibleFiles(IProject project) throws Exception {
		Map<IFile,DeclarationList> map = new HashMap<IFile, DeclarationList>();
		for(IFile file : Utils.getEligibleFiles(project)) 
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
