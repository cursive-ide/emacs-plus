package com.mulgasoft.emacsplus.actions.tool;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import com.mulgasoft.emacsplus.util.EmacsIds;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class TWAction extends AnAction {
  private static final Map<String, KeyStroke> keys = new HashMap<String, KeyStroke>() {
    {
      put(EmacsIds.TW_SELECT_PREVIOUS, KeyStroke.getKeyStroke(224, 0));
      put(EmacsIds.TW_SELECT_NEXT, KeyStroke.getKeyStroke(225, 0));
      put(EmacsIds.TW_SCROLL_UP, KeyStroke.getKeyStroke(34, 0));
      put(EmacsIds.TW_SCROLL_DOWN, KeyStroke.getKeyStroke(33, 0));
      put(EmacsIds.TW_BEGIN, KeyStroke.getKeyStroke(36, 0));
      put(EmacsIds.TW_END, KeyStroke.getKeyStroke(35, 0));
    }
  };

  protected KeyStroke getKey(AnActionEvent e) {
    return keys.get(e.getActionManager().getId(this));
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    JComponent component = getComponent(e.getDataContext());
    ActionListener act = component.getActionForKeyStroke(getKey(e));
    if (act != null) {
      act.actionPerformed(new ActionEvent(getComponent(e.getDataContext()), 0, null));
    }

  }

  protected static Editor getEditor(DataContext dc) {
    return TextComponentEditorAction.getEditorFromContext(dc);
  }

  @Override
  public void update(AnActionEvent e) {
    Presentation presentation = e.getPresentation();
    DataContext dataContext = e.getDataContext();
    Editor editor = getEditor(dataContext);
    if (editor != null) {
      presentation.setEnabled(false);
    } else {
      presentation.setEnabled(isValid(e));
    }

  }

  protected static JComponent getComponent(DataContext dc) {
    JComponent result = null;
    Object cc = PlatformDataKeys.CONTEXT_COMPONENT.getData(dc);
    if (cc instanceof JComponent) {
      result = (JComponent) cc;
    }

    return result;
  }

  protected boolean isValid(AnActionEvent e) {
    JComponent component = getComponent(e.getDataContext());
    KeyStroke key = getKey(e);
    return component != null && key != null && component.getActionForKeyStroke(key) != null;
  }
}
