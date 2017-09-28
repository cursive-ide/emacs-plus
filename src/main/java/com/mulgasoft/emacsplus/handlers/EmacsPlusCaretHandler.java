package com.mulgasoft.emacsplus.handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import org.jetbrains.annotations.NotNull;

public abstract class EmacsPlusCaretHandler extends EditorActionHandler {
  protected EmacsPlusCaretHandler() {
    super(true);
  }

  protected EmacsPlusCaretHandler(boolean runForEachCaret) {
    super(runForEachCaret);
  }

  protected static Caret checkCaret(@NotNull Editor editor, Caret caret) {
    return caret == null ? editor.getCaretModel().getCurrentCaret() : caret;
  }

  @Override
  protected void doExecute(Editor editor, Caret caret, DataContext dataContext) {
    doXecute(editor, checkCaret(editor, caret), dataContext);
  }

  protected abstract void doXecute(Editor var1, Caret var2, DataContext var3);
}
