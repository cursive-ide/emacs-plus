package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.CaseHandler;

public class DowncaseWord extends EmacsPlusAction {
  public DowncaseWord() {
    super(new DowncaseWord.myHandler());
  }

  private static class myHandler extends CaseHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      caseAction(editor, caret, CaseHandler.Cases.LOWER);
    }
  }
}
