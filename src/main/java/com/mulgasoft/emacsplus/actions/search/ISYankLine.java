package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISHandler;

public class ISYankLine extends EmacsPlusAction {
  public ISYankLine() {
    super(new ISYankLine.myHandler());
  }

  private static final class myHandler extends ISHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      Editor selected = FileEditorManager.getInstance(editor.getProject()).getSelectedTextEditor();
      int offset = selected.getCaretModel().getOffset();
      Document doc = selected.getDocument();
      String text = doc.getText(new TextRange(offset, doc.getLineEndOffset(doc.getLineNumber(offset))));
      if (text != null) {
        editor.getDocument().insertString(caret.getOffset(), fixYank(editor, text));
      }

    }
  }
}
