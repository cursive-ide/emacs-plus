package com.mulgasoft.emacsplus.actions.motion;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ExprHandler;

public class BackwardWord extends EmacsPlusAction {
  protected BackwardWord() {
    super(new BackwardWord.myHandler());
  }

  private static final class myHandler extends ExprHandler {

    @Override
    protected void doXecute(Editor var1, Caret var2, DataContext var3) {
      moveToWord(var1, var2, var3, -1);
    }
  }
}
