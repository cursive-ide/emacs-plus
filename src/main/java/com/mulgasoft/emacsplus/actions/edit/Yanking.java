package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;

public abstract class Yanking extends EmacsPlusAction {
  private static int ourLength = 0;
  private static int ourIndex = 0;
  private static int ourOffset = 0;

  protected Yanking(EditorActionHandler defaultHandler) {
    super(defaultHandler);
  }

  private static void setLength(int length) {
    ourLength = length;
  }

  protected static int getLength() {
    return ourLength;
  }

  protected static void setIndex(int index) {
    ourIndex = index;
  }

  protected static int getIndex() {
    return ourIndex;
  }

  private static void setOffset(int offset) {
    ourOffset = offset;
  }

  protected static int getOffset() {
    return ourOffset;
  }

  protected static void reset() {
    setLength(0);
    setOffset(0);
    setIndex(1);
  }

  protected static void yanked(TextRange location) {
    if (location != null) {
      setLength(location.getEndOffset() - location.getStartOffset());
      setOffset(location.getEndOffset());
    }

  }

  protected static void popped(TextRange location) {
    yanked(location);
    ++ourIndex;
  }
}
