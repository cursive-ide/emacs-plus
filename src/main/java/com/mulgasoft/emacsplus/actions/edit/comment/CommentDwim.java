package com.mulgasoft.emacsplus.actions.edit.comment;

import com.intellij.codeInsight.actions.MultiCaretCodeInsightActionHandler;
import com.intellij.codeInsight.generation.CommentByBlockCommentHandler;
import com.intellij.codeInsight.generation.CommentByLineCommentHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.mulgasoft.emacsplus.handlers.CommentHandler;

public class CommentDwim extends CommentAction {
  @Override
  protected CommentHandler getMyHandler() {
    return new CommentDwim.myHandler();
  }

  protected static final class myHandler extends CommentHandler {
    @Override
    protected void invokeAction(Editor editor, Caret caret, DataContext dataContext, PsiFile file) {
      if (caret.hasSelection()) {
        commentSelection(editor, caret, dataContext, file);
      } else {
        commentLine(editor, caret, dataContext);
      }

    }

    private void commentSelection(Editor editor, Caret caret, DataContext d, PsiFile file) {
      Document document = editor.getDocument();
      int start = caret.getSelectionStart();
      int end = caret.getSelectionEnd();
      String text = document.getText(new TextRange(start, end)).trim();
      String seq = getLineComment();
      Project project = editor.getProject();
      if (seq != null && text.startsWith(seq)) {
        invoke(new CommentByLineCommentHandler(), project, editor, caret, file);
      } else if ((seq = getBlockStart()) != null && text.startsWith(seq)) {
        invoke(new CommentByBlockCommentHandler(), project, editor, caret, file);
      } else {
        int ls = getLineStartOffset(document, start);
        int es = getLineEndOffset(document, end);
        if ((start != ls || end != es && end != getLineStartOffset(document, end)) && hasBlockComments()) {
          invoke(new CommentByBlockCommentHandler(), project, editor, caret, file);
        } else {
          invoke(new CommentByLineCommentHandler(), project, editor, caret, file);
        }
      }

    }

    private static void invoke(MultiCaretCodeInsightActionHandler handler,
                               Project project,
                               Editor editor,
                               Caret caret,
                               PsiFile file) {
      handler.invoke(project, editor, caret, file);
      handler.postInvoke();
    }
  }
}
