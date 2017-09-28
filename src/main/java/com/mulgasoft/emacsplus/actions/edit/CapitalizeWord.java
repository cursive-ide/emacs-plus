package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.CaseHandler;
import com.mulgasoft.emacsplus.handlers.ISHandler;

public class CapitalizeWord extends EmacsPlusAction {
  public CapitalizeWord() {
    super(new CapitalizeWord.myHandler());
  }

  private static class myHandler extends CaseHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      caseAction(editor, caret, CaseHandler.Cases.CAP);
    }

    @Override
    protected boolean isEnabledForCaret(Editor editor, Caret caret, DataContext dataContext) {
      return !ISHandler.isInISearch(editor) && super.isEnabledForCaret(editor, caret, dataContext);
    }
  }
}
