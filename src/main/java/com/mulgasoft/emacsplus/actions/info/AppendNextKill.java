package com.mulgasoft.emacsplus.actions.info;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.EmacsPlusCaretHandler;
import com.mulgasoft.emacsplus.util.EmacsIds;

public class AppendNextKill extends EmacsPlusAction {
  public AppendNextKill() {
    super(new AppendNextKill.myHandler(false));
    addCommandListener(this, EmacsIds.APPEND_KILL_NAME);
  }

  private static class myHandler extends EmacsPlusCaretHandler {
    private myHandler(boolean runForEachCaret) {
      super(runForEachCaret);
    }

    @Override
    protected void doXecute(Editor var1, Caret var2, DataContext var3) {
    }
  }
}
