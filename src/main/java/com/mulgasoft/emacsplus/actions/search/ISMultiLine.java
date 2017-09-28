package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.FindModel;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISHandler;

public class ISMultiLine extends EmacsPlusAction {
  public ISMultiLine() {
    super(new ISMultiLine.myHandler());
  }

  private static final class myHandler extends ISHandler {

    @Override
    public void executeWriteAction(Editor isEditor, Caret caret, DataContext dataContext) {
      Editor editor = FileEditorManager.getInstance(isEditor.getProject()).getSelectedTextEditor();
      FindModel findModel = ISHandler.getFindModel(editor);
      if (findModel != null) {
        findModel.setMultiline(!findModel.isMultiline());
      }

    }

    @Override
    protected boolean isEnabledForCaret(Editor editor, Caret caret, DataContext dataContext) {
      return isInISearch(editor);
    }
  }
}
