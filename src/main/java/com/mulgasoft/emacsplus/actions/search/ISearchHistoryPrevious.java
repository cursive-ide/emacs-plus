package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISearchHistory;

public class ISearchHistoryPrevious extends EmacsPlusAction {
  public ISearchHistoryPrevious() {
    super(new ISearchHistoryPrevious.myHandler());
  }

  private static final class myHandler extends ISearchHistory {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      String[] vals = getHistory(editor);
      if (isReset()) {
        setIndex(vals.length);
      }

      int index = getIndex();
      setText(editor, vals, index - (index > 0 ? 1 : 0));
    }
  }
}
