package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.command.CommandEvent;
import com.mulgasoft.emacsplus.util.EmacsIds;

public class QueryReplaceRegexp extends QueryReplace {
  @Override
  protected String getName() {
    return EmacsIds.QUERY_REPLACE_REGEXP_NAME;
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
