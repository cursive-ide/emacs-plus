package com.mulgasoft.emacsplus.actions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import com.intellij.openapi.wm.StatusBar.Info;

import java.util.HashMap;
import java.util.Map;

public abstract class EmacsPlusAction extends TextComponentEditorAction implements EmacsPlusBA {
  private static final Map<String, EmacsPlusBA> ourCommandMap = new HashMap();

  protected EmacsPlusAction(EditorActionHandler defaultHandler) {
    super(defaultHandler);
  }

  public static void addCommandListener(EmacsPlusBA action, String id) {
    ourCommandMap.put(id, action);
  }

  @Override
  public void before(CommandEvent e) {
  }

  @Override
  public void after(CommandEvent e) {
  }

  public static CommandListener getCommandListener() {
    return EmacsPlusAction.StaticCommandListener.ourCommandListener;
  }

  public static void infoMessage(final String msg) {
    ApplicationManager.getApplication().invokeLater(() -> Info.set(msg, null));
  }

  public static void errorMessage(String msg) {
    infoMessage(msg);
  }

  private static class StaticCommandListener {
    private static final EmacsPlusAction.EmacsPlusCommandListener
        ourCommandListener =
        new EmacsPlusAction.EmacsPlusCommandListener();
  }

  private static class EmacsPlusCommandListener implements CommandListener {

    @Override
    public void commandStarted(CommandEvent event) {
      EmacsPlusBA thisAction = ourCommandMap.get(event.getCommandName());
      if (thisAction != null) {
        thisAction.before(event);
      }
    }

    @Override
    public void commandFinished(CommandEvent event) {
      EmacsPlusBA thisAction = ourCommandMap.get(event.getCommandName());
      if (thisAction != null) {
        thisAction.after(event);
      }
    }

    @Override
    public void beforeCommandFinished(CommandEvent event) {
    }

    @Override
    public void undoTransparentActionStarted() {
    }

    @Override
    public void undoTransparentActionFinished() {
    }
  }
}
