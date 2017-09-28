package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.WhiteSpaceHandler;

public class JustOneSpace extends EmacsPlusAction {
  public JustOneSpace() {
    super(new JustOneSpace.myHandler());
  }

  private static class myHandler extends WhiteSpaceHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      transformSpace(editor, caret, dataContext, " ", false);
    }
  }
}
