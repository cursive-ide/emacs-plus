package com.mulgasoft.emacsplus.actions.edit.comment;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.psi.PsiFile;
import com.mulgasoft.emacsplus.handlers.CommentHandler;

public class CommentIndentNew extends CommentAction {
  @Override
  protected CommentHandler getMyHandler() {
    return new CommentIndentNew.myHandler();
  }

  private static class myHandler extends CommentHandler {

    @Override
    protected void invokeAction(Editor editor, Caret caret, DataContext dataContext, PsiFile file) {
      Document document = editor.getDocument();
      LogicalPosition pos = caret.getLogicalPosition();
      int offset = document.getLineEndOffset(pos.line);
      document.insertString(offset, "\n");
      caret.moveToOffset(document.getLineStartOffset(pos.line + 1));
      super.invokeAction(editor, caret, dataContext, file);
    }
  }
}
