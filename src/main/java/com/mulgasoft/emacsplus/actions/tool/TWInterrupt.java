package com.mulgasoft.emacsplus.actions.tool;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.mulgasoft.emacsplus.util.EditorUtil;

public class TWInterrupt extends TWAction {
  @Override
  public void actionPerformed(AnActionEvent e) {
    Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
    checkTW(e, project);
    EditorUtil.closeEditorPopups();
    EditorUtil.activateCurrentEditor(project);
  }

  private static void checkTW(AnActionEvent e, Project project) {
    ToolWindow toolWindow = e.getData(PlatformDataKeys.TOOL_WINDOW);
    if (toolWindow != null) {
      ToolWindowManager manager = ToolWindowManager.getInstance(project);
      if (manager.isMaximized(toolWindow)) {
        manager.setMaximized(toolWindow, false);
      }
    }

  }

  @Override
  protected boolean isValid(AnActionEvent e) {
    return getComponent(e.getDataContext()) != null;
  }
}
