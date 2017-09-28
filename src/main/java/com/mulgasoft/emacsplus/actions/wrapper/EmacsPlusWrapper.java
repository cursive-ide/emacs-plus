package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.util.ActionUtil;

public abstract class EmacsPlusWrapper extends EmacsPlusAction {
  protected EmacsPlusWrapper(EditorActionHandler defaultHandler) {
    super(defaultHandler);
  }

  static EditorActionHandler getWrappedHandler(String name) {
    EditorActionHandler handler = null;
    AnAction action = ActionUtil.getAction(name);
    if (action instanceof EditorAction) {
      handler = ((EditorAction) action).getHandler();
    }

    return handler;
  }
}
