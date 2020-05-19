package prompto.ide.menu;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

public abstract class EnablePromptoNatureHandler extends AbstractCommandHandler {

	String nature;
	
	protected EnablePromptoNatureHandler(String nature) {
		this.nature = nature;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IProject project = getProjectFromMenuEvent(event);
			IProjectDescription description = project.getDescription();
			if(!description.hasNature(nature)) {
				List<String> natureIds = Arrays.stream(description.getNatureIds())
					.collect(Collectors.toList());
				natureIds.add(nature);
				description.setNatureIds(natureIds.toArray(new String[natureIds.size()]));
				project.setDescription(description, null);
			}
		} catch(CoreException e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}

}
