package com.mulgasoft.emacsplus.handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.textarea.TextComponentEditor;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.util.KillCmdUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.Transferable;

public abstract class YankHandler extends EmacsPlusWriteHandler {
  private static final boolean ourYankReplace = true;

  protected static Transferable getData() {
    return EditorModificationUtil.getContentsToPasteToEditor(null);
  }

  private String getText(Transferable data, Editor editor) {
    return KillCmdUtil.getTransferableText(data, getSepr(editor));
  }

  @NonNls
  protected String getSepr(Editor editor) {
    return "\n";
  }

  public TextRange paste(@NotNull Editor editor, @NotNull Caret caret, @NotNull Transferable data, int length) {
    String text = getText(data, editor);
    int caretOffset = caret.getOffset();
    int newOff = caretOffset - length;
    TextRange result = null;
    if (newOff >= 0) {
      result = new TextRange(newOff, newOff + text.length());
      editor.getDocument().replaceString(caretOffset - length, caretOffset, text);
    }

    return result;
  }

  public TextRange paste(@NotNull Editor editor, @NotNull TextRange range, @NotNull Transferable data) {
    String text = getText(data, editor);
    TextRange result = new TextRange(range.getStartOffset(), range.getStartOffset() + text.length());
    editor.getDocument().replaceString(range.getStartOffset(), range.getEndOffset(), text);
    return result;
  }

  public TextRange paste(@NotNull Editor editor, @NotNull Caret caret, @NotNull Transferable data) {
    String text = getText(data, editor);
    int caretOffset = caret.getOffset();
    TextRange result = new TextRange(caretOffset, caretOffset + text.length());
    if (editor instanceof TextComponentEditor) {
      Document doc = editor.getDocument();
      doc.insertString(caret.getOffset(), text);
    } else {
      EditorModificationUtil.insertStringAtCaret(editor, text, false, true);
    }

    return result;
  }

  protected TextRange yankIt(Editor editor, Caret caret) {
    Transferable data = getData();
    boolean isReplace = ourYankReplace || editor.isOneLineMode();
    TextRange replace = null;
    SelectionModel sm = editor.getSelectionModel();
    if (sm.hasSelection()) {
      replace = new TextRange(sm.getSelectionStart(), sm.getSelectionEnd());
      sm.removeSelection();
    }

    if (editor instanceof EditorEx) {
      EditorEx editorEx = (EditorEx) editor;
      if (editorEx.isStickySelection()) {
        editorEx.setStickySelection(false);
      }
    }

    TextRange location;
    if (replace != null && isReplace) {
      location = paste(editor, replace, data);
    } else {
      location = paste(editor, caret, data);
    }

    return location;
  }

  @Override
  protected boolean isEnabledForCaret(Editor editor, Caret caret, DataContext dataContext) {
    return !ISHandler.isISearchField(editor) && super.isEnabledForCaret(editor, caret, dataContext);
  }
}
