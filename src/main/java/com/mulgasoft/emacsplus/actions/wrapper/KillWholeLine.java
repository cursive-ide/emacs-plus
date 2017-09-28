package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.util.EmacsIds;

public class KillWholeLine extends KillWrapper {
  public KillWholeLine() {
    super(new KillWholeLine.myHandler());
  }

  @Override
  protected String getName() {
    return EmacsIds.KILL_WHOLE_LINE_NAME;
  }

  private static class myHandler extends KillWrapper.CutHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      if (editor.getSelectionModel().hasSelection(true)) {
        editor.getSelectionModel().removeSelection(true);
      }

      myCutHandler.executeWriteAction(editor, caret, dataContext);
    }
  }
}
