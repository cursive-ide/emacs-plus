package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.util.KillCmdUtil;

public abstract class KillWrapper extends EmacsPlusWrapper {
  KillCmdUtil.KillRingInfo info = null;

  protected KillWrapper(EditorActionHandler defaultHandler) {
    super(defaultHandler);
    EmacsPlusAction.addCommandListener(this, getName());
  }

  protected abstract String getName();

  @Override
  public void before(CommandEvent e) {
    info = KillCmdUtil.beforeKill();
  }

  @Override
  public void after(CommandEvent e) {
    try {
      KillCmdUtil.afterKill(info, e.getDocument(), true);
    } finally {
      info = null;
    }

  }

  public static class CutHandler extends EditorWriteActionHandler {
    protected final EditorWriteActionHandler myCutHandler = getWrappedHandler("EditorCut");

    private static EditorWriteActionHandler getWrappedHandler(String name) {
      EditorWriteActionHandler result = null;
      EditorActionHandler handler = KillWrapper.getWrappedHandler(name);
      if (handler instanceof EditorWriteActionHandler) {
        result = (EditorWriteActionHandler) handler;
      }

      return result;
    }
  }
}
