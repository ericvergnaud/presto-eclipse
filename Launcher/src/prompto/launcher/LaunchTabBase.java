package prompto.launcher;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import prompto.declaration.IDeclaration;
import prompto.declaration.IMethodDeclaration;
import prompto.declaration.TestMethodDeclaration;
import prompto.ide.core.RunType;
import prompto.ide.utils.CoreUtils;
import prompto.ide.utils.ImageUtils;
import prompto.ide.utils.RcpUtils;
import prompto.ide.utils.ShellUtils;

public abstract class LaunchTabBase extends AbstractLaunchConfigurationTab {

	Text serverText;
	Combo projectCombo;
	Combo fileCombo;
	Combo methodCombo;
	Button stopInMainButton;
	
	protected abstract RunType getRunType();
	protected abstract boolean supportsMethodSelector();
	protected abstract boolean requiresSelectedMethod();
	protected boolean supportsServerPort() { return false; };

	
	@Override
	public void createControl(Composite parent) {
		Control root = createRoot(parent);
		setControl(root);
	}

	private Control createRoot(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(1, false));
		if(supportsServerPort()) {
			Control child = createPortSelectorGroup(root);
			child.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		}
		Control child = createMethodSelectorGroup(root);
		child.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		return root;
	}
	
	private Control createPortSelectorGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setText("Server port");
		serverText = new Text(group, SWT.SINGLE | SWT.BORDER);
		serverText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		return group;
	}

	private Control createMethodSelectorGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setText("Project/File/Start method");
		Control child = createLabel(group, "Project:");
		child.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		child = createProjectSelector(group);
		child.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		child = createLabel(group, "File:");
		child.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		child = createFileSelector(group);
		child.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		child = createLabel(group, "Start method:");
		child.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		if(supportsMethodSelector()) {
			child = createMethodSelector(group);
			child.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		}
		child = createStopInMainCheckbox(group);
		child.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		return group;
	}

	private Control createProjectSelector(Composite parent) {
		projectCombo = new Combo(parent, SWT.READ_ONLY);
		projectCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
				fillInFiles();
				if(supportsMethodSelector())
					fillInMethods();
				manageControls();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		return projectCombo;
	}


	private Control createFileSelector(Composite parent) {
		fileCombo = new Combo(parent, SWT.READ_ONLY);
		fileCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
				if(supportsMethodSelector())
					fillInMethods();
				manageControls();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		return fileCombo;
	}

	private Control createMethodSelector(Composite parent) {
		methodCombo = new Combo(parent, SWT.READ_ONLY);
		methodCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
				manageControls();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		return methodCombo;
	}

	private Control createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	private Control createStopInMainCheckbox(Composite parent) {
		stopInMainButton = new Button(parent, SWT.CHECK);
		stopInMainButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
				manageControls();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		stopInMainButton.setText("Stop in start method");
		return stopInMainButton;
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	private void fillInProjects() {
		projectCombo.setItems(new String[0]);
		for(IProject project : ShellUtils.getRoot().getProjects()) {
			if(isEligibleProject(project))
				projectCombo.add(project.getName());
		}
	}

	private boolean isEligibleProject(IProject project) {
		try {
			String nature = getRunType().getNature();
			return nature==null || project.hasNature(nature);
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void fillInFiles() {
		fillInFiles(getSelectedProject());
	}
	
	protected IProject getSelectedProject() {
		int idx = projectCombo.getSelectionIndex();
		if(idx<0)
			return null;
		else
			return ShellUtils.getRoot().getProject(projectCombo.getItem(idx));
	}

	private void fillInFiles(IProject project) {
		fileCombo.setItems(new String[0]);
		Set<IFile> files = CoreUtils.getEligibleFiles(project, getRunType());
		for(IFile file : files)
			fileCombo.add(file.getName());
	}
	
	private void fillInMethods() {
		fillInMethods(getSelectedFile(getSelectedProject()));
	}



	private void fillInMethods(IFile file) {
		if(methodCombo!=null) {
			methodCombo.setItems(new String[0]);
			List<? extends IDeclaration> methods = getEligibleMethods(file);
			for(IDeclaration method : methods)
				methodCombo.add(getMethodSignature(file, method));
		}
	}
	
	protected IFile getSelectedFile(IProject project) {
		if(project==null)
			return null;
		int idx = fileCombo.getSelectionIndex();
		if(idx<0)
			return null;
		String file = fileCombo.getItem(idx);
		return CoreUtils.getEligibleFiles(project, getRunType())
				.stream()
				.filter((f)->file.equals(f.getName()))
				.findFirst()
				.orElse(null);
	}

	private void manageControls() {
		setErrorMessage(null);
		projectCombo.setEnabled(projectCombo.getItemCount()>0);
		int idx = projectCombo.getSelectionIndex();
		if(idx==-1 && getErrorMessage()==null)
			setErrorMessage("No project selected");
		fileCombo.setEnabled(idx>=0);
		idx = fileCombo.getSelectionIndex();
		if(idx==-1 && getErrorMessage()==null)
			setErrorMessage("No file selected");
		if(supportsMethodSelector()) {
			methodCombo.setEnabled(idx>=0);
			if(requiresSelectedMethod()) {
				idx = methodCombo.getSelectionIndex();
				if(idx==-1 && getErrorMessage()==null)
					setErrorMessage("No method selected");
			}
		}
		updateLaunchConfigurationDialog();
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		fillInServerPort(configuration);
		fillInProjects();
		IProject project = selectProject(configuration);
		fillInFiles(project);
		IFile file = selectFile(configuration, project);
		fillInMethods(file);
		selectMethod(configuration, file);
		selectStopInMain(configuration);
		manageControls();
	}
	
	private void fillInServerPort(ILaunchConfiguration configuration) {
		if(serverText!=null) try {
			serverText.setText(configuration.getAttribute(LauncherConstants.HTTP_PORT, "8080"));
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
	}

	private void selectStopInMain(ILaunchConfiguration configuration) {
		try {
			stopInMainButton.setSelection(configuration.getAttribute(LauncherConstants.STOP_IN_MAIN, true));
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
	}

	private void selectMethod(ILaunchConfiguration configuration, IFile file) {
		IDeclaration method = LaunchUtils.getConfiguredMethod(configuration, file);
		if(method!=null && methodCombo.getItemCount()>0) 
			RcpUtils.selectInCombo(methodCombo, getMethodSignature(file, method));
	}

	private IFile selectFile(ILaunchConfiguration configuration, IProject project) {
		IFile file = LaunchUtils.getConfiguredFile(configuration, project);
		if(fileCombo.getItemCount()>0 && file!=null)
			RcpUtils.selectInCombo(fileCombo,file.getName());
		return file;
	}

	private IProject selectProject(ILaunchConfiguration configuration) {
		IProject project = LaunchUtils.getConfiguredProject(configuration);
		if(projectCombo.getItemCount()>0 && project!=null)
			RcpUtils.selectInCombo(projectCombo,project.getName());
		return project;
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		return !requiresSelectedMethod() || methodCombo.getSelectionIndex()>=0;
	}
	
	@Override
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		// nothing to do
	}

	protected IDeclaration getSelectedMethod(IFile file) {
		if(file==null || methodCombo==null)
			return null;
		int idx = methodCombo.getSelectionIndex();
		if(idx<0)
			return null;
		return getEligibleMethods(file).get(idx);
	}

	private List<? extends IDeclaration> getEligibleMethods(IFile file) {
		switch(getRunType()) {
		case APPLI:
		case SERVER:
			return CoreUtils.getEligibleMainMethods(file);
		case TEST:
			return CoreUtils.getEligibleTestMethods(file);
		default:
			return null;
		}
	}

	protected String getProjectName(IProject project) {
		return project==null ? null : project.getName();
	}

	@Override
	public String getName() {
		return "Main";
	}

	@Override
	public Image getImage() {
		return ImageUtils.load(Plugin.getDefault().getBundle(),"images/configMain.png");
	}
	
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if(serverText!=null)
			configuration.setAttribute(LauncherConstants.HTTP_PORT, serverText.getText());
		configuration.setAttribute(LauncherConstants.RUNTYPE, getRunType().name());
		IProject project = getSelectedProject();
		configuration.setAttribute(LauncherConstants.PROJECT, getProjectName(project));
		IFile file = getSelectedFile(project);
		configuration.setAttribute(LauncherConstants.FILE, getFileName(file));
		IDeclaration method = getSelectedMethod(file);
		configuration.setAttribute(LauncherConstants.METHOD, getMethodSignature(file, method));
		configuration.setAttribute(LauncherConstants.STOP_IN_MAIN, stopInMainButton.getSelection());
	}

	private String getMethodSignature(IFile file, IDeclaration method) {
		if(method instanceof IMethodDeclaration)
			return CoreUtils.getMethodSignature((IMethodDeclaration)method, CoreUtils.getDialect(file));
		else if(method instanceof TestMethodDeclaration)
			return method.getName();
		else
			return null;
	}

	private String getFileName(IFile file) {
		return file==null ? null : ShellUtils.getFilePath(file);
	}


}
