package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ISearchBackwardRegexp extends ISearchBackward {
  public ISearchBackwardRegexp() {
    super(new RegexpHandler());
  }

  private static class RegexpHandler extends ISearchBackward.Handler {
    @Override
    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
      ISearch searcher = findSearcher(editor, dataContext);
      if (searcher != null) {
        searcher.getFindModel().setRegularExpressions(true);
        searcher.searchBackward();
      }
    }
  }
}
