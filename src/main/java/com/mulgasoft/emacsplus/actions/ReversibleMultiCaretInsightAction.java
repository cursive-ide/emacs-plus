package com.mulgasoft.emacsplus.actions;

import com.intellij.codeInsight.actions.MultiCaretCodeInsightAction;
import com.intellij.codeInsight.actions.MultiCaretCodeInsightActionHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.actionSystem.DocCommandGroupId;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

public abstract class ReversibleMultiCaretInsightAction extends MultiCaretCodeInsightAction implements DumbAware {
  boolean isReverse;
  private DataContext myDataContext;

  protected ReversibleMultiCaretInsightAction() {
    this(true);
  }

  protected ReversibleMultiCaretInsightAction(boolean reverse) {
    isReverse = true;
    myDataContext = null;
    isReverse = reverse;
  }

  public DataContext getDataContext() {
    return myDataContext;
  }

  @Override
  @NotNull
  protected MultiCaretCodeInsightActionHandler getHandler() {
    return getHandler(this);
  }

  protected abstract MultiCaretCodeInsightActionHandler getHandler(ReversibleMultiCaretInsightAction var1);

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    if (project != null) {
      myDataContext = e.getDataContext();
      Editor hostEditor = CommonDataKeys.EDITOR.getData(myDataContext);
      if (hostEditor != null) {
        actionPerformedImpl(project, hostEditor);
      }
    }

  }

  @Override
  public void actionPerformedImpl(final Project project, final Editor hostEditor) {
    CommandProcessor
        .getInstance()
        .executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
          MultiCaretCodeInsightActionHandler handler = getHandler();

          try {
            iterateCarets(project, hostEditor, handler);
          } finally {
            handler.postInvoke();
            myDataContext = null;
          }

        }), getCommandName(), DocCommandGroupId.noneGroupId(hostEditor.getDocument()));
    hostEditor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
  }

  private void iterateCarets(@NotNull final Project project,
                             @NotNull final Editor hostEditor,
                             @NotNull final MultiCaretCodeInsightActionHandler handler) {
    PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
    final PsiFile psiFile = documentManager.getCachedPsiFile(hostEditor.getDocument());
    documentManager.commitAllDocuments();
    hostEditor.getCaretModel().runForEachCaret(caret -> {
      Editor editor = hostEditor;
      if (psiFile != null) {
        Caret injectedCaret = InjectedLanguageUtil.getCaretForInjectedLanguageNoCommit(caret, psiFile);
        if (injectedCaret != null) {
          caret = injectedCaret;
          editor = injectedCaret.getEditor();
        }
      }

      PsiFile file = PsiUtilBase.getPsiFileInEditor(caret, project);
      if (file != null) {
        handler.invoke(project, editor, caret, file);
      }

    }, isReverse);
  }
}
