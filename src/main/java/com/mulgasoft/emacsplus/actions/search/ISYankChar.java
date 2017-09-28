package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISHandler;

public class ISYankChar extends EmacsPlusAction {
  public ISYankChar() {
    super(new ISYankChar.myHandler());
  }

  private static final class myHandler extends ISHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      Editor selected = FileEditorManager.getInstance(editor.getProject()).getSelectedTextEditor();
      int offset = selected.getCaretModel().getOffset();
      String text = selected.getDocument().getText(new TextRange(offset, offset + 1));
      if (text != null) {
        editor.getDocument().insertString(caret.getOffset(), fixYank(editor, text));
      }

    }
  }
}
