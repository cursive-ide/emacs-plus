package cursive.emacsplus;

import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ApplicationComponent;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import cursive.emacsplus.keys.Keymaps;
import org.jetbrains.annotations.NotNull;

/**
 * @author Colin Fleming
 */
public class EmacsPlus implements ApplicationComponent {
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
        com.mulgasoft.emacsplus.EmacsPlus.resetCommand(event.getCommandName());
      }
    });
    CommandProcessor.getInstance().addCommandListener(EmacsPlusAction.getCommandListener());
  }

  @Override
  public void disposeComponent() {
  }

  @Override
  @NotNull
  public String getComponentName() {
    return "EmacsPlus";
  }
}