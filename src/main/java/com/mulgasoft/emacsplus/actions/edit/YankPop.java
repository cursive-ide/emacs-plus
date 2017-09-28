package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.textarea.TextComponentEditor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.EmacsPlus;
import com.mulgasoft.emacsplus.handlers.YankHandler;
import com.mulgasoft.emacsplus.util.ActionUtil;
import com.mulgasoft.emacsplus.util.EmacsIds;

import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.List;

public class YankPop extends Yanking {
  private static final List<String> yanks = Arrays.asList("yank", "yank-pop");
  private static TextRange dest = null;
  private static boolean yanker = false;
  private static boolean dispatched = false;

  public YankPop() {
    super(new YankPop.myHandler());
    addCommandListener(this, "yank-pop");
  }

  @Override
  public void before(CommandEvent e) {
    yanker = yanks.contains(EmacsPlus.getUltCommand());
  }

  @Override
  public void after(CommandEvent e) {
    dispatched = false;
    if (yanker) {
      popped(dest);
      yanker = false;
    }

  }

  private static final class myHandler extends YankHandler {

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      if (yanker && (!(editor instanceof TextComponentEditor) || caret.getOffset() == Yanking.getOffset())) {
        int index = Yanking.getIndex();
        Transferable[] contents = CopyPasteManager.getInstance().getAllContents();
        if (index >= contents.length) {
          index = 0;
          Yanking.setIndex(index);
        }

        Transferable content = contents[index];
        dest = paste(editor, caret, content, getLength());
      } else if (!dispatched) {
        dispatched = true;
        yanker = false;
        if (editor instanceof EditorEx) {
          ActionUtil.dispatchLater(EmacsIds.EDITOR_PASTE_MULTIPLE, dataContext);
        } else {
          beep();
        }
      }

    }

    private static void beep() {
      ApplicationManager.getApplication().invokeLater(() -> EmacsPlus.beep(true));
    }
  }
}
