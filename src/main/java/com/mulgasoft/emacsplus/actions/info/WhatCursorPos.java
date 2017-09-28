package com.mulgasoft.emacsplus.actions.info;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.EmacsPlusCaretHandler;
import org.jetbrains.annotations.NonNls;

public class WhatCursorPos extends EditorAction {
  @NonNls
  private static final String N_GEN = "\\c";
  @NonNls
  private static final String N_NEW = "\\n";
  @NonNls
  private static final String N_RET = "\\r";
  @NonNls
  private static final String N_TAB = "\\t";
  @NonNls
  private static final String N_BS = "\\b";
  @NonNls
  private static final String N_FF = "\\f";
  @NonNls
  private static final String N_SPC = "SPC";
  private static final String CURSOR_POSITION = "Char: %s  (%d, #o%o, #x%x)  point=%d of %d (%d%%)";
  private static final String EOB_POSITION = "point=%d of %d (EOB)";

  protected WhatCursorPos() {
    super(new WhatCursorPos.myHandler());
  }

  private static final class myHandler extends EmacsPlusCaretHandler {

    @Override
    protected void doXecute(Editor var1, Caret var2, DataContext var3) {
      Document doc = var1.getDocument();
      int offset = var2.getOffset();
      int docLen = doc.getTextLength();
      String msg = offset >= docLen ? getEob(offset, docLen) : getCurPos(offset, docLen, doc);
      EmacsPlusAction.infoMessage(msg);
    }

    private static String getEob(int offset, int docLen) {
      return String.format(EOB_POSITION, offset, docLen);
    }

    private static String getCurPos(int offset, int docLen, Document doc) {
      char curChar = doc.getCharsSequence().charAt(offset);
      int percent = (new Float((double) (offset * 100 / docLen) + 0.5D)).intValue();
      String sChar = curChar <= ' ' ? normalizeChar(curChar) : String.valueOf(curChar);
      return String.format(CURSOR_POSITION, sChar, (int) curChar, (int) curChar,
                           (int) curChar, offset, docLen, percent);
    }

    private static String normalizeChar(char cc) {
      String result = null;
      switch (cc) {
        case '\b':
          result = "\\b";
          break;
        case '\t':
          result = "\\t";
          break;
        case '\n':
          result = "\\n";
          break;
        case '\f':
          result = "\\f";
          break;
        case '\r':
          result = "\\r";
          break;
        case ' ':
          result = "SPC";
          break;
        default:
          result = "\\c" + cc;
      }

      return result;
    }
  }
}
