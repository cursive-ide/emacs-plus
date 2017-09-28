package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.textarea.TextComponentEditor;
import com.intellij.openapi.ui.DialogWrapper;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import com.mulgasoft.emacsplus.util.EditorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class KeyboardQuit extends EmacsPlusWrapper {
  public KeyboardQuit() {
    super(new Handler(getWrappedHandler("EditorEscape")));
    setInjectedContext(true);
  }

  private static class Handler extends EditorActionHandler {
    private boolean isTextComponent = false;
    private final EditorActionHandler wrappedHandler;

    protected Handler(@NotNull EditorActionHandler wrappedHandler) {
      this.wrappedHandler = wrappedHandler;
    }

    @Override
    protected boolean isEnabledForCaret(@NotNull Editor editor, @NotNull Caret caret, DataContext dataContext) {
      if (editor instanceof TextComponentEditor) {
        if (ISHandler.isInISearch(editor)) {
          return false;
        }

        isTextComponent = true;
      }

      return isTextComponent ||
             wrappedHandler.isEnabled(editor, caret, dataContext) ||
             (editor instanceof EditorEx &&
              ((EditorEx) editor).isStickySelection());
    }

    @Override
    protected void doExecute(Editor editor, @Nullable Caret caret, DataContext dataContext) {
      if (isTextComponent) {
        isTextComponent = false;
        if (!cancelIfDialog(editor.getComponent())) {
          editor.getCaretModel().removeCaret(editor.getCaretModel().getPrimaryCaret());
        }

        EditorUtil.closeEditorPopups();
      } else {
        wrappedHandler.execute(editor, caret, dataContext);
      }

      EditorUtil.closeEditorPopups();
      EditorUtil.activateCurrentEditor(CommonDataKeys.PROJECT.getData(dataContext));
    }

    private static boolean cancelIfDialog(Component component) {
      DialogWrapper dw = DialogWrapper.findInstance(component);
      boolean result = dw != null;
      if (result) {
        dw.doCancelAction();
      }

      return result;
    }
  }
}
