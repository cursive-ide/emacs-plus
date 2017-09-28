package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISHandler;

public class ISForwardDelete extends EmacsPlusAction {
  public ISForwardDelete() {
    super(new ISForwardDelete.myHandler());
  }

  private static final class myHandler extends ISHandler {

    @Override
    public void executeWriteAction(Editor isEditor, Caret caret, DataContext dataContext) {
      int isOffset = caret.getOffset();
      Document isDoc = isEditor.getDocument();
      if (isOffset < isDoc.getTextLength()) {
        isDoc.deleteString(isOffset, isOffset + 1);
        if (isDoc.getTextLength() == 0) {
          Editor editor = FileEditorManager.getInstance(isEditor.getProject()).getSelectedTextEditor();
          int offset = editor.getCaretModel().getOffset();
          editor.getCaretModel().moveToOffset(offset - 1);
          editor.getSelectionModel().removeSelection();
        }
      }

    }
  }
}
