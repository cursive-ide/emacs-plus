package com.mulgasoft.emacsplus.actions.info;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.wm.StatusBar.Info;
import com.mulgasoft.emacsplus.handlers.EmacsPlusCaretHandler;

public class WhatLine extends EditorAction {
  protected WhatLine() {
    super(new WhatLine.myHandler());
  }

  private static final class myHandler extends EmacsPlusCaretHandler {

    @Override
    protected void doXecute(Editor var1, Caret var2, DataContext var3) {
      final int cline = var1.getDocument().getLineNumber(var2.getOffset()) + 1;
      ApplicationManager.getApplication().invokeLater(() -> Info.set("Line " + cline, null));
    }
  }
}
