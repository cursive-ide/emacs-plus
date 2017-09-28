package com.mulgasoft.emacsplus.actions.wrapper;

import com.mulgasoft.emacsplus.util.EmacsIds;

public class KillLine extends KillWrapper {
  public KillLine() {
    super(getWrappedHandler(EmacsIds.EDITOR_CUT_LINE_END));
  }

  @Override
  protected String getName() {
    return "kill-line";
  }
}
