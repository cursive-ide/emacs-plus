package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.featureStatistics.FeatureUsageTracker;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.Switcher;
import com.intellij.ide.actions.Switcher.SwitcherPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.ScrollingUtil;
import com.mulgasoft.emacsplus.keys.Keymaps;
import com.mulgasoft.emacsplus.util.ActionUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.util.List;

public class SwitchToBuffer extends DumbAwareAction {
  @NonNls
  private static final String SWITCH_TITLE = IdeBundle.message("title.popup.recent.files");
  @NonNls
  private static final String GOFORWARD_ACTION = "Emacs+.Forward";
  @NonNls
  private static final String GOBACK_ACTION = "Emacs+.Back";
  @NonNls
  private static final String GOUP_ACTION = "Emacs+.Up";
  @NonNls
  private static final String GODOWN_ACTION = "Emacs+.Down";
  @NonNls
  private static final String GOTOP_ACTION = "Emacs+.Top";
  @NonNls
  private static final String GOBOT_ACTION = "Emacs+.Bottom";

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    FeatureUsageTracker.getInstance().triggerFeatureUsed("navigation.recent.files");
    SwitcherPanel switcher = getSwitcher(e);
    if (switcher != null) {
      keySetup(switcher);
    }

  }

  private static SwitcherPanel getSwitcher(AnActionEvent e) {
    return Switcher.createAndShowSwitcher(e, SWITCH_TITLE, true, null);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setEnabled(e.getProject() != null);
  }

  private static void keySetup(JPanel field) {
    KeyStroke ksG = KeyStroke.getKeyStroke(71, 128);
    KeyStroke ksN = KeyStroke.getKeyStroke(78, 128);
    KeyStroke ksP = KeyStroke.getKeyStroke(80, 128);
    KeyStroke ksD = KeyStroke.getKeyStroke(86, 128);
    KeyStroke ksU = KeyStroke.getKeyStroke(86, Keymaps.getMeta());
    KeyStroke ksT = Keymaps.getIntlKeyStroke(153);
    KeyStroke ksB = Keymaps.getIntlKeyStroke(160);
    removeFromActions(field, ksG, null);
    removeFromActions(field, ksN, null);
    removeFromActions(field, ksP, null);
    removeFromActions(field, ksD, null);
    CustomShortcutSet cuts = CustomShortcutSet.fromString("ESCAPE", "control G");
    replaceOnActions(field, KeyStroke.getKeyStroke(27, 0), null, cuts);
    InputMap im = field.getInputMap();
    ActionMap am = field.getActionMap();
    am.put(GOFORWARD_ACTION, new TextAction(GOFORWARD_ACTION) {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof SwitcherPanel) {
          ((SwitcherPanel) e.getSource()).goForward();
        }

      }
    });
    am.put(GOBACK_ACTION, new TextAction(GOBACK_ACTION) {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof SwitcherPanel) {
          ((SwitcherPanel) e.getSource()).goBack();
        }

      }
    });
    am.put(GODOWN_ACTION, new TextAction(GODOWN_ACTION) {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof SwitcherPanel) {
          ScrollingUtil.movePageDown(((SwitcherPanel) e.getSource()).getSelectedList());
        }

      }
    });
    am.put(GOUP_ACTION, new TextAction(GOUP_ACTION) {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof SwitcherPanel) {
          ScrollingUtil.movePageUp(((SwitcherPanel) e.getSource()).getSelectedList());
        }

      }
    });
    am.put(GOTOP_ACTION, new TextAction(GOTOP_ACTION) {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof SwitcherPanel) {
          ScrollingUtil.moveHome(((SwitcherPanel) e.getSource()).getSelectedList());
        }

      }
    });
    am.put(GOBOT_ACTION, new TextAction(GOBOT_ACTION) {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof SwitcherPanel) {
          ScrollingUtil.moveEnd(((SwitcherPanel) e.getSource()).getSelectedList());
        }

      }
    });
    im.put(ksD, GODOWN_ACTION);
    im.put(ksU, GOUP_ACTION);
    im.put(ksN, GOFORWARD_ACTION);
    im.put(ksP, GOBACK_ACTION);
    im.put(ksT, GOTOP_ACTION);
    im.put(ksB, GOBOT_ACTION);
  }

  private static void replaceOnActions(JComponent field, KeyStroke key1, KeyStroke key2, CustomShortcutSet customs) {
    List<AnAction> actions = (List<AnAction>) field.getClientProperty(AnAction.ACTIONS_KEY);
    for (AnAction act : actions) {
      List<KeyboardShortcut> kbs = ActionUtil.getKBShortCuts(act);
      KeyboardShortcut kb = ActionUtil.getShortcut(kbs, key1, key2);
      if (kb != null) {
        act.unregisterCustomShortcutSet(field);
        act.registerCustomShortcutSet(customs, field);
        break;
      }
    }

  }

  private static void removeFromActions(JComponent field, KeyStroke key1, KeyStroke key2) {
    List<AnAction> actions = (List<AnAction>) field.getClientProperty(AnAction.ACTIONS_KEY);
    for (AnAction act : actions) {
      List<KeyboardShortcut> kbs = ActionUtil.getKBShortCuts(act);
      KeyboardShortcut kb = ActionUtil.getShortcut(kbs, key1, key2);
      if (kb != null) {
        Shortcut[] cuts = act.getShortcutSet().getShortcuts();
        Shortcut[] newCuts = new Shortcut[cuts.length - 1];
        int diff = 0;

        for (int i = 0; i < cuts.length; ++i) {
          if (cuts[i] == kb) {
            diff = 1;
          } else {
            newCuts[i - diff] = cuts[i];
          }
        }

        act.unregisterCustomShortcutSet(field);
        act.registerCustomShortcutSet(new CustomShortcutSet(newCuts), field);
        break;
      }
    }

  }
}
