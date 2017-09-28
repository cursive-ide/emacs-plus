package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class ISearchBackwardRegexp extends ISearchBackward {
  @Override
  public void actionPerformed(AnActionEvent e) {
    ISearch searcher = delegateAction(e);
    if (searcher != null) {
      searcher.getFindModel().setRegularExpressions(true);
    }

  }
}
