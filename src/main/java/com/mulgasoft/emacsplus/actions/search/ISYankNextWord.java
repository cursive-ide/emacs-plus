package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISHandler;

public class ISYankNextWord extends EmacsPlusAction {
  public ISYankNextWord() {
    super(new ISYankNextWord.myHandler());
  }

  private static final class myHandler extends ISHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      Document doc = editor.getDocument();
      Editor selected = getTextEditor(editor.getProject());
      String text = getNextWord(selected, editor.isOneLineMode() && !isRegexp(editor), false);
      if (text != null && !text.isEmpty()) {
        doc.insertString(caret.getOffset(), fixYank(editor, text));
      }

    }
  }
}
