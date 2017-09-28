package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.EmacsPlusWriteHandler;

public class TransposeLines extends EmacsPlusAction {
  public TransposeLines() {
    super(new TransposeLines.myHandler());
  }

  private static final class myHandler extends EmacsPlusWriteHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      Document document = editor.getDocument();
      int cline = document.getLineNumber(caret.getOffset());
      if (cline == 0) {
        ++cline;
      }

      int line2 = cline--;
      int len = document.getLineCount();
      if (line2 < len) {
        swapLines(document, cline, line2);
        ++line2;
        caret.moveToOffset(document.getLineStartOffset(line2 < len ? line2 : len - 1));
        EditorModificationUtil.scrollToCaret(editor);
      }

    }

    private static void swapLines(Document document, int line1, int line2) {
      TextRange t1 = new TextRange(document.getLineStartOffset(line1), document.getLineEndOffset(line1));
      TextRange t2 = new TextRange(document.getLineStartOffset(line2), document.getLineEndOffset(line2));
      String line1Text = document.getText(t1);
      String line2Text = document.getText(t2);
      document.replaceString(t2.getStartOffset(), t2.getEndOffset(), line1Text);
      document.replaceString(t1.getStartOffset(), t1.getEndOffset(), line2Text);
    }
  }
}
