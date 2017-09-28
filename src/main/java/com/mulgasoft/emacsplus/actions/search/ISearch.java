package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.EditorSearchSession;
import com.intellij.find.FindModel;
import com.intellij.find.SearchReplaceComponent;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class ISearch {
  private final SearchReplaceComponent searchComp;
  private final EditorSearchSession session;

  private ISearch(Editor editor, SearchReplaceComponent component) {
    session = EditorSearchSession.get(editor);
    searchComp = component;
  }

  @Nullable
  public static ISearch from(Editor editor) {
    if (editor != null) {
      JComponent hc = editor.getHeaderComponent();
      if (hc instanceof SearchReplaceComponent) {
        return new ISearch(editor, (SearchReplaceComponent) hc);
      }
    }

    return null;
  }

  public JComponent getComponent() {
    return searchComp;
  }

  public JTextComponent getSearchField() {
    return searchComp.getSearchTextComponent();
  }

  public JTextComponent getReplaceField() {
    return searchComp.getReplaceTextComponent();
  }

  public FindModel getFindModel() {
    return session.getFindModel();
  }

  public boolean hasMatches() {
    return session.hasMatches();
  }

  public void searchForward() {
    session.searchForward();
  }

  public void searchBackward() {
    session.searchBackward();
  }

  public void replaceCurrent() {
    session.replaceCurrent();
  }

  public void showHistory(boolean var1, JTextComponent var2) {
  }

  public void requestFocus() {
    searchComp.requestFocus();
  }

  public void close() {
    session.close();
  }
}
