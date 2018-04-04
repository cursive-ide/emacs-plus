package com.mulgasoft.emacsplus;

import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ApplicationComponent;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.keys.Keymaps;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class EmacsPlus implements ApplicationComponent {
  private static String ultCommand = null;
  private static String penultCommand = null;
  private static final boolean visualBeep = false;

  @Override
  public void initComponent() {
    Keymaps.enableKeymaps();
    CommandProcessor.getInstance().addCommandListener(new CommandListener() {
      @Override
      public void beforeCommandFinished(CommandEvent event) {
      }

      @Override
      public void undoTransparentActionStarted() {
      }

      @Override
      public void undoTransparentActionFinished() {
      }

      @Override
      public void commandStarted(CommandEvent event) {
      }

      @Override
      public void commandFinished(CommandEvent event) {
        setUltCommand(event.getCommandName());
      }
    });
    CommandProcessor.getInstance().addCommandListener(EmacsPlusAction.getCommandListener());
  }

  private static void setUltCommand(String name) {
    penultCommand = ultCommand;
    ultCommand = name;
  }

  public static String getUltCommand() {
    return ultCommand;
  }

  public static String getPenultCommand() {
    return penultCommand;
  }

  public static void beep() {
    beep(false);
  }

  public static void beep(boolean reset) {
    if (!visualBeep) {
      Toolkit.getDefaultToolkit().beep();
    }

    if (reset) {
      setUltCommand("");
    }

  }

  public static void resetCommand(String name) {
    setUltCommand(name);
  }

  @Override
  public void disposeComponent() {}

  @Override
  @NotNull
  public String getComponentName() {
    return "EmacsPlus";
  }
}
