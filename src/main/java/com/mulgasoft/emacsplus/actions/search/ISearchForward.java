package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindModel.FindModelObserver;
import com.intellij.find.FindSettings;
import com.intellij.find.FindUtil;
import com.intellij.find.editorHeaderActions.NextOccurrenceAction;
import com.intellij.find.editorHeaderActions.RestorePreviousSettingsAction;
import com.intellij.find.editorHeaderActions.VariantsCompletionAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actions.IncrementalFindAction.Handler;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.actions.EmacsPlusBA;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import com.mulgasoft.emacsplus.keys.Keymaps;
import com.mulgasoft.emacsplus.util.EmacsIds;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ISearchForward extends EditorAction implements EmacsPlusBA {
  final String GEN_MSG;
  @NonNls
  private static final String REPLACE_CLASS = "com.intellij.find.editorHeaderActions.ReplaceOnEnterAction";
  @NonNls
  private static final String CLOSE_CLASS = "com.intellij.find.editorHeaderActions.CloseOnESCAction";
  @NonNls
  private static final String HISTORY_CLASS = "com.intellij.find.editorHeaderActions.ShowHistoryAction";
  @NonNls
  private static final String UP_ACTION = "IS.Up";
  @NonNls
  private static final String DOWN_ACTION = "IS.Down";
  @NonNls
  private static final String ENTER_ACTION = "IS.Enter";
  @NonNls
  private static final String REPLACE_ACTION = "IS.Replace";
  @NonNls
  private static final String INTERRUPT_ACTION = "IS.Interrupt";
  @NonNls
  private static final String UNDO_ACTION = "undoKeystroke";
  private int myStartOffset;
  private boolean myIsMulti;
  protected Editor myEditor;
  private boolean isReplace;
  private ISearchForward.theSelection oldSelection;
  private ISearch mySearcher;
  private final FindModelObserver fmo;

  String getNoActionMsg(EditorAction action) {
    return String.format(GEN_MSG, action.getTemplatePresentation().getText());
  }

  protected ISearchForward(boolean isReplace) {
    super(new Handler(isReplace));
    GEN_MSG = "Emacs+ %s behavior not supported in this version of IDEA";
    myStartOffset = 0;
    myIsMulti = false;
    myEditor = null;
    this.isReplace = false;
    oldSelection = null;
    mySearcher = null;
    fmo = findModel -> {
      boolean multi = findModel.isMultiline();
      if (multi != isMulti()) {
        myIsMulti = multi;
        if (mySearcher != null) {
          changeFieldActions(mySearcher, false);
        }
      }

    };
    EmacsPlusAction.addCommandListener(this, getName());
    this.isReplace = isReplace;
  }

  protected ISearchForward() {
    this(false);
  }

  protected String getName() {
    return EmacsIds.ISEARCH_NAME;
  }

  protected Editor getEditor() {
    return myEditor;
  }

  protected ISearch getSearcher() {
    return mySearcher;
  }

  private boolean isMulti() {
    return myIsMulti;
  }

  @Override
  public void before(CommandEvent var1) {
    myEditor = ISHandler.getTextEditor(var1.getProject());
    myStartOffset = myEditor.getCaretModel().getPrimaryCaret().getOffset();
    if (!isReplace) {
      oldSelection = new theSelection(myEditor.getSelectionModel(),
                                      myEditor instanceof EditorEx
                                      ? (EditorEx) myEditor
                                      : null);
    }

  }

  @Override
  public void after(CommandEvent var1) {
    mySearcher = ISearch.from(myEditor);
    if (mySearcher != null && mySearcher.getSearchField() != null) {
      FindModel fm = mySearcher.getFindModel();
      fm.setRegularExpressions(false);
      myIsMulti = fm.isMultiline();
      changeFieldActions(mySearcher, false);
      setSwitchAction(mySearcher);
      watchModelChanges(fm);
      if (!isReplace && !oldSelection.isEmpty()) {
        myEditor.getSelectionModel().setSelection(oldSelection.vpstart, oldSelection.start,
                                                  oldSelection.vpend,
                                                  oldSelection.end);
        if (oldSelection.isSticky) {
          ((EditorEx) myEditor).setStickySelection(true);
        }
      }
    } else {
      EmacsPlusAction.errorMessage(getNoActionMsg(this));
    }

  }

  protected void changeFieldActions(ISearch searcher, boolean isReplace) {
    JTextComponent field = isReplace ? searcher.getReplaceField() : searcher.getSearchField();
    KeyboardShortcut kbS = new KeyboardShortcut(KeyStroke.getKeyStroke(9, 512), null);
    if (!addToAction(HISTORY_CLASS, kbS, field) &&
        findAction(field, ISearchForward.InnerShowHistory.class) == null) {
      new ISearchForward.InnerShowHistory(searcher, field, kbS);
    }

    KeyStroke ksG = KeyStroke.getKeyStroke(71, 128);
    removeFromAction(CLOSE_CLASS, ksG, field);
    InputMap im = field.getInputMap();
    ActionMap am = field.getActionMap();
    am.put(INTERRUPT_ACTION, new ISearchForward.ISearchInterrupt(INTERRUPT_ACTION));
    im.put(ksG, INTERRUPT_ACTION);
    im.put(KeyStroke.getKeyStroke(27, 0), INTERRUPT_ACTION);
    am.put("IS.Down", new ISearchForward.IMoveDown("IS.Down", isReplace));
    im.put(KeyStroke.getKeyStroke(86, 128), "IS.Down");
    am.put("IS.Up", new ISearchForward.IMoveUp("IS.Up", isReplace));
    im.put(KeyStroke.getKeyStroke(86, Keymaps.getMeta()), "IS.Up");
    im.put(Keymaps.getIntlKeyStroke(47), UNDO_ACTION);
    KeyStroke ksE = KeyStroke.getKeyStroke(10, 0);
    removeFromAction(NextOccurrenceAction.class, new KeyboardShortcut(ksE, null), field);
    if (isReplace) {
      replaceSpecifics(ksE, field);
    } else {
      searchSpecifics(searcher, ksE, field);
    }

  }

  private void replaceSpecifics(KeyStroke ksE, JComponent field) {
    if (!isMulti()) {
      removeFromAction(REPLACE_CLASS, ksE, field);
      field.getActionMap().put(REPLACE_ACTION, new ISearchForward.IReplaceReturn(REPLACE_ACTION));
      field.getInputMap().put(ksE, REPLACE_ACTION);
    }

  }

  private void searchSpecifics(ISearch searcher, KeyStroke ksE, JComponent field) {
    KeyboardShortcut kbC = new KeyboardShortcut(KeyStroke.getKeyStroke(9, 128), null);
    if (field instanceof JTextComponent) {
      addToAction(VariantsCompletionAction.class, kbC, (JTextComponent) field);
    }

    if (!isMulti() && !searcher.getFindModel().isReplaceState()) {
      field.getActionMap().put(ENTER_ACTION, new ISearchForward.ISearchReturn(ENTER_ACTION));
      field.getInputMap().put(ksE, ENTER_ACTION);
      if (findAction(field, ISearchForward.InnerISearchReturn.class) == null) {
        new ISearchForward.InnerISearchReturn(searcher, field, new KeyboardShortcut(KeyStroke.getKeyStroke(10, 0),
                                                                                    null));
      }
    }

  }

  private void watchModelChanges(FindModel model) {
    model.addObserver(fmo);
  }

  protected void setSwitchAction(ISearch searcher) {
    new ISearchForward.SwitchToISearch(searcher);
    new ISearchForward.SwitchToISearchBack(searcher);
  }

  private void cleanUp() {
    if (mySearcher != null) {
      FindModel fm = mySearcher.getFindModel();
      if (fm.isRegularExpressions()) {
        fm.setRegularExpressions(false);
      }

      mySearcher = null;
    }

  }

  private void isearchReturn() {
    ISearch searcher = getSearcher();
    if (searcher != null) {
      cleanUp();
      searcher.close();
    }

    myEditor.getSelectionModel().removeSelection();
    myEditor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
  }

  private static AnAction findAction(JComponent field, Class actionClass) {
    List<AnAction> actions = (List<AnAction>) field.getClientProperty(AnAction.ACTIONS_KEY);
    for (AnAction a : actions) {
      if (actionClass.isInstance(a)) {
        return a;
      }
    }

    return null;
  }

  private static Shortcut findCut(Shortcut[] cuts, Shortcut cut) {
    Shortcut result = null;

    for (Shortcut sc : cuts) {
      if (sc.startsWith(cut)) {
        result = sc;
        break;
      }
    }

    return result;
  }

  private static boolean addToAction(Class actionClass, Shortcut cut, JTextComponent field) {
    boolean result = false;
    AnAction action = findAction(field, actionClass);
    if (action != null) {
      result = true;
      Shortcut[] cuts = action.getShortcutSet().getShortcuts();
      if (findCut(cuts, cut) == null) {
        Shortcut[] newcuts = new Shortcut[cuts.length + 1];
        newcuts[0] = cut;

        System.arraycopy(cuts, 0, newcuts, 1, cuts.length);

        action.unregisterCustomShortcutSet(field);
        action.registerCustomShortcutSet(new CustomShortcutSet(newcuts), field);
      }
    }

    return result;
  }

  private static boolean addToAction(String actionClass, Shortcut cut, JTextComponent field) {
    boolean result = false;

    try {
      Class clazz = Class.forName(actionClass);
      result = addToAction(clazz, cut, field);
    } catch (ClassNotFoundException var6) {
    }

    return result;
  }

  private static boolean removeFromAction(Class actionClass, Shortcut cut, JComponent field) {
    boolean result = false;
    AnAction action = findAction(field, actionClass);
    if (action != null) {
      Shortcut[] cuts = action.getShortcutSet().getShortcuts();
      Shortcut oldCut = findCut(cuts, cut);
      if (oldCut != null) {
        Shortcut[] newcuts = new Shortcut[cuts.length - 1];
        int diff = 0;

        for (int i = 0; i < cuts.length; ++i) {
          if (cuts[i] == oldCut) {
            diff = 1;
            result = true;
          } else {
            newcuts[i - diff] = cuts[i];
          }
        }

        action.unregisterCustomShortcutSet(field);
        action.registerCustomShortcutSet(new CustomShortcutSet(newcuts), field);
      }
    }

    return result;
  }

  private static void removeFromAction(String actionClass, KeyStroke ks, JComponent field) {
    boolean result =
        removeFromAction(actionClass, new KeyboardShortcut(ks, null), field);
    if (!result) {
      field.unregisterKeyboardAction(ks);
      result = true;
    }

  }

  private static boolean removeFromAction(String actionClass, Shortcut cut, JComponent field) {
    boolean result = false;

    try {
      Class clazz = Class.forName(actionClass);
      result = removeFromAction(clazz, cut, field);
    } catch (ClassNotFoundException var6) {
    }

    return result;
  }

  private class SwitchToISearchBack extends AnAction {
    SwitchToISearchBack(ISearch searcher) {
      ArrayList<Shortcut> shortcuts = new ArrayList();
      ContainerUtil.addAll(shortcuts,
                           ActionManager
                               .getInstance()
                               .getAction(EmacsIds.ISEARCH_BACK_ID)
                               .getShortcutSet()
                               .getShortcuts());
      registerCustomShortcutSet(new CustomShortcutSet((Shortcut[]) shortcuts.toArray(new Shortcut[shortcuts.size()])),
                                searcher.getComponent());
    }

    private void setInitialText(ISearch seacher, JTextComponent field, String itext) {
      String text = itext != null ? itext : "";
      if (text.contains("\n")) {
        seacher.getFindModel().setMultiline(true);
      }

      field.setText(text);
      seacher.getFindModel().setStringToFind(text);
      field.selectAll();
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
      ISearch searcher = getSearcher();
      if (searcher != null) {
        searcher.searchBackward();
        JTextComponent field = searcher.getSearchField();
        if (field.getText().isEmpty()) {
          String[] vals = FindSettings.getInstance().getRecentFindStrings();
          if (vals.length > 0) {
            int offset = myEditor.getCaretModel().getOffset();
            setInitialText(searcher, field, vals[vals.length - 1]);
            int adj = offset;
            if (offset < myEditor.getDocument().getTextLength()) {
              adj = offset + 1;
            }

            try {
              myEditor.getCaretModel().moveToOffset(adj);
              FindUtil.searchBack(e.getProject(), myEditor, null);
            } finally {
              if (myEditor.getCaretModel().getOffset() == adj) {
                myEditor.getCaretModel().moveToOffset(offset);
              }

            }
          }
        }
      }

    }
  }

  private class SwitchToISearch extends AnAction {
    SwitchToISearch(ISearch searcher) {
      registerCustomShortcutSet(ISearchForward.this.getShortcutSet(), searcher.getComponent());
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
      JTextComponent field = getSearcher().getSearchField();
      AnAction action;
      if (field != null && field.getText().isEmpty()) {
        action = findAction(field, RestorePreviousSettingsAction.class);
        if (action != null) {
          action.update(e);
          action.actionPerformed(e);
        } else {
          FindModel model = FindManager.getInstance(e.getProject()).getPreviousFindModel();
          if (model != null) {
            getSearcher().getFindModel().copyFrom(model);
          }
        }
      }

      action = ActionManager.getInstance().getAction("FindNext");
      action.update(e);
      action.actionPerformed(e);
    }
  }

  private class IMoveUp extends TextAction {
    private boolean isReplace = false;

    private IMoveUp(String name, boolean isReplace) {
      super(name);
      this.isReplace = isReplace;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      ISearch searcher = getSearcher();
      if (searcher != null) {
        JTextComponent field = isReplace ? searcher.getReplaceField() : searcher.getSearchField();
        field.setCaretPosition(0);
      }

    }
  }

  private class IMoveDown extends TextAction {
    private boolean isReplace = false;

    private IMoveDown(String name, boolean isReplace) {
      super(name);
      this.isReplace = isReplace;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      ISearch searcher = getSearcher();
      if (searcher != null) {
        JTextComponent field = isReplace ? searcher.getReplaceField() : searcher.getSearchField();
        field.setCaretPosition(field.getText().length());
      }

    }
  }

  private class IReplaceReturn extends TextAction {
    private boolean once = true;

    private IReplaceReturn(String name) {
      super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      ISearch searcher = getSearcher();
      if (searcher != null) {
        List<CaretState> state = makeState(searcher);
        searcher.replaceCurrent();
        if (state != null && !searcher.hasMatches()) {
          myEditor.getCaretModel().setCaretsAndSelections(state);
        }
      }

    }

    private List<CaretState> makeState(ISearch searcher) {
      List<CaretState> result = null;
      if (once) {
        once = false;
        SelectionModel sm = myEditor.getSelectionModel();
        if (sm.hasSelection() && !searcher.getFindModel().isGlobal()) {
          int off = myEditor.getCaretModel().getOffset();
          int[] starts = sm.getBlockSelectionStarts();
          int[] ends = sm.getBlockSelectionEnds();

          for (int i = 0; i < starts.length; ++i) {
            if (starts[i] != ends[i]) {
              if (result == null) {
                result = new ArrayList();
              }

              result.add(new CaretState(myEditor.offsetToLogicalPosition(off),
                                        myEditor.offsetToLogicalPosition(starts[i]),
                                        myEditor.offsetToLogicalPosition(ends[i])));
            }
          }
        }
      }

      return result;
    }
  }

  private class ISearchReturn extends TextAction {
    private ISearchReturn(String name) {
      super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      isearchReturn();
    }
  }

  private class InnerISearchReturn extends AnAction implements DumbAware {
    JComponent field = null;

    protected InnerISearchReturn(ISearch searcher, JComponent field, KeyboardShortcut shortcut) {
      this.field = field;
      registerCustomShortcutSet(new CustomShortcutSet(shortcut), field);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
      isearchReturn();
    }

    @Override
    public void update(AnActionEvent e) {
      ISearch searcher = getSearcher();
      e
          .getPresentation()
          .setEnabled(searcher != null &&
                      searcher.hasMatches() &&
                      !isMulti() &&
                      !StringUtil.isEmpty(searcher.getSearchField().getText()));
    }
  }

  private class InnerShowHistory extends AnAction implements DumbAware {
    JTextComponent field = null;

    protected InnerShowHistory(ISearch searcher, JTextComponent field, KeyboardShortcut shortcut) {
      this.field = field;
      registerCustomShortcutSet(new CustomShortcutSet(shortcut), field);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
      getSearcher().showHistory(false, field);
    }

    @Override
    public void update(AnActionEvent e) {
      e.getPresentation().setEnabled(getSearcher() != null);
    }
  }

  private class ISearchInterrupt extends TextAction {
    private ISearchInterrupt(String name) {
      super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      boolean hasMatches = true;
      if (mySearcher != null) {
        hasMatches = mySearcher.hasMatches();
        mySearcher.close();
        cleanUp();
      }

      if (hasMatches) {
        myEditor.getCaretModel().moveToOffset(myStartOffset);
        myEditor.getSelectionModel().removeSelection();
      }

      myEditor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
    }
  }

  private static class theSelection {
    VisualPosition vpstart;
    VisualPosition vpend;
    int start;
    int end;
    boolean isSticky = false;

    theSelection(SelectionModel sm, EditorEx editor) {
      if (sm.hasSelection()) {
        vpstart = sm.getSelectionStartPosition();
        vpend = sm.getSelectionEndPosition();
        start = sm.getSelectionStart();
        end = sm.getSelectionEnd();
        if (editor != null) {
          isSticky = editor.isStickySelection();
          editor.setStickySelection(false);
        } else {
          sm.removeSelection(true);
        }
      }

    }

    boolean isEmpty() {
      return vpstart == null;
    }
  }
}
