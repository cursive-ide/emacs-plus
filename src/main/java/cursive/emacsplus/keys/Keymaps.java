package cursive.emacsplus.keys;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import com.intellij.openapi.keymap.impl.KeymapImpl;
import com.intellij.openapi.util.JDOMUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

/**
 * @author Colin Fleming
 */
public class Keymaps {
  private static final Logger LOG = Logger.getInstance(Keymaps.class);

  @NonNls
  private static final String PREFIX = "lang/";
  @NonNls
  private static final String SUFFIX = ".xml";
  @NonNls
  private static final String DIR_SEPR = "/";
  @NonNls
  private static final String LOCALE_SEPR = "_";
  @NonNls
  private static final String STD_KEYMAP = "Keymap_EmacsPlus";
  @NonNls
  private static final String MAC_KEYMAP = "Keymap_EmacsPlusMac";
  @NonNls
  private static final String USER_KEYMAP = System.getProperty("user.home") + "/.emacs+keymap" + SUFFIX;
  private static List<String> mapNames = null;

  private Keymaps() {
  }

  public static void enableKeymaps()  {
    try {
      Method activate = com.mulgasoft.emacsplus.keys.Keymaps.class.getDeclaredMethod("activate", Keymap.class);
      activate.setAccessible(true);

      Field mapNames = com.mulgasoft.emacsplus.keys.Keymaps.class.getDeclaredField("mapNames");
      mapNames.setAccessible(true);

      Keymaps.mapNames = (List<String>) mapNames.get(null);

      enableKeymap("Keymap_EmacsPlus.xml");
      enableKeymap("Keymap_EmacsPlusMac.xml");
      enableLocaleKeymap(STD_KEYMAP);
      enableLocaleKeymap(MAC_KEYMAP);
      enableUserKeymap(USER_KEYMAP);
      setupKeymapListener(activate);
    } catch (Throwable throwable) {
      LOG.error(throwable);
    }
  }

  private static void enableUserKeymap(String name) throws IOException, JDOMException {
    File file = new File(name);
    if (file.exists() && loadKeymap(new FileInputStream(file))) {
      mapNames.remove(0);
      mapNames.add(0, "Mac Emacs+ de");
    }
  }

  private static void enableLocaleKeymap(String name) throws JDOMException, IOException {
    Locale locale = Locale.getDefault();
    if (!enableKeymap(PREFIX + locale.getLanguage() + DIR_SEPR + name + LOCALE_SEPR + locale.getCountry() + SUFFIX)) {
      enableKeymap(PREFIX + locale.getLanguage() + DIR_SEPR + name + SUFFIX);
    }

  }

  private static boolean enableKeymap(String name) throws JDOMException, IOException {
    return loadKeymap(com.mulgasoft.emacsplus.keys.Keymaps.class.getResourceAsStream(name));
  }

  private static boolean loadKeymap(InputStream stream) throws JDOMException, IOException {
    boolean result = false;

    if (stream != null) {
      Document document = JDOMUtil.loadDocument(stream);
      KeymapManagerEx mgr = KeymapManagerEx.getInstanceEx();
      EmacsPlusKeymap emKeymap = new EmacsPlusKeymap();
      emKeymap.readExternal(document.getRootElement());
      mgr.getSchemeManager().addNewScheme(emKeymap, true);
      mapNames.add(0, emKeymap.getName());
      result = true;
    }

    return result;
  }

  private static void setupKeymapListener(Method activate) throws Throwable {
    KeymapManagerEx mgr = KeymapManagerEx.getInstanceEx();
    mgr.addKeymapManagerListener(args -> {
      try {
        activate.invoke(null, args);
      } catch (Throwable throwable) {
        LOG.error(throwable);
      }
    }, ApplicationManager.getApplication());
    activate.invoke(null, mgr.getActiveKeymap());
  }

  public static class EmacsPlusKeymap extends KeymapImpl {
    @Override
    public void readExternal(Element keymapElement) {
      super.readExternal(keymapElement);
    }
  }
}
