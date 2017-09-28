package com.mulgasoft.emacsplus.actions.motion;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.actions.EditorActionUtil;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ExprHandler;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import com.mulgasoft.emacsplus.util.EditorUtil;

public class BackToIndentation extends EmacsPlusAction {
  public BackToIndentation() {
    super(new BackToIndentation.myHandler());
  }

  private static class myHandler extends ExprHandler {

    @Override
    protected void doXecute(Editor var1, Caret var2, DataContext var3) {
      EditorUtil.checkMarkSelection(var1, var2);
      int line = getCorrectLine(var1, var2);
      int col = EditorActionUtil.findFirstNonSpaceColumnOnTheLine(var1, line);
      if (col >= 0) {
        var2.moveToVisualPosition(new VisualPosition(line, col));
        EditorModificationUtil.scrollToCaret(var1);
      }

    }

    private static int getCorrectLine(Editor editor, Caret caret) {
      int caretLine = caret.getLogicalPosition().line;
      VisualPosition caretPos = caret.getVisualPosition();
      VisualPosition caretLineStart = editor.offsetToVisualPosition(editor.getDocument().getLineStartOffset(caretLine));
      return Math.min(caretPos.line, caretLineStart.line);
    }

    @Override
    protected boolean isEnabledForCaret(Editor editor, Caret caret, DataContext dataContext) {
      return super.isEnabledForCaret(editor, caret, dataContext) && !ISHandler.isInISearch(editor);
    }
  }
}
