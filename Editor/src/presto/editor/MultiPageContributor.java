package presto.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import presto.grammar.DeclarationList;
import presto.parser.Dialect;
import presto.parser.IParser;
import presto.runtime.Context;
import presto.store.IEclipseCodeStore;
import presto.store.StoreUtils;
import presto.utils.CodeWriter;

/**
 * Manages the installation/deinstallation of global actions for multi-page editors.
 * Responsible for the redirection of global actions to the active editor.
 * Multi-page contributor replaces the contributors for the individual editors in the multi-page editor.
 */
public class MultiPageContributor extends MultiPageEditorActionBarContributor {

	private IEditorPart activeEditorPart;

    /**
     * Creates a multi-page contributor.
     */
    public MultiPageContributor() {
        super();
    }

    /**
     * Returns the action registed with the given text editor.
     * @return IAction or null if editor is null.
     */
    protected IAction getAction(ITextEditor editor, String actionID) {
        return (editor == null ? null : editor.getAction(actionID));
    }

    /* (non-JavaDoc)
     * Method declared in MultiPageEditorActionBarContributor.
     */
    public void setActivePage(IEditorPart part) {

    	if (activeEditorPart == part)
            return;
        activeEditorPart = part;
        setActionHandlers(part);
        translateIfRequired(part);
    }
    
    private void translateIfRequired(IEditorPart part) {
    	if(!(part instanceof SourceEditor))
    		return;
    	translateIfRequired((SourceEditor)part);
    }
    
    private void translateIfRequired(SourceEditor editor) {
    	IDocumentProvider dp = editor.getDocumentProvider();
    	if(!(dp instanceof TranslatedDocumentProvider))
    		return;
    	SourceEditor actual = editor.getActualSourceEditor();
    	translate(actual, editor);
    }
    
    private void translate(SourceEditor actual, SourceEditor target) {
    	IFile actualFile = actual.getFile();
    	Dialect actualDialect = actual.getDialect();
		IDocument actualDocument = actual.getDocument();
    	Dialect targetDialect = target.getDialect();
		IDocument targetDocument = target.getDocument();
		translate(actualFile, actualDialect, actualDocument, targetDialect, targetDocument);
	}

	private void translate(IFile actualFile, Dialect actualDialect, IDocument actualDocument, Dialect targetDialect, IDocument targetDocument) {
		String code = actualDocument.get();
		code = translate(code, actualFile, actualDialect, targetDialect);
		targetDocument.set(code);
	}

	private String translate(String actualCode, IFile actualFile, Dialect actualDialect, Dialect targetDialect) {
		try {
			IEclipseCodeStore store = StoreUtils.fetchStoreFor(actualFile);
			Context context = store.getContext();
			IParser parser = actualDialect.getParserFactory().newParser();
			InputStream input = new ByteArrayInputStream(actualCode.getBytes());
			DeclarationList dl = parser.parse(null, input);
			CodeWriter writer = new CodeWriter(targetDialect, context);
			dl.toDialect(writer);
			return writer.toString();
		} catch(Exception e) {
			return e.getMessage();
		}
	}

	private void setActionHandlers(IEditorPart part) {
		IActionBars actionBars = getActionBars();
        if (actionBars != null) {

            ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;

            actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
                    getAction(editor, ActionFactory.DELETE.getId()));
            actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
                    getAction(editor, ActionFactory.UNDO.getId()));
            actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
                    getAction(editor, ActionFactory.REDO.getId()));
            actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(),
                    getAction(editor, ActionFactory.CUT.getId()));
            actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
                    getAction(editor, ActionFactory.COPY.getId()));
            actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(),
                    getAction(editor, ActionFactory.PASTE.getId()));
            actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(),
                    getAction(editor, ActionFactory.SELECT_ALL.getId()));
            actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(),
                    getAction(editor, ActionFactory.FIND.getId()));
            actionBars.setGlobalActionHandler(
                    IDEActionFactory.BOOKMARK.getId(), getAction(editor,
                            IDEActionFactory.BOOKMARK.getId()));
            actionBars.setGlobalActionHandler(
                    IDEActionFactory.ADD_TASK.getId(), getAction(editor,
                            IDEActionFactory.ADD_TASK.getId()));
            actionBars.updateActionBars();
        }
    }
}
