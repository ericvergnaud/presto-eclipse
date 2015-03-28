package presto.launcher;

import java.util.List;

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

import presto.utils.ImageUtils;
import core.grammar.MethodDeclaration;

public class ConfigMainTab extends AbstractLaunchConfigurationTab {

	Combo projectCombo;
	Combo fileCombo;
	Combo methodCombo;
	Button stopInMainButton;
	
	@Override
	public void createControl(Composite parent) {
		Control root = createRoot(parent);
		setControl(root);
	}

	private Control createRoot(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(1, false));
		Control child = createSelectorGroup(root);
		child.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		return root;
	}

	private Control createSelectorGroup(Composite parent) {
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
		child = createMethodSelector(group);
		child.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
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
		// nothing to do
	}

	private void fillInProjects() {
		projectCombo.setItems(new String[0]);
		for(IProject project : Utils.getRoot().getProjects())
			projectCombo.add(project.getName());
	}

	private void fillInFiles() {
		fillInFiles(getSelectedProject());
	}
	
	private IProject getSelectedProject() {
		int idx = projectCombo.getSelectionIndex();
		if(idx<0)
			return null;
		else
			return Utils.getRoot().getProject(projectCombo.getItem(idx));
	}

	private void fillInFiles(IProject project) {
		fileCombo.setItems(new String[0]);
		List<IFile> files = Utils.getEligibleFiles(project);
		for(IFile file : files)
			fileCombo.add(file.getName());
	}
	
	private void fillInMethods() {
		fillInMethods(getSelectedFile(getSelectedProject()));
	}



	private void fillInMethods(IFile file) {
		methodCombo.setItems(new String[0]);
		List<MethodDeclaration> methods = Utils.getEligibleMethods(file);
		for(MethodDeclaration method : methods)
			methodCombo.add(method.getName());
	}
	
	private IFile getSelectedFile(IProject project) {
		if(project==null)
			return null;
		int idx = fileCombo.getSelectionIndex();
		if(idx<0)
			return null;
		return Utils.getEligibleFiles(project).get(idx);
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
		methodCombo.setEnabled(idx>=0);
		idx = methodCombo.getSelectionIndex();
		if(idx==-1 && getErrorMessage()==null)
			setErrorMessage("No method selected");
		updateLaunchConfigurationDialog();
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		fillInProjects();
		IProject project = selectProject(configuration);
		fillInFiles(project);
		IFile file = selectFile(configuration, project);
		fillInMethods(file);
		selectMethod(configuration, file);
		selectStopInMain(configuration);
		manageControls();
	}
	
	private void selectStopInMain(ILaunchConfiguration configuration) {
		try {
			stopInMainButton.setSelection(configuration.getAttribute(Constants.STOP_IN_MAIN, true));
		} catch (CoreException e) {
			e.printStackTrace(System.err);
		}
	}

	private void selectMethod(ILaunchConfiguration configuration, IFile file) {
		MethodDeclaration method = Utils.getConfiguredMethod(configuration, file);
		if(method!=null && methodCombo.getItemCount()>0) 
			Utils.selectInCombo(methodCombo,method.getName());
	}

	private IFile selectFile(ILaunchConfiguration configuration, IProject project) {
		IFile file = Utils.getConfiguredFile(configuration, project);
		if(fileCombo.getItemCount()>0 && file!=null)
			Utils.selectInCombo(fileCombo,file.getName());
		return file;
	}

	private IProject selectProject(ILaunchConfiguration configuration) {
		IProject project = Utils.getConfiguredProject(configuration);
		if(projectCombo.getItemCount()>0 && project!=null)
			Utils.selectInCombo(projectCombo,project.getName());
		return project;
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		return methodCombo.getSelectionIndex()>=0;
	}
	
	@Override
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		// nothing to do
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		IProject project = getSelectedProject();
		configuration.setAttribute(Constants.PROJECT, getProjectName(project));
		IFile file = getSelectedFile(project);
		configuration.setAttribute(Constants.FILE, file==null ? null : Utils.getFilePath(file));
		MethodDeclaration method = getSelectedMethod(file);
		String signature = method==null ? null : Utils.getMethodSignature(method, Utils.getDialect(file));
		configuration.setAttribute(Constants.METHOD, signature);
		configuration.setAttribute(Constants.STOP_IN_MAIN, stopInMainButton.getSelection());
	}

	private MethodDeclaration getSelectedMethod(IFile file) {
		if(file==null)
			return null;
		int idx = methodCombo.getSelectionIndex();
		if(idx<0)
			return null;
		return Utils.getEligibleMethods(file).get(idx);
	}

	private String getProjectName(IProject project) {
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
}
