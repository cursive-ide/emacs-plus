package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.EmacsPlusWriteHandler;

public class TransposeChars extends EmacsPlusAction {
  public TransposeChars() {
    super(new TransposeChars.myHandler());
  }

  private static final class myHandler extends EmacsPlusWriteHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      Document document = editor.getDocument();
      int coff = caret.getOffset();
      int lineNo = document.getLineNumber(coff);
      int boff = document.getLineStartOffset(lineNo);
      int eoff = document.getLineEndOffset(lineNo);
      int incr = 1;
      if (coff != boff || lineNo != 0) {
        if (coff == eoff) {
          --coff;
          if (coff == 0) {
            return;
          }
        }

        int b = coff - incr;
        int e = Math.min(eoff, coff + incr);
        String sub = document.getText(new TextRange(b, e));
        document.replaceString(b, e, sub.substring(incr) + sub.substring(0, incr));
        caret.moveToOffset(e);
      }
    }
  }
}
