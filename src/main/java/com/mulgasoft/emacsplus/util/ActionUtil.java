package com.mulgasoft.emacsplus.util;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActionUtil {
  @NonNls
  public static final String CR = "\n";
  @NonNls
  public static final String SPACE = " ";
  @NonNls
  public static final String EMPTY_STR = "";

  private static class OurInstanceHolder {
    private static final ActionUtil ourInstance = new ActionUtil();
  }

  public static ActionUtil getInstance() {

    return OurInstanceHolder.ourInstance;
  }

  public static List<KeyboardShortcut> getKBShortCuts(@NotNull String actionId) {
    return getKBShortCuts(getAction(actionId));
  }

  public static List<KeyboardShortcut> getKBShortCuts(@NotNull AnAction action) {
    List<KeyboardShortcut> kbs = new ArrayList();
    if (action != null) {
      Shortcut[] shortcuts = action.getShortcutSet().getShortcuts();

      for (Shortcut shortcut : shortcuts) {
        if (shortcut instanceof KeyboardShortcut) {
          kbs.add((KeyboardShortcut) shortcut);
        }
      }
    }

    return kbs;
  }

  public static boolean hasKBShortCut(@NotNull String actionId, @NotNull KeyStroke key1, @Nullable KeyStroke key2) {
    return getShortcut(getKBShortCuts(actionId), key1, key2) != null;
  }

  public static boolean hasKBShortCut(@NotNull AnAction action, @NotNull KeyStroke key1, @Nullable KeyStroke key2) {
    return getShortcut(getKBShortCuts(action), key1, key2) != null;
  }

  public static KeyboardShortcut getShortcut(@NotNull List<KeyboardShortcut> kbs,
                                             @NotNull KeyStroke key1,
                                             @Nullable KeyStroke key2) {
    KeyboardShortcut result = null;
    if (!kbs.isEmpty()) {
      Iterator<KeyboardShortcut> iterator = kbs.iterator();

      KeyboardShortcut kb;
      while (true) {
        do {
          if (!iterator.hasNext()) {
            return result;
          }

          kb = iterator.next();
        } while (!key1.equals(kb.getFirstKeyStroke()));

        KeyStroke k2 = kb.getSecondKeyStroke();
        if (key2 == null) {
          if (key2 == k2) {
            break;
          }
        } else if (key2.equals(k2)) {
          break;
        }
      }

      result = kb;
    }

    return result;
  }

  public static boolean isOnceAction(@NotNull String id) {
    boolean result = true;
    AnAction action = getAction(id);
    if (action instanceof EditorAction) {
      EditorActionHandler handler = ((EditorAction) action).getHandler();
      result = !handler.runForAllCarets();
    }

    return result;
  }

  public static AnAction getAction(String id) {
    return ActionManager.getInstance().getAction(id);
  }

  public static EditorAction getEditorAction(String id) {
    EditorAction result = null;
    AnAction a = getAction(id);
    if (a instanceof EditorAction) {
      result = (EditorAction) a;
    }

    return result;
  }

  public static void dispatch(@NotNull String id, @NotNull DataContext context) {
    AnAction dispatch = getAction(id);
    if (dispatch != null) {
      dispatch(dispatch, context);
    }
  }

  public static void dispatchLater(@NotNull final String id, @NotNull final DataContext context) {
    ApplicationManager.getApplication().invokeLater(() -> dispatch(id, context));
  }

  public static boolean dispatchLater(@NotNull final AnAction dispatch, @NotNull final DataContext context) {
    ApplicationManager.getApplication().invokeLater(() -> dispatch(dispatch, context));
    return true;
  }

  public static boolean dispatch(@NotNull AnAction dispatch, @NotNull DataContext context) {
    AnActionEvent event = AnActionEvent.createFromAnAction(dispatch, null, "MainMenu", context);
    dispatch.update(event);
    if (event.getPresentation().isEnabled()) {
      dispatch.actionPerformed(event);
      return true;
    } else {
      return false;
    }
  }
}
