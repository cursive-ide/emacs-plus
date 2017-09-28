package com.mulgasoft.emacsplus.actions.edit.comment;

import com.intellij.codeInsight.actions.MultiCaretCodeInsightActionHandler;
import com.intellij.lang.LanguageCommenters;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.mulgasoft.emacsplus.actions.ReversibleMultiCaretInsightAction;
import com.mulgasoft.emacsplus.handlers.CommentHandler;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import org.jetbrains.annotations.NotNull;

public abstract class CommentAction extends ReversibleMultiCaretInsightAction {
  protected abstract CommentHandler getMyHandler();

  @Override
  protected MultiCaretCodeInsightActionHandler getHandler(ReversibleMultiCaretInsightAction var1) {
    CommentHandler mine = getMyHandler();
    mine.preInvoke(var1);
    return mine;
  }

  @Override
  protected boolean isValidFor(@NotNull Project project,
                               @NotNull Editor editor,
                               @NotNull Caret caret,
                               @NotNull PsiFile file) {
    boolean result = false;
    if (!ISHandler.isInISearch(editor)) {
      FileType fileType = file.getFileType();
      if (fileType instanceof AbstractFileType) {
        result = ((AbstractFileType) fileType).getCommenter() != null;
      } else if (LanguageCommenters.INSTANCE.forLanguage(file.getLanguage()) == null &&
                 LanguageCommenters.INSTANCE.forLanguage(file.getViewProvider().getBaseLanguage()) == null) {
        PsiElement host = InjectedLanguageManager.getInstance(project).getInjectionHost(file);
        result = host != null && LanguageCommenters.INSTANCE.forLanguage(host.getLanguage()) != null;
      } else {
        result = true;
      }
    }

    return result;
  }
}
