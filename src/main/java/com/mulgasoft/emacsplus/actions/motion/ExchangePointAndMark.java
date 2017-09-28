package com.mulgasoft.emacsplus.actions.motion;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.util.ActionUtil;
import com.mulgasoft.emacsplus.util.EmacsIds;

public class ExchangePointAndMark extends EmacsPlusAction {
  private static DataContext dc = null;

  public ExchangePointAndMark() {
    super(new ExchangePointAndMark.myHandler());
    EmacsPlusAction.addCommandListener(this, getName());
  }

  protected static String getName() {
    return EmacsIds.EXCHANGE_PM_NAME;
  }

  @Override
  public void after(CommandEvent e) {
    if (dc != null) {
      ActionUtil.dispatchLater(EmacsIds.EDITOR_SWAP_BOUNDARIES_ID, dc);
      Editor editor = getEditor(dc);
      dc = null;
      if (editor.getCaretModel().getCaretCount() == 1) {
        editor
            .getScrollingModel()
            .scrollTo(editor.getCaretModel().getPrimaryCaret().getLogicalPosition(), ScrollType.MAKE_VISIBLE);
      }
    }

  }

  private static class myHandler extends EditorActionHandler {
    private myHandler() {
      super(true);
    }

    @Override
    protected void doExecute(Editor editor, Caret caret, DataContext dataContext) {
      SelectionModel selectionModel = editor.getSelectionModel();
      if (selectionModel.hasSelection()) {
        VisualPosition pos = caret.getVisualPosition();
        VisualPosition vpos = selectionModel.getLeadSelectionPosition();
        VisualPosition epos = selectionModel.getSelectionEndPosition();
        VisualPosition spos = selectionModel.getSelectionStartPosition();
        if (!pos.equals(spos) && !pos.equals(epos)) {
          caret.moveToVisualPosition(vpos.equals(epos) ? spos : epos);
        }

        dc = dataContext;
      }

    }
  }
}
