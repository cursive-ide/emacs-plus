package com.mulgasoft.emacsplus.handlers;

import com.intellij.find.FindModel;
import com.intellij.find.SearchReplaceComponent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.mulgasoft.emacsplus.actions.search.ISearch;
import com.mulgasoft.emacsplus.actions.search.ISearch;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public abstract class ISHandler extends YankHandler {
  private final char[] regTokens = {'{', '}', '(', ')', '[', ']', '\\', '^', '$', '.', '|', '?', '*', '+'};

  public static FindModel getFindModel(Editor editor) {
    FindModel findModel = null;
    ISearch searcher = ISearch.from(editor);
    if (searcher != null) {
      findModel = searcher.getFindModel();
    }

    return findModel;
  }

  @Nullable
  public static JTextComponent getSearchField(Editor editor) {
    if (editor != null) {
      JComponent hc = editor.getHeaderComponent();
      if (hc instanceof SearchReplaceComponent) {
        return ((SearchReplaceComponent) hc).getSearchTextComponent();
      }
    }
    return null;
  }

  @Nullable
  public static JTextComponent getReplaceField(Editor editor) {
    if (editor != null) {
      JComponent hc = editor.getHeaderComponent();
      if (hc instanceof SearchReplaceComponent) {
        return ((SearchReplaceComponent) hc).getReplaceTextComponent();
      }
    }
    return null;
  }

  public static Editor getTextEditor(Project project) {
    return FileEditorManager.getInstance(project).getSelectedTextEditor();
  }

  public static boolean isISearchField(Editor isEditor) {
    JTextComponent field = getSearchField(isEditor);
    return field != null && field == isEditor.getComponent();
  }

  public static boolean isISReplaceField(Editor isEditor) {
    JTextComponent field = getReplaceField(isEditor);
    return field != null && field == isEditor.getComponent();
  }

  public static boolean isInISearch(Editor isEditor) {
    return isISearchField(isEditor) || isISReplaceField(isEditor);
  }

  protected static boolean isRegexp(Editor isEditor) {
    FindModel findModel = getFindModel(getTextEditor(isEditor.getProject()));
    return findModel != null && findModel.isRegularExpressions();
  }

  protected String fixYank(Editor isEditor, String text) {
    String result = text;
    if (isRegexp(isEditor)) {
      StringBuilder sb = new StringBuilder();

      for (char c : text.toCharArray()) {
        if (c == '\n') {
          sb.append('\\');
          sb.append('n');
        } else {
          for (char r : regTokens) {
            if (c == r) {
              sb.append('\\');
              break;
            }
          }

          sb.append(c);
        }
      }

      result = sb.toString();
    }

    return result;
  }

  @Override
  protected boolean isEnabledForCaret(Editor editor, Caret caret, DataContext dataContext) {
    return isISearchField(editor);
  }
}
