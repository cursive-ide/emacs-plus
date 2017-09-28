package com.mulgasoft.emacsplus.util;

import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;

public class KeyUtil {
  private static Keymap getKeymap() {
    return KeymapManager.getInstance().getActiveKeymap();
  }
}
