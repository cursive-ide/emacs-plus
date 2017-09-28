package com.mulgasoft.emacsplus.actions.motion;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.mulgasoft.emacsplus.EmacsPlus;
import com.mulgasoft.emacsplus.handlers.EmacsPlusCaretHandler;
import com.mulgasoft.emacsplus.util.EmacsIds;

import java.awt.*;

public class Recenter extends EditorAction {
  public Recenter() {
    super(new Recenter.myHandler());
  }

  public static void setScrollMargin(int sm) {
    Recenter.myHandler.class_0.scrollMargin = sm;
  }

  private static class myHandler extends EmacsPlusCaretHandler {
    private Recenter.myHandler.class_0 state;

    private myHandler() {
      state = Recenter.myHandler.class_0.field_3;
    }

    @Override
    protected void doXecute(Editor var1, Caret var2, DataContext var3) {
      if (!EmacsIds.RECENTER_NAME.equals(EmacsPlus.getUltCommand())) {
        state = Recenter.myHandler.class_0.field_3;
      }

      int lineHeight = var1.getLineHeight();
      int cpos = var2.getVisualPosition().getLine() * lineHeight;
      Rectangle visibleArea = var1.getScrollingModel().getVisibleArea();
      var1.getScrollingModel().scrollVertically(class_0.getLine(this, cpos, visibleArea.height, lineHeight));
    }

    private enum class_0 {
      // $FF: renamed from: B com.mulgasoft.emacsplus.actions.motion.Recenter$myHandler$CS
      field_1,
      // $FF: renamed from: T com.mulgasoft.emacsplus.actions.motion.Recenter$myHandler$CS
      field_2,
      // $FF: renamed from: C com.mulgasoft.emacsplus.actions.motion.Recenter$myHandler$CS
      field_3;

      static int scrollMargin = 0;

      public static void setScrollMargin(int sm) {
        scrollMargin = sm;
      }

      public static int getLine(Recenter.myHandler h, int caretLine, int areaHeight, int lineHeight) {
        switch (h.state) {
          case field_1:
            h.state = field_3;
            return caretLine + lineHeight - areaHeight + scrollMargin * lineHeight;
          case field_2:
            h.state = field_1;
            return caretLine - scrollMargin * lineHeight;
          case field_3:
            h.state = field_2;
          default:
            return caretLine - areaHeight / 2;
        }
      }
    }
  }
}
