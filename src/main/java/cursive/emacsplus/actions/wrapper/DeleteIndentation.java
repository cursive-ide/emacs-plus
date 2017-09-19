package cursive.emacsplus.actions.wrapper;

import com.intellij.codeInsight.editorActions.JoinLinesHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.mulgasoft.emacsplus.actions.wrapper.EmacsPlusWrapper;
import com.mulgasoft.emacsplus.util.ActionUtil;

/**
 * @author Colin Fleming
 */
public class DeleteIndentation extends EmacsPlusWrapper {
  public DeleteIndentation() {
    super(new DeleteIndentation.Handler());
  }

  public static class Handler extends EditorWriteActionHandler {
    private final EditorActionHandler wrappedHandler = new JoinLinesHandler(getWrappedHandler());

    Handler() {
      super(true);
    }

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      if (caret != null) {
        Document document = editor.getDocument();
        int cl = caret.getLogicalPosition().line;
        int currentLine = document.getLineNumber(caret.getOffset());
        if (currentLine > 0) {
          --currentLine;
          caret.moveToOffset(document.getLineEndOffset(currentLine));
          wrappedHandler.execute(editor, caret, dataContext);
        }
      }

    }

    static EditorActionHandler getWrappedHandler() {
      EditorActionHandler handler = null;
      AnAction action = ActionUtil.getInstance().getAction("EditorJoinLines");
      if (action instanceof EditorAction) {
        handler = ((EditorAction) action).getHandler();
      }

      return handler;
    }
  }
}
