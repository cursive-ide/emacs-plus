package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.util.ActionUtil;
import com.mulgasoft.emacsplus.util.EmacsIds;

public class DeleteOtherWindows extends EmacsPlusAction {
  protected DeleteOtherWindows() {
    super(new DeleteOtherWindows.myHandler());
  }

  private static class myHandler extends EditorActionHandler {

    @Override
    public void doExecute(Editor editor, Caret caret, DataContext dataContext) {
      FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(editor.getProject());
      DataContext d = DataManager.getInstance().getDataContext(editor.getComponent());
      ActionUtil.dispatchLater(fileEditorManager.isInSplitter() ? EmacsIds.EDITOR_UNSPLIT : EmacsIds.EDITOR_MAXIMIZE, d);
    }
  }
}
