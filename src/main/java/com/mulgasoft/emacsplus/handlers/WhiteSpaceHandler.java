package com.mulgasoft.emacsplus.handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

public class WhiteSpaceHandler extends EmacsPlusWriteHandler {
  protected static int transformSpace(@NotNull Editor editor,
                                      @NotNull Caret caret,
                                      DataContext dataContext,
                                      String replace,
                                      boolean ignoreCR) {
    return transformSpace(editor, caret.getOffset(), dataContext, replace, ignoreCR);
  }

  protected static int transformSpace(@NotNull Editor editor,
                                      int offset,
                                      DataContext dataContext,
                                      String replace,
                                      boolean ignoreCR) {
    Document document = editor.getDocument();
    int lastOff = document.getTextLength();
    int left = countWS(document, lastOff, offset - 1, -1, ignoreCR);
    int right = countWS(document, lastOff, offset, 1, ignoreCR);
    if (ignoreCR) {
      replace = offset - left != 0 && offset + right != document.getTextLength() ? replace + '\n' : replace;
    }

    document.replaceString(offset - left, offset + right, replace);
    return offset - left + replace.length();
  }

  protected static boolean isBlankLine(Document document, int line) {
    int offset = document.getLineStartOffset(line);
    int lastOff = document.getTextLength();
    return countWS(document, lastOff, offset, 1, false) == document.getLineEndOffset(line) - offset;
  }

  private static int countWS(Document document, int lastOff, int offset, int dir, boolean ignoreCR) {
    CharSequence seq = document.getCharsSequence();
    char eol = 10;
    char cr = 13;
    int lineOff = offset;

    int off;
    char c;
    for (off = offset; -1 < off && off < lastOff && (c = seq.charAt(off)) <= ' '; off += dir) {
      if (c == eol || c == cr) {
        if (!ignoreCR) {
          break;
        }

        lineOff = off + dir;
      }
    }

    return Math.abs(offset - (ignoreCR ? lineOff : off));
  }
}
