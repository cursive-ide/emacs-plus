package com.mulgasoft.emacsplus.handlers;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;

public class CaseHandler extends EmacsPlusWriteHandler {
  protected static void caseAction(Editor editor, Caret caret, CaseHandler.Cases cases) {
    Document doc = editor.getDocument();
    TextRange range = caret.hasSelection() ? getSelectionRange(caret) : getNextWordRange(editor, false, true);
    if (!range.isEmpty()) {
      doc.replaceString(range.getStartOffset(), range.getEndOffset(), toCase(doc.getText(range), cases));
      caret.moveToVisualPosition(editor.offsetToVisualPosition(range.getEndOffset()));
    }

  }

  private static TextRange getSelectionRange(Caret caret) {
    TextRange result = new TextRange(caret.getSelectionStart(), caret.getSelectionEnd());
    caret.removeSelection();
    return result;
  }

  private static String toCase(String text, CaseHandler.Cases cases) {
    StringBuilder builder = new StringBuilder(text.length());
    boolean prevIsSlash = false;

    for (char c : text.toCharArray()) {
      if (!prevIsSlash) {
        c = cases == Cases.UPPER ? Character.toUpperCase(c) : Character.toLowerCase(c);
      }

      prevIsSlash = c == '\\';
      builder.append(c);
    }

    if (cases == CaseHandler.Cases.CAP && builder.length() > 0) {
      builder.replace(0, 1, String.valueOf(Character.toUpperCase(builder.charAt(0))));
    }

    return builder.toString();
  }

  protected enum Cases {
    UPPER,
    LOWER,
    CAP
  }
}
