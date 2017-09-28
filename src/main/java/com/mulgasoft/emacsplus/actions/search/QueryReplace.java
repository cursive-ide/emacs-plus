package com.mulgasoft.emacsplus.actions.search;

import com.mulgasoft.emacsplus.EmacsPlus;
import com.mulgasoft.emacsplus.util.EmacsIds;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class QueryReplace extends ISearchForward implements FocusListener, KeyListener {
  public QueryReplace() {
    super(true);
  }

  @Override
  protected String getName() {
    return EmacsIds.QUERY_REPLACE_NAME;
  }

  @Override
  protected void changeFieldActions(ISearch searcher, boolean isReplace) {
    super.changeFieldActions(searcher, isReplace);
    super.changeFieldActions(searcher, true);
    searcher.getReplaceField().addFocusListener(this);
    searcher.getReplaceField().addKeyListener(this);
  }

  @Override
  public void focusGained(FocusEvent e) {
    EmacsPlus.resetCommand(getName());
  }

  @Override
  public void focusLost(FocusEvent e) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
    EmacsPlus.resetCommand(getName());
  }

  @Override
  public void keyPressed(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }
}
