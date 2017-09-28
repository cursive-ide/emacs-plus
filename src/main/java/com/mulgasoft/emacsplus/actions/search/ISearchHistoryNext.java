package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISearchHistory;

public class ISearchHistoryNext extends EmacsPlusAction {
  public ISearchHistoryNext() {
    super(new ISearchHistoryNext.myHandler());
  }

  private static final class myHandler extends ISearchHistory {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      String[] vals = getHistory(editor);
      int index = getIndex();
      if (index < vals.length - 1) {
        int var10003;
        if (isReset()) {
          var10003 = index;
        } else {
          ++index;
          var10003 = index;
        }

        setText(editor, vals, var10003);
      } else if (getText(editor).isEmpty()) {
        beep(editor);
      } else {
        setIndex(vals.length);
        setText(editor, "");
      }

    }
  }
}
