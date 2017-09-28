package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import com.mulgasoft.emacsplus.util.EmacsIds;
import org.jetbrains.annotations.NotNull;

public class KillRegion extends KillWrapper {
  public KillRegion() {
    super(new KillRegion.myHandler());
  }

  @Override
  protected String getName() {
    return EmacsIds.KILL_REGION_NAME;
  }

  public static class myHandler extends KillWrapper.CutHandler {
    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      if (myCutHandler != null) {
        myCutHandler.executeWriteAction(editor, caret, dataContext);
      }

    }

    @Override
    protected boolean isEnabledForCaret(@NotNull Editor editor, @NotNull Caret caret, DataContext dataContext) {
      return caret.hasSelection() &&
             !ISHandler.isInISearch(editor) &&
             super.isEnabledForCaret(editor, caret, dataContext);
    }
  }
}
