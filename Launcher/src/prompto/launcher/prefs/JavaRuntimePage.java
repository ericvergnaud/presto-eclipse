package prompto.launcher.prefs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.Preferences;

import prompto.distribution.Artifact;
import prompto.distribution.Distribution;
import prompto.distribution.Version;
import prompto.launcher.Plugin;

public class JavaRuntimePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	TableViewer viewer;
	Set<Distribution> distributions = new TreeSet<>();
	
	public JavaRuntimePage() {
	}

	public JavaRuntimePage(String title) {
		super(title);
	}

	public JavaRuntimePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void init(IWorkbench workbench) {
		Preferences prefs = Plugin.getPreferences();
		String pref = prefs.get(Initializer.PROMPTO_DISTRIBUTION_JAVA_LIST, "");
		Collection<Distribution> dists = Distribution.fromPrefsString(pref);
		distributions.addAll(dists);
	}

	@Override
	protected Control createContents(Composite parent) {
		return createMainControl(parent);
	}
	
	@Override
	protected void performApply() {
		performOk();
	}
	
	@Override
	public boolean performOk() {
		String dists = Distribution.toPrefsString(distributions);
		Preferences prefs = Plugin.getPreferences();
		prefs.put(Initializer.PROMPTO_DISTRIBUTION_JAVA_LIST, dists);
		try {
			prefs.flush();
			return true;
		} catch(Exception e) {
			e.printStackTrace(System.err);
			return false;
		}
	}

	private Composite createMainControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		control.setLayout(layout);
		createTableViewer(control);
		Composite buttons = new Composite(control, SWT.NONE);
		buttons.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));
		layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);
		createAddSnapshotButton(buttons);
		createAddButton(buttons); 
		createRemoveButton(buttons);
		populateTableViewer();
		return control;
	}

	private void populateTableViewer() {
		viewer.getTable().clearAll();
		distributions.forEach((d)->{
			TableItem item = new TableItem(viewer.getTable(), SWT.NULL);
			item.setText(0, d.getVersion().toString());
			item.setText(1, d.getDirectory());
		});
	}

	private void createAddSnapshotButton(Composite parent) {
		Button button = new Button(parent, SWT.NONE);
		GridData gd = new GridData();
		gd.widthHint = 150;
		button.setLayoutData(gd);
		button.setText("Add SNAPSHOT");
		button.addSelectionListener(selectionListener(this::onAddSnapshot));
	}
	
	SelectionListener selectionListener(Consumer<SelectionEvent> ce) {
		return new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ce.accept(e);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
	}
	
	void onAddSnapshot(SelectionEvent e) {
		try {
			Artifact artifact = new Artifact("org.prompto", "Server", "0.0.1-SNAPSHOT", "jar");
			if(artifactExistsLocally(artifact)) {
				deleteDistribution(artifact.getVersion());
				createDistributionFromLocalPom(artifact);
				registerDistribution(artifact.getVersion());
				populateTableViewer();
			} else {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MessageBox alert = new MessageBox(shell, SWT.ICON_ERROR | SWT.ABORT);
				alert.setText("Error");
				alert.setMessage("Could not locate 'Prompto platform' artifact in local Maven repository.\n"
						+ "Run 'mvn install' on 'Prompto platform' project and try again.");
				alert.open();	
			}
		} catch(Throwable t) {
			t.printStackTrace(System.err);
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageBox alert = new MessageBox(shell, SWT.ICON_ERROR | SWT.ABORT);
			alert.setText("Error");
			alert.setMessage("Could not create distribution: " + t.getMessage());
			alert.open();	
		}
	}
	

	private void registerDistribution(String version) {
		Path dest = getDistributionFolder(version);
		Distribution dist = new Distribution(Version.parse(version), dest.toString());
		distributions.add(dist);	
	}

	private void deleteDistribution(String version) throws Exception {
		Path dest = getDistributionFolder(version);
		deleteRecursive(dest.toFile());
	}
	
	
	private void deleteRecursive(File file) {
		if(file.exists()) {
			if(file.isDirectory()) {
				for(String name : file.list())
					deleteRecursive(new File(file, name));
			}
			file.delete();
		}
	}

	private Path createDistributionFromLocalPom(Artifact artifact) throws Exception {
		Path dest = getDistributionFolder(artifact.getVersion());
		copyLocalArtifactToDistributionFolder(artifact, dest);
		copyLocalArtifactDependenciesToDistributionFolder(artifact, dest);
		return dest;
		
	}

	private void copyLocalArtifactToDistributionFolder(Artifact artifact, Path dest) throws Exception {
		File source = getSource(artifact);
		dest = dest.resolve(source.toPath().getFileName());
		Files.copy(source.toPath(), dest);
	}

	@SuppressWarnings("deprecation")
	private void copyLocalArtifactDependenciesToDistributionFolder(Artifact artifact, Path dest) throws Exception {
		System.out.println("Creating Prompto distribution at " + dest.toString());
		File sourcePom = getSourcePom(artifact);
		Properties userProps = new Properties();
		userProps.put("outputDirectory", dest.toString());
		MavenExecutionRequest request = MavenPlugin.getMaven().createExecutionRequest(null)
				.setBaseDirectory(dest.toFile())
				.setPom(sourcePom)
				.setUserProperties(userProps)
				.setGoals(Collections.singletonList("dependency:copy-dependencies"));
		MavenPlugin.getMaven().execute(request, null);
	}

	private File getSourcePom(Artifact artifact) throws CoreException {
		return getSource(artifact.withType("pom"));
	}
	
	private File getSource(Artifact artifact) throws CoreException {
			IMaven maven = MavenPlugin.getMaven();
		ArtifactRepository repo = maven.getLocalRepository();
		File repoDir = new File(repo.getBasedir());
		String sourcePath = maven.getArtifactPath(repo, artifact.getGroupId(), artifact.getArtifactId(), 
				artifact.getVersion(), artifact.getType(), null);
		return new File(repoDir, sourcePath);
	}

	private Path getDistributionFolder(String version) {
		File dest = new File(System.getProperty("java.io.tmpdir"), "Prompto/Java/" + version + "/");
		dest.mkdirs();
		return dest.toPath();
	}

	private boolean artifactExistsLocally(Artifact artifact) throws CoreException {
		return !MavenPlugin.getMaven().isUnavailable(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), 
				artifact.getType(), null, Collections.emptyList());
	}

	private void createAddButton(Composite parent) {
		Button button = new Button(parent, SWT.NONE);
		GridData gd = new GridData();
		gd.widthHint = 80;
		button.setLayoutData(gd);
		button.setText("Add...");
	}

	private void createRemoveButton(Composite parent) {
		Button button = new Button(parent, SWT.NONE);
		GridData gd = new GridData();
		gd.widthHint = 80;
		button.setLayoutData(gd);
		button.setText("Remove");
	}

	private void createTableViewer(Composite parent) {
		int options = SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION;
		viewer = new TableViewer(parent, options );
		Table table = viewer.getTable();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.verticalSpan = 8;
		data.horizontalSpan = 2;
		table.setLayoutData(data);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText("version");
		column.setWidth(75);
		column = new TableColumn(table, SWT.LEFT);
		column.setText("location");
		column.setWidth(350);
	}
}
