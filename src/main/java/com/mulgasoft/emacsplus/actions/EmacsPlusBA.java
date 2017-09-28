package com.mulgasoft.emacsplus.actions;

import com.intellij.openapi.command.CommandEvent;

public interface EmacsPlusBA {
  void before(CommandEvent e);

  void after(CommandEvent e);
}
