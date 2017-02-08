package prompto.launcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import prompto.code.IEclipseCodeStore;
import prompto.runner.Runner;

public interface ILaunchHelper {

	String getRunTypeName();
	Collection<IFile> getSourceFiles(LaunchContext context) throws CoreException;
	String getTargetJar(LaunchContext context);
	List<String> getTargetSpecifiers(LaunchContext context);
	default Collection<String> getCommandLineArgs(LaunchContext context) {
		return Runner.getCommandLineArgs(context);
	}

	static class Test implements ILaunchHelper {
		
		@Override
		public String getRunTypeName() {
			return "Prompto Test";
		}

		@Override
		public String getTargetJar(LaunchContext context) {
			return "Standalone-0.0.1-SNAPSHOT.jar";
		};
		
		@Override
		public List<String> getTargetSpecifiers(LaunchContext context) {
			return Arrays.asList("-test", context.getMethod().getName());
		}
		
		@Override
		public Collection<IFile> getSourceFiles(LaunchContext context) throws CoreException {
			IEclipseCodeStore store = context.getCodeStore();
			return store.getFiles();
		}
		
		@Override
		public Collection<String> getCommandLineArgs(LaunchContext context) {
			return Collections.emptyList();
		}
	}
	
	static class Application implements ILaunchHelper {
		
		@Override
		public String getRunTypeName() {
			return "Prompto Application";
		}

		@Override
		public String getTargetJar(LaunchContext context) {
			return "Standalone-0.0.1-SNAPSHOT.jar";
		};
		
		@Override
		public List<String> getTargetSpecifiers(LaunchContext context) {
			return Arrays.asList("-application", Runner.getProjectName(context),
					"-mainMethod", context.getMethod().getName());
		}
		
		@Override
		public Collection<IFile> getSourceFiles(LaunchContext context) throws CoreException {
			IEclipseCodeStore store = context.getCodeStore();
			return store.getFiles();
		}

	}
	
	static class Script implements ILaunchHelper {
		
		@Override
		public String getRunTypeName() {
			return "Prompto Script";
		}

		@Override
		public String getTargetJar(LaunchContext context) {
			return "Standalone-0.0.1-SNAPSHOT.jar";
		}
		
		
		@Override
		public List<String> getTargetSpecifiers(LaunchContext context) {
			return Arrays.asList("-script", context.getFile().getName());
		}
		
		@Override
		public Collection<IFile> getSourceFiles(LaunchContext context) throws CoreException {
			return Arrays.asList(context.getFile());
		}
	}
	
	static class Server implements ILaunchHelper {
		
		@Override
		public String getRunTypeName() {
			return "Prompto Server";
		}

		@Override
		public String getTargetJar(LaunchContext context) {
			return "Server-0.0.1-SNAPSHOT.jar";
		};
		
		@Override
		public List<String> getTargetSpecifiers(LaunchContext context) {
			return Arrays.asList("-application", Runner.getProjectName(context),
					"-http_port", context.getHttpPort(),
					"-serverAboutToStart", context.getMethod().getName());
		}


		@Override
		public Collection<IFile> getSourceFiles(LaunchContext context) throws CoreException {
			IEclipseCodeStore store = context.getCodeStore();
			return store.getFiles();
		}
	}
}
