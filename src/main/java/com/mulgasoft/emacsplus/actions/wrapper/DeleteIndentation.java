package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.codeInsight.editorActions.JoinLinesHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;

public class DeleteIndentation extends EmacsPlusWrapper {
  public DeleteIndentation() {
    super(new DeleteIndentation.Handler());
  }

  private static class Handler extends EditorWriteActionHandler {
    final EditorActionHandler
        wrappedHandler =
        new JoinLinesHandler(EmacsPlusWrapper.getWrappedHandler("EditorJoinLines"));

    private Handler() {
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
  }
}
