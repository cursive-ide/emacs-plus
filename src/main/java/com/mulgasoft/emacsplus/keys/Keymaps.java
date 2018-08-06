package com.mulgasoft.emacsplus.keys;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManagerListener;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import com.intellij.openapi.keymap.impl.KeymapImpl;
import com.intellij.openapi.util.JDOMUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Keymaps {
  @NonNls
  private static final String PREFIX = "lang/";
  @NonNls
  private static final String SUFFIX = ".xml";
  @NonNls
  private static final String STD_KEYMAP = "Keymap_EmacsPlus";
  @NonNls
  private static final String MAC_KEYMAP = "Keymap_EmacsPlusMac";
  @NonNls
  private static final String USER_KEYMAP = System.getProperty("user.home") + "/.emacs+keymap" + SUFFIX;
  @NonNls
  private static final String EMACS = "Emacs+";

  @NonNls
  private static final String DE = "de";
  @NonNls
  private static final String ES = "es";
  @NonNls
  private static final String FR = "fr";
  private static final List<String> mapNames = new ArrayList();
  private static boolean isAlt = true;
  private static String isLocale = null;

  private static final Logger LOG = Logger.getInstance(Keymaps.class);

  private Keymaps() {
  }

  public static void enableKeymaps() {
    enableKeymap('/' + STD_KEYMAP + SUFFIX);
    enableKeymap('/' + MAC_KEYMAP + SUFFIX);
    enableLocaleKeymap(STD_KEYMAP);
    enableLocaleKeymap(MAC_KEYMAP);
    enableUserKeymap(USER_KEYMAP);
    setupKeymapListener();
  }

  private static void enableUserKeymap(String name) {
    try {
      File file = new File(name);
      if (file.exists() && loadKeymap(new FileInputStream(file))) {
        mapNames.remove(0);
        mapNames.add(0, "Mac Emacs+ de");
      }
    } catch (FileNotFoundException e) {
      LOG.error(e);
    }

  }

  private static void enableLocaleKeymap(String name) {
    Locale l = Locale.getDefault();
    if (!enableKeymap('/' + PREFIX + l.getLanguage() + '/' + name + '_' + l.getCountry() + SUFFIX)) {
      enableKeymap('/' + PREFIX + l.getLanguage() + '/' + name + SUFFIX);
    }

  }

  private static boolean enableKeymap(String name) {
    return loadKeymap(Keymaps.class.getResourceAsStream(name));
  }

  private static boolean loadKeymap(InputStream stream) {
    boolean result = false;

    try {
      if (stream != null) {
        Document document = JDOMUtil.loadDocument(stream);
        KeymapManagerEx mgr = KeymapManagerEx.getInstanceEx();
        EmacsPlusKeymap emKeymap = new EmacsPlusKeymap();
        emKeymap.readExternal(document.getRootElement());
        mgr.getSchemeManager().addNewScheme(emKeymap, true);
        mapNames.add(0, emKeymap.getName());
        result = true;
      }
    } catch (Exception e) {
      LOG.error(e);
    }

    return result;
  }

  public static class EmacsPlusKeymap extends KeymapImpl {
    @Override
    public void readExternal(Element keymapElement) {
      super.readExternal(keymapElement);
    }
  }

  public static boolean isAlt() {
    return isAlt;
  }

  public static String isLocale() {
    return isLocale;
  }

  private static void setupKeymapListener() {
    KeymapManagerEx mgr = KeymapManagerEx.getInstanceEx();
    mgr.addKeymapManagerListener(new KeymapManagerListener() {
      @Override
      public void activeKeymapChanged(@Nullable Keymap keymap) {
        activate(keymap);
      }
    }, ApplicationManager.getApplication());
    activate(mgr.getActiveKeymap());
  }

  private static void activate(Keymap keymap) {
    if (keymap != null) {
      Keymap map = keymap;

      do {
        isAlt = true;
        isLocale = null;
        String name = map.getName();
        if (mapNames.contains(name)) {
          if (!name.startsWith(EMACS)) {
            isAlt = false;
          }

          int ind = name.lastIndexOf(EMACS) + EMACS.length();
          ++ind;
          if (ind < name.length()) {
            isLocale = name.substring(ind, name.length());
          }
          break;
        }
      } while ((map = map.getParent()) != null);
    }

  }

  public static KeyStroke getIntlKeyStroke(int key) {
    KeyStroke ks = null;
    switch (key) {
      case 47:
        if (isLocale != null) {
          if ("fr".equals(isLocale)) {
            ks = KeyStroke.getKeyStroke(513, 192);
          } else {
            ks = KeyStroke.getKeyStroke(55, 192);
          }
        } else {
          ks = KeyStroke.getKeyStroke(47, 2);
        }
        break;
      case 153:
        if (isLocale != null) {
          ks = KeyStroke.getKeyStroke(153, getMeta());
        } else {
          ks = KeyStroke.getKeyStroke(44, getMeta() | 64);
        }
        break;
      case 160:
        if (isLocale != null) {
          ks = KeyStroke.getKeyStroke(153, getMeta() | 64);
        } else {
          ks = KeyStroke.getKeyStroke(46, getMeta() | 64);
        }
    }

    return ks;
  }

  public static int getMeta() {
    return isAlt() ? 512 : 256;
  }
}
