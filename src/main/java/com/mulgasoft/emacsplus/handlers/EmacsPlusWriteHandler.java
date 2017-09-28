package com.mulgasoft.emacsplus.handlers;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.editor.actions.EditorActionUtil;
import com.intellij.openapi.util.TextRange;

public abstract class EmacsPlusWriteHandler extends EditorWriteActionHandler {
  protected EmacsPlusWriteHandler() {
    super(true);
  }

  protected static String getNextWord(Editor editor, boolean isLine, boolean isWord) {
    return editor.getDocument().getText(getNextWordRange(editor, isLine, isWord));
  }

  protected static TextRange getNextWordRange(Editor editor, boolean isLine, boolean isWord) {
    return getWordRange(editor, isLine, isWord, 1);
  }

  protected static TextRange getPreviousWordRange(Editor editor, boolean isLine, boolean isWord, int dir) {
    return getWordRange(editor, isLine, isWord, dir > 0 ? -dir : dir);
  }

  private static TextRange getWordRange(Editor editor, boolean isLine, boolean isWord, int dir) {
    Document doc = editor.getDocument();
    int offset = editor.getCaretModel().getOffset();
    CharSequence chars = editor.getDocument().getCharsSequence();
    int maxOffset = isLine ? doc.getLineEndOffset(doc.getLineNumber(offset)) : chars.length();
    offset = Math.min(offset, maxOffset);
    int newOffset = offset + dir;
    dir = dir == 0 ? -1 : dir;
    int
        startOffset =
        !isWord || dir >= 0 && (offset >= maxOffset || Character.isJavaIdentifierStart(chars.charAt(offset)))
        ? offset
        : -1;
    if (newOffset == maxOffset && startOffset < 0) {
      startOffset = offset;
    }

    for (; newOffset < maxOffset && newOffset > 0; newOffset += dir) {
      if (startOffset < 0) {
        char c = chars.charAt(newOffset);
        if (Character.isJavaIdentifierStart(c)) {
          startOffset = newOffset + (dir < 0 ? 1 : 0);
          if (dir < 0 && EditorActionUtil.isWordOrLexemeStart(editor, newOffset, false)) {
            break;
          }
        }
      } else if (dir < 0) {
        if (EditorActionUtil.isWordOrLexemeStart(editor, newOffset, false)) {
          break;
        }
      } else if (EditorActionUtil.isWordOrLexemeEnd(editor, newOffset, false)) {
        break;
      }
    }

    if (dir < 0) {
      int tmp = newOffset;
      newOffset = startOffset;
      startOffset = tmp;
    }

    return newOffset <= maxOffset && newOffset >= 0 ? new TextRange(startOffset < 0 ? newOffset : startOffset,
                                                                    newOffset) : new TextRange(offset, offset);
  }
}
