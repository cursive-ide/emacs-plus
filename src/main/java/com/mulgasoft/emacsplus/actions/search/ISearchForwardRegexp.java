package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.command.CommandEvent;
import com.mulgasoft.emacsplus.util.EmacsIds;

public class ISearchForwardRegexp extends ISearchForward {
  @Override
  protected String getName() {
    return EmacsIds.ISEARCH_REGEXP_NAME;
  }

  @Override
  public void after(CommandEvent e) {
    super.after(e);
    ISearch searcher = getSearcher();
    if (searcher != null) {
      searcher.getFindModel().setRegularExpressions(true);
    }

  }
}
