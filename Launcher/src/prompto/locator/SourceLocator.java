package prompto.locator;

import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

public class SourceLocator extends AbstractSourceLookupDirector {

	@Override
	public void initializeParticipants() {
		ISourceLookupParticipant slp = new SourceLookupParticipant();
		addParticipants(new ISourceLookupParticipant[] { slp } );
		
	}

}
