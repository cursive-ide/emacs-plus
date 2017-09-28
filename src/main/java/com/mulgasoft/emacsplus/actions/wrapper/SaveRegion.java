package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.handlers.ISHandler;

public class SaveRegion extends KillWrapper {
  public SaveRegion() {
    super(new SaveRegion.myHandler());
  }

  @Override
  protected String getName() {
    return "kill-ring-save";
  }

  private static class myHandler extends EditorActionHandler {
    private final EditorActionHandler mySaveHandler;

    private myHandler() {
      mySaveHandler = KillWrapper.getWrappedHandler("EditorCopy");
    }

    @Override
    public void doExecute(Editor editor, Caret caret, DataContext dataContext) {
      if (mySaveHandler != null) {
        mySaveHandler.execute(editor, caret, dataContext);
      }

    }

    @Override
    protected boolean isEnabledForCaret(Editor editor, Caret caret, DataContext dataContext) {
      return !ISHandler.isISearchField(editor) && super.isEnabledForCaret(editor, caret, dataContext);
    }
  }
}
