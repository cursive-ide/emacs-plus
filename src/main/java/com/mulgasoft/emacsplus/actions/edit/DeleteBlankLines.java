package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.WhiteSpaceHandler;
import org.jetbrains.annotations.NotNull;

public class DeleteBlankLines extends EmacsPlusAction {
  public DeleteBlankLines() {
    this(new DeleteBlankLines.myHandler());
  }

  protected DeleteBlankLines(EditorActionHandler defaultHandler) {
    super(defaultHandler);
  }

  private static class myHandler extends WhiteSpaceHandler {

    @Override
    public boolean isEnabledForCaret(@NotNull Editor editor, @NotNull Caret caret, DataContext dataContext) {
      return !editor.isOneLineMode();
    }

    @Override
    public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
      Document document = editor.getDocument();
      int offset = caret.getOffset();
      int lineNum = document.getLineNumber(offset);
      boolean nextBlank = lineNum < document.getLineCount() - 1 && isBlankLine(document, lineNum + 1);
      if (!isBlankLine(document, lineNum)) {
        if (nextBlank) {
          offset = document.getLineStartOffset(document.getLineNumber(offset) + 1);
          transformSpace(editor, offset, dataContext, "", true);
        }
      } else {
        if (!nextBlank && (lineNum == 0 || !isBlankLine(document, lineNum - 1))) {
          offset = transformSpace(editor, caret, dataContext, "", true);
        } else {
          offset = transformSpace(editor, offset, dataContext, "", true);
          document.insertString(offset, "\n");
        }

        caret.moveToOffset(offset);
        EditorModificationUtil.scrollToCaret(editor);
      }

    }
  }
}
