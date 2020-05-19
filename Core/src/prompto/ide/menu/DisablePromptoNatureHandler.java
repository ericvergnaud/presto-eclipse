package prompto.ide.menu;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

public class DisablePromptoNatureHandler extends AbstractCommandHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IProject project = getProjectFromMenuEvent(event);
			IProjectDescription description = project.getDescription();
			List<String> natureIds = Arrays.stream(description.getNatureIds())
					.filter((s)->!s.startsWith("prompto.nature."))
					.collect(Collectors.toList());
			description.setNatureIds(natureIds.toArray(new String[natureIds.size()]));
			project.setDescription(description, null);
		} catch(CoreException e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}


}
