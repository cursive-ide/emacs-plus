package com.mulgasoft.emacsplus.actions.wrapper;

import com.mulgasoft.emacsplus.util.EmacsIds;

public class KillWord extends KillWrapper {
  protected KillWord() {
    super(KillWrapper.getWrappedHandler(EmacsIds.EDITOR_KILL_TO_WORD_END));
  }

  @Override
  protected String getName() {
    return "kill-word";
  }
}
