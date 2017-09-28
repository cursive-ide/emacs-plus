package com.mulgasoft.emacsplus.actions.edit.comment;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.mulgasoft.emacsplus.handlers.CommentHandler;
import com.mulgasoft.emacsplus.util.ActionUtil;
import com.mulgasoft.emacsplus.util.EmacsIds;

public class CommentKill extends CommentAction {
  @Override
  protected CommentHandler getMyHandler() {
    return new CommentKill.myHandler();
  }

  private static class myHandler extends CommentHandler {

    @Override
    protected void invokeAction(Editor editor, Caret caret, DataContext d, PsiFile file) {
      DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());
      CommentHandler.CommentRange range = findCommentRange(editor, caret, dataContext);
      if (range != null) {
        caret.moveToOffset(range.getStartOffset());
        caret.setSelection(range.getStartOffset(), range.getEndOffset());
        ActionUtil.dispatchLater(EmacsIds.KILL_REGION_ID, dataContext);
        ActionUtil.dispatchLater(EmacsIds.DELETE_HORIZ_ID, dataContext);
        ActionUtil.dispatchLater(EmacsIds.EMACS_STYLE_INDENT_ID, dataContext);
      }

    }
  }
}
