package com.mulgasoft.emacsplus.actions.edit.comment;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.mulgasoft.emacsplus.handlers.CommentHandler;

public class CommentNextLine extends CommentAction {
  @Override
  protected CommentHandler getMyHandler() {
    return new CommentNextLine.myHandler();
  }

  private static class myHandler extends CommentHandler {

    @Override
    protected void invokeAction(Editor editor, Caret caret, DataContext dataContext, PsiFile file) {
      Document document = editor.getDocument();
      LogicalPosition pos = null;
      PsiElement ele = inComment(editor, caret);
      if (ele != null && ele.getTextRange() != null) {
        pos = editor.offsetToLogicalPosition(ele.getTextRange().getEndOffset());
      } else {
        pos = caret.getLogicalPosition();
      }

      int line = pos.line + 1;
      if (line < document.getLineCount()) {
        caret.moveToOffset(document.getLineStartOffset(line));
        super.invokeAction(editor, caret, dataContext, file);
      }

    }
  }
}
