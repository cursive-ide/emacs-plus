package com.mulgasoft.emacsplus.actions.wrapper;

import com.mulgasoft.emacsplus.util.EmacsIds;

public class KillWordBackward extends KillWrapper {
  protected KillWordBackward() {
    super(KillWrapper.getWrappedHandler(EmacsIds.EDITOR_KILL_TO_WORD_START));
  }

  @Override
  protected String getName() {
    return EmacsIds.BACK_KILL_WORD_NAME;
  }
}
