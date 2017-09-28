package com.mulgasoft.emacsplus.util;

import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretStateTransferableData;
import com.intellij.openapi.editor.ClipboardTextPerCaretSplitter;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ide.KillRingTransferable;
import com.intellij.openapi.util.text.StringUtil;
import com.mulgasoft.emacsplus.EmacsPlus;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;

public class KillCmdUtil {
  public static final Logger LOG = Logger.getInstance(KillCmdUtil.class);

  private KillCmdUtil() {
  }

  public static void killWrapper(String cmdId, DataContext data, Document doc) {
    killWrapper(cmdId, data, doc, false);
  }

  public static boolean wasNextKill() {
    return EmacsIds.APPEND_KILL_NAME.equals(EmacsPlus.getPenultCommand());
  }

  public static boolean isNextKill() {
    return EmacsIds.APPEND_KILL_NAME.equals(EmacsPlus.getUltCommand());
  }

  public static KillCmdUtil.KillRingInfo beforeKill() {
    int killLen = 0;
    Transferable prevContent = null;
    if (isNextKill()) {
      Transferable[] contents = CopyPasteManager.getInstance().getAllContents();
      killLen = contents.length;
      if (killLen > 0) {
        prevContent = contents[0];
        if (prevContent instanceof KillRingTransferable) {
          ((KillRingTransferable) prevContent).setReadyToCombine(false);
        }
      }
    }

    return new KillCmdUtil.KillRingInfo(killLen, prevContent);
  }

  public static void afterKill(KillCmdUtil.KillRingInfo info, Document doc, boolean isCut) {
    if (info != null && info.topContent != null) {
      Transferable[] contents = CopyPasteManager.getInstance().getAllContents();
      if (contents.length > 1) {
        String oldD = getTransferableText(info.topContent);
        String newD = getTransferableText(contents[0]);
        if (CopyPasteManager.getInstance() instanceof CopyPasteManagerEx) {
          int start = -1;
          int end = -1;
          if (contents[0] instanceof KillRingTransferable) {
            start = ((KillRingTransferable) contents[0]).getStartOffset();
            end = ((KillRingTransferable) contents[0]).getEndOffset();
          }

          KillRingTransferable newT = new KillRingTransferable(oldD + newD, doc, start, end, isCut);
          contents = CopyPasteManager.getInstance().getAllContents();
          CopyPasteManagerEx cpm = (CopyPasteManagerEx) CopyPasteManager.getInstance();
          cpm.removeContent(contents[1]);
          cpm.removeContent(contents[0]);
          cpm.setContents(newT);
          newT.setReadyToCombine(true);
        }
      }
    }

  }

  public static void killWrapper(String cmdId, DataContext data, Document doc, boolean isCut) {
    Transferable prevContent = null;
    KillCmdUtil.KillRingInfo info = null;

    try {
      if (wasNextKill()) {
        Transferable[] contents = CopyPasteManager.getInstance().getAllContents();
        int killLen = contents.length;
        if (killLen > 0) {
          prevContent = contents[0];
          if (prevContent instanceof KillRingTransferable) {
            ((KillRingTransferable) prevContent).setReadyToCombine(false);
          }
        }

        info = new KillCmdUtil.KillRingInfo(killLen, prevContent);
      }

      ActionUtil.dispatch(cmdId, data);
    } finally {
      afterKill(info, doc, isCut);
    }

  }

  @NotNull
  public static String getTransferableText(@NotNull Transferable data) {
    return getTransferableText(data, "\n");
  }

  public static String getTransferableText(@NotNull Transferable data, @NotNull String lineSepr) {
    String text = EditorModificationUtil.getStringContent(data);
    if (text != null) {
      try {
        CaretStateTransferableData caretData = data.isDataFlavorSupported(CaretStateTransferableData.FLAVOR)
        ? (CaretStateTransferableData) data.getTransferData(CaretStateTransferableData.FLAVOR)
        : null;
        if (caretData == null) {
          text = (String) data.getTransferData(DataFlavor.stringFlavor);
        } else {
          List<String>
              segments =
              (new ClipboardTextPerCaretSplitter()).split(text, caretData, caretData.startOffsets.length);
          StringBuilder buf = new StringBuilder();

          for (Object s : segments) {
            buf.append(s);
          }

          text = buf.toString();
        }
      } catch (Exception e) {
        LOG.error(e);
      }

      text = StringUtil.convertLineSeparators(text, lineSepr);
    }

    return text == null ? "" : text;
  }

  public static class KillRingInfo {
    int killRingLen = 0;
    Transferable topContent = null;

    KillRingInfo(int killRingLen, Transferable topContent) {
      this.killRingLen = killRingLen;
      this.topContent = topContent;
    }
  }
}
