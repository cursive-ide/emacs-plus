package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.YankHandler;

public class Yank extends Yanking {
  public Yank() {
    super(new Yank.myHandler());
    addCommandListener(this, "yank");
  }

  @Override
  public void before(CommandEvent e) {
    reset();
  }

  private static final class myHandler extends YankHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      Yanking.yanked(yankIt(editor, caret));
    }
  }
}
