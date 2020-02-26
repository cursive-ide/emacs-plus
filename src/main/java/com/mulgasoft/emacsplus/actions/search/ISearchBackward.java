package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.FindUtil;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.mulgasoft.emacsplus.util.ActionUtil;
import com.mulgasoft.emacsplus.util.EmacsIds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.editor.actions.IncrementalFindAction.SEARCH_DISABLED;

public class ISearchBackward extends EditorAction {
  protected ISearchBackward() {
    this(new Handler());
  }

  protected ISearchBackward(EditorActionHandler handler) {
    super(handler);
  }

  protected static class Handler extends EditorActionHandler {
    @Override
    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
      ISearch searcher = findSearcher(editor, dataContext);
      if (searcher != null) {
        searcher.searchBackward();
      }
    }

    @Override
    protected boolean isEnabledForCaret(@NotNull Editor editor, @NotNull Caret caret, DataContext dataContext) {
      Project project = dataContext.getData(CommonDataKeys.PROJECT);
      if (project == null) {
        return false;
      }
      return !editor.isOneLineMode() && !SEARCH_DISABLED.get(editor, false);
    }
  }

  protected static ISearch findSearcher(@NotNull Editor editor, DataContext dataContext) {
    ISearch searcher = ISearch.from(editor);
    if (searcher == null) {
      ActionUtil.dispatch(EmacsIds.ISEARCH_ID, dataContext);
      searcher = ISearch.from(editor);
    } else {
      searcher.requestFocus();
      FindUtil.configureFindModel(false, editor, searcher.getFindModel(), false);
    }
    return searcher;
  }
}
