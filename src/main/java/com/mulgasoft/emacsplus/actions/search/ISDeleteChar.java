package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISHandler;

public class ISDeleteChar extends EmacsPlusAction {
  public ISDeleteChar() {
    super(new ISDeleteChar.myHandler());
  }

  private static final class myHandler extends ISHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      Editor selected = FileEditorManager.getInstance(editor.getProject()).getSelectedTextEditor();
      int offset = selected.getCaretModel().getOffset();
      int isOffset = caret.getOffset();
      Document isDoc = editor.getDocument();
      if (isOffset > 0) {
        isDoc.deleteString(isOffset - 1, isOffset);
        if (isDoc.getTextLength() == 0) {
          selected.getCaretModel().moveToOffset(offset - 1);
          selected.getSelectionModel().removeSelection();
        }
      }

    }

    @Override
    protected boolean isEnabledForCaret(Editor editor, Caret caret, DataContext dataContext) {
      return isInISearch(editor);
    }
  }
}
