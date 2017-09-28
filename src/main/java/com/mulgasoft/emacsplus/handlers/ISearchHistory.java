package com.mulgasoft.emacsplus.handlers;

import com.intellij.find.FindSettings;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.ui.JBColor;
import com.intellij.ui.LightColors;
import com.mulgasoft.emacsplus.EmacsPlus;
import com.mulgasoft.emacsplus.util.EmacsIds;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public abstract class ISearchHistory extends ISHandler {
  protected static int ourSearchIndex = 0;
  protected static int ourReplaceIndex = 0;
  private static int ourStartOffset = 0;
  protected boolean isSearchField = false;
  protected boolean isReplaceField = false;
  private boolean isReset = false;
  private Color myReplaceBackground;
  private static final List<String>
      keys =
      Arrays.asList(EmacsIds.ISEARCH_H_PREVIOUS_NAME, EmacsIds.ISEARCH_H_NEXT_NAME);
  private static final List<String> iskeys = Arrays.asList(EmacsIds.ISEARCH_NAME,
                                                           EmacsIds.ISEARCH_REGEXP_NAME,
                                                           EmacsIds.ISEARCH_BACK_NAME,
                                                           EmacsIds.ISEARCH_BACK_REGEXP_NAME,
                                                           EmacsIds.QUERY_REPLACE_NAME,
                                                           EmacsIds.QUERY_REPLACE_REGEXP_NAME,
                                                           IdeBundle.message(EmacsIds.ACTION_FIND_NEXT_NAME));
  private JTextComponent myReplaceField;
  private final DocumentListener myReplaceListener;

  protected ISearchHistory() {
    myReplaceBackground = JBColor.WHITE;
    myReplaceField = null;
    myReplaceListener = new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        restoreBackground();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        restoreBackground();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        restoreBackground();
      }
    };
  }

  private void checkReset(Editor isEditor) {
    if (!wasPreviousHistory(isEditor) && !wasPreviousSearch(isEditor)) {
      isReset = true;
      ourSearchIndex = 0;
      ourReplaceIndex = 0;
      if (isReplaceField) {
        myReplaceBackground = (new JTextField()).getBackground();
      }
    } else {
      isReset = false;
    }

  }

  private static boolean wasPreviousHistory(Editor isEditor) {
    return keys.contains(EmacsPlus.getUltCommand());
  }

  private boolean wasPreviousSearch(Editor isEditor) {
    boolean result = false;
    if (iskeys.contains(EmacsPlus.getUltCommand())) {
      ourStartOffset = getTextEditor(isEditor.getProject()).getCaretModel().getPrimaryCaret().getOffset();
      String lastSearch = getText(isEditor);
      if (!lastSearch.isEmpty()) {
        String[] vals = getRecents();
        if (vals.length > 0 && lastSearch.equals(vals[vals.length - 1])) {
          result = true;
          if (isSearchField) {
            ourSearchIndex = vals.length - 1;
          } else {
            ourReplaceIndex = vals.length - 1;
          }
        }
      }
    }

    return result;
  }

  protected boolean isReset() {
    return isReset;
  }

  protected void setText(@NotNull Editor isEditor, String[] vals, int index) {
    if (!isSearchField || !isReset && ourSearchIndex == index) {
      if (!isReplaceField || !isReset && ourReplaceIndex == index) {
        beep(isEditor);
      } else {
        ourReplaceIndex = index;
        setText(isEditor, vals[index]);
      }
    } else {
      ourSearchIndex = index;
      setText(isEditor, vals[index]);
    }

  }

  protected static void setText(@NotNull Editor isEditor, String text) {
    getTextEditor(isEditor.getProject()).getCaretModel().moveToOffset(ourStartOffset);
    JComponent field = isEditor.getComponent();
    if (field instanceof JTextComponent) {
      ((JTextComponent) field).setText(text);
    } else {
      beep(isEditor);
    }

  }

  protected static String getText(@NotNull Editor isEditor) {
    String result = "";
    JComponent field = isEditor.getComponent();
    if (field instanceof JTextComponent) {
      result = ((JTextComponent) field).getText();
    }

    return result;
  }

  protected int getIndex() {
    return isSearchField ? ourSearchIndex : ourReplaceIndex;
  }

  protected void setIndex(int index) {
    if (isSearchField) {
      ourSearchIndex = index;
    } else {
      ourReplaceIndex = index;
    }

  }

  protected String[] getHistory(Editor isEditor) {
    checkReset(isEditor);
    return getRecents();
  }

  private String[] getRecents() {
    FindSettings settings = FindSettings.getInstance();
    return isSearchField ? settings.getRecentFindStrings() : settings.getRecentReplaceStrings();
  }

  protected static void beep(Editor isEditor) {
    JComponent field = isEditor.getComponent();
    Color back = field.getBackground();
    field.setBackground(LightColors.CYAN);
    EmacsPlus.beep();
  }

  @Override
  protected boolean isEnabledForCaret(Editor editor, Caret caret, DataContext dataContext) {
    isSearchField = isISearchField(editor);
    isReplaceField = isISReplaceField(editor);
    if (isReplaceField && myReplaceField == null) {
      JComponent j = editor.getComponent();
      if (j instanceof JTextComponent) {
        myReplaceField = (JTextComponent) j;
        myReplaceField.getDocument().addDocumentListener(myReplaceListener);
      }
    }

    return isSearchField || isReplaceField;
  }

  private void restoreBackground() {
    myReplaceField.setBackground(myReplaceBackground);
  }
}
