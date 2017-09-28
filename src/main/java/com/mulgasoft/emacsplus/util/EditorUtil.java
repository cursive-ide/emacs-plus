package com.mulgasoft.emacsplus.util;

import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;

import javax.swing.*;

public class EditorUtil {
  private EditorUtil() {
  }

  public static Editor getCurrentEditor(Project project) {
    return FileEditorManager.getInstance(project).getSelectedTextEditor();
  }

  public static void activateCurrentEditor(final Project project) {
    Editor editor = getCurrentEditor(project);
    if (editor != null) {
      final JComponent focusedComponent = editor.getContentComponent();
      ApplicationManager
          .getApplication()
          .invokeLater(() -> IdeFocusManager.getInstance(project).requestFocus(focusedComponent, true));
    }

  }

  public static void closeEditorPopups() {
    if (JBPopupFactory.getInstance().isPopupActive()) {
      ApplicationManager
          .getApplication()
          .invokeLater(() -> IdeEventQueue.getInstance().getPopupManager().closeAllPopups());
    }

  }

  public static PsiFile getPsiFile(Editor editor, Caret caret) {
    return PsiUtilBase.getPsiFileInEditor(caret, editor.getProject());
  }

  public static void checkMarkSelection(Editor editor, Caret caret) {
    boolean result = false;
    if (editor instanceof EditorEx) {
      result = true;
      EditorEx ex = (EditorEx) editor;
      if (caret.hasSelection() && !ex.isStickySelection()) {
        int offset = caret.getOffset();
        int otherEnd = caret.getSelectionStart();
        if (otherEnd == caret.getSelectionEnd()) {
          ex.setStickySelection(true);
        } else if (offset == otherEnd) {
          otherEnd = caret.getSelectionEnd();

          try {
            caret.moveToOffset(otherEnd);
            ex.setStickySelection(true);
          } finally {
            caret.moveToOffset(offset);
          }
        }
      }
    }
  }
}
