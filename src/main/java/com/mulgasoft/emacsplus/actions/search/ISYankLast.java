package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISHandler;

public class ISYankLast extends EmacsPlusAction {
  public ISYankLast() {
    super(new ISYankLast.myHandler());
  }

  private static final class myHandler extends ISHandler {

    @Override
    protected String getSepr(Editor editor) {
      return editor.isOneLineMode() ? " " : "\n";
    }

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      yankIt(editor, caret);
    }
  }
}
