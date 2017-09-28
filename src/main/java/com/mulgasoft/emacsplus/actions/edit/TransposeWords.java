package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.EmacsPlusWriteHandler;

public class TransposeWords extends EmacsPlusAction {
  public TransposeWords() {
    super(new TransposeWords.myHandler());
  }

  private static final class myHandler extends EmacsPlusWriteHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      Document doc = editor.getDocument();
      CharSequence chars = doc.getCharsSequence();
      int maxOffset = chars.length();
      int offset = caret.getOffset();
      if (offset == maxOffset) {
        --offset;
        caret.moveToOffset(offset);
      }

      TextRange right = getNextWordRange(editor, false, true);
      int
          leftdir =
          offset != 0 &&
          Character.isJavaIdentifierPart(chars.charAt(offset)) &&
          !Character.isJavaIdentifierPart(chars.charAt(offset - 1)) ? 1 : 0;
      TextRange left = getPreviousWordRange(editor, false, true, leftdir);
      boolean eof = false;
      if (offset != 0 && !left.isEmpty()) {
        if (offset != maxOffset && !trueEnd(maxOffset, left, right, doc)) {
          if (Character.isJavaIdentifierPart(chars.charAt(offset)) &&
              Character.isJavaIdentifierPart(chars.charAt(offset - 1))) {
            left = new TextRange(left.getStartOffset(), right.getEndOffset());
            caret.moveToOffset(Math.min(maxOffset, right.getEndOffset() + 1));
            right = getNextWordRange(editor, false, true);
            if (right.getEndOffset() == maxOffset) {
              eof = true;
            }
          }
        } else {
          eof = true;
        }
      } else {
        left = right;
        caret.moveToOffset(right.getEndOffset() + 1);
        right = getNextWordRange(editor, false, true);
      }

      if (eof) {
        right = left;
        caret.moveToOffset(left.getStartOffset() - 1);
        left = getPreviousWordRange(editor, false, true, leftdir);
      }

      if (right != null && !right.isEmpty() && left != null && !left.isEmpty()) {
        String rtext = doc.getText(right);
        String ltext = doc.getText(left);
        doc.replaceString(right.getStartOffset(), right.getEndOffset(), ltext);
        doc.replaceString(left.getStartOffset(), left.getEndOffset(), rtext);
        caret.moveToOffset(right.getEndOffset());
      } else {
        caret.moveToOffset(offset);
      }

    }

    private static boolean trueEnd(int maxOffset, TextRange left, TextRange right, Document doc) {
      boolean result = false;
      String last = doc.getText(right);
      if (right.getEndOffset() == maxOffset &&
          (left.getEndOffset() == maxOffset || last.isEmpty() || Character.isWhitespace(last.charAt(0)))) {
        result = true;
      }

      return result;
    }
  }
}
