package com.mulgasoft.emacsplus.handlers;

import com.intellij.codeInsight.actions.MultiCaretCodeInsightActionHandler;
import com.intellij.ide.DataManager;
import com.intellij.lang.Commenter;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageCommenters;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.mulgasoft.emacsplus.actions.ReversibleMultiCaretInsightAction;
import com.mulgasoft.emacsplus.util.ActionUtil;
import com.mulgasoft.emacsplus.util.EditorUtil;
import com.mulgasoft.emacsplus.util.EmacsIds;
import org.jetbrains.annotations.NotNull;

public class CommentHandler extends MultiCaretCodeInsightActionHandler {
  private static final Integer ourCommentColumn = 32;
  private String myLangId = null;
  private String myLineC = null;
  private String myBlockStart = null;
  private String myBlockEnd = null;
  private ReversibleMultiCaretInsightAction myAction = null;

  public void preInvoke(ReversibleMultiCaretInsightAction action) {
    myAction = action;
  }

  @Override
  public void postInvoke() {
    myAction = null;
  }

  @Override
  public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull Caret caret, @NotNull PsiFile file) {
    if (checkLanguage(project, caret, file)) {
      invokeAction(editor, caret, myAction.getDataContext(), file);
    }

  }

  protected void invokeAction(Editor editor, Caret caret, DataContext dataContext, PsiFile file) {
    commentLine(editor, caret, dataContext);
  }

  protected static int getCommentColumn() {
    return ourCommentColumn;
  }

  protected String getLineComment() {
    return myLineC;
  }

  protected String getBlockStart() {
    return myBlockStart;
  }

  protected boolean hasBlockComments() {
    return myBlockStart != null && myBlockEnd != null;
  }

  protected String getEmptyBlockComment() {
    return ' ' + myBlockStart + ' ' + ' ' + myBlockEnd;
  }

  protected String getEmptyLineComment() {
    return ' ' + myLineC + ' ';
  }

  protected static int getLineStartOffset(Document document, int offset) {
    return document.getLineStartOffset(document.getLineNumber(offset));
  }

  protected static int getLineEndOffset(Document document, int offset) {
    return document.getLineEndOffset(document.getLineNumber(offset));
  }

  protected boolean checkLanguage(Project project, Caret caret, PsiFile file) {
    boolean result = false;
    Commenter commenter = null;
    FileType fileType = file.getFileType();
    if (fileType instanceof AbstractFileType) {
      commenter = ((AbstractFileType) fileType).getCommenter();
      result = checkFill(String.valueOf(commenter), commenter);
    } else {
      Language language = file.getLanguage();
      if (language != null) {
        result = checkFill(language.getID(), LanguageCommenters.INSTANCE.forLanguage(language));
      }
    }

    return result;
  }

  private boolean checkFill(String id, Commenter commenter) {
    boolean result = true;
    if ((myLangId == null || !myLangId.equals(id)) && (result = commenter != null)) {
      myLangId = id;
      myLineC = commenter.getLineCommentPrefix();
      myBlockStart = commenter.getBlockCommentPrefix();
      myBlockEnd = commenter.getBlockCommentSuffix();
    }

    return result;
  }

  protected CommentHandler.CommentRange findCommentRange(Editor editor, Caret caret, DataContext dataContext) {
    return findCommentRange(EditorUtil.getPsiFile(editor, caret), editor, caret, dataContext);
  }

  protected CommentHandler.CommentRange findCommentRange(PsiFile psi,
                                                         Editor editor,
                                                         Caret caret,
                                                         DataContext dataContext) {
    CommentHandler.CommentRange result = null;
    Document document = editor.getDocument();
    int current = caret.getOffset();
    int line = document.getLineNumber(current);
    int bol = document.getLineStartOffset(line);
    int eol = document.getLineEndOffset(line);
    String text = document.getText(new TextRange(bol, eol));
    result = findCommentRange(psi, bol, text, myLineC);
    if (result == null) {
      result = findCommentRange(psi, bol, text, myBlockStart);
    }

    return result;
  }

  protected CommentHandler.CommentRange findCommentRange(PsiFile psi, int filePos, String text, String prefix) {
    CommentHandler.CommentRange crange = null;
    int index = 0;
    if (prefix != null && psi != null) {
      PsiElement ele;
      while (index >= 0) {
        index = text.indexOf(prefix, index);
        if (index >= 0) {
          ele = psi.findElementAt(filePos + index);
          if (ele instanceof PsiComment || (ele = ele.getParent()) instanceof PsiComment) {
            crange = commentRange(ele, prefix);
            break;
          }

          ++index;
        }
      }

      if (crange == null) {
        ele = inComment(psi, filePos);
        if (ele != null && ele.getText().startsWith(prefix)) {
          crange = commentRange(ele, prefix);
        }
      }
    }

    return crange;
  }

  private CommentHandler.CommentRange commentRange(PsiElement ele, String prefix) {
    TextRange range = ele.getTextRange();
    if (range == null) {
      int off = ele.getTextOffset();
      range = new TextRange(off, off + prefix.length());
    }

    return new CommentRange(range, prefix);
  }

  protected static PsiElement inComment(Editor editor, Caret caret) {
    return inComment(EditorUtil.getPsiFile(editor, caret), caret.getOffset());
  }

  protected static PsiElement inComment(PsiFile psi, int offset) {
    PsiElement result = null;
    PsiElement ele = psi.findElementAt(offset);
    if (ele != null && (ele instanceof PsiComment || (ele = ele.getParent()) instanceof PsiComment)) {
      result = ele;
    }

    return result;
  }

  protected void commentLine(Editor editor, Caret caret, DataContext dataContext) {
    boolean tabit = false;
    Document document = editor.getDocument();
    int line = document.getLineNumber(caret.getOffset());
    int bol = document.getLineStartOffset(line);
    int eol = document.getLineEndOffset(line);
    PsiFile psi = EditorUtil.getPsiFile(editor, caret);
    CommentHandler.CommentRange range = findCommentRange(psi, editor, caret, dataContext);
    if (range != null) {
      int index = range.getStartOffset() + Math.min(range.getPrefix().length(), range.getLength());
      if (index != eol && index + 1 < document.getTextLength()) {
        char next = document.getText(new TextRange(index, index + 1)).charAt(0);
        if (next == ' ' || next == '\t') {
          ++index;
        }
      }

      caret.moveToOffset(index);
    } else {
      tabit = addComment(editor, caret, bol, eol);
    }

    if (tabit) {
      ActionUtil
          .dispatchLater(EmacsIds.EMACS_STYLE_INDENT_ID, DataManager.getInstance().getDataContext(editor.getComponent()));
    }

    EditorModificationUtil.scrollToCaret(editor);
  }

  private boolean addComment(Editor editor, Caret caret, int bol, int eol) {
    Document document = editor.getDocument();
    boolean tabit = false;
    String comment = null;
    int length = 0;
    if (myLineC != null) {
      comment = getEmptyLineComment();
      length = comment.length();
    } else if (myBlockStart != null) {
      comment = getEmptyBlockComment();
      length = myBlockStart.length() + 2;
    }

    if (comment != null) {
      String text = document.getText(new TextRange(bol, eol));

      int end;
      for (end = text.length() - 1; end >= 0 && text.charAt(end) <= ' '; --end) {
      }

      ++end;
      if (end == 0) {
        document.replaceString(bol, eol, comment);
        caret.moveToOffset(bol + length);
        tabit = true;
      } else {
        int cc = getCommentColumn() - 1;
        int newoff = bol + end;
        if (end < cc) {
          caret.moveToOffset(newoff);
          String fill = EditorModificationUtil.calcStringToFillVirtualSpace(editor, cc - end);
          document.insertString(newoff, fill + comment);
          caret.moveToOffset(newoff + fill.length() + length);
        } else {
          document.replaceString(newoff, eol, comment);
          caret.moveToOffset(newoff + length);
        }
      }
    }

    return tabit;
  }

  public static class CommentRange {
    final TextRange range;
    final String prefix;

    private CommentRange(TextRange range, String prefix) {
      this.range = range != null ? range : new TextRange(0, 0);
      this.prefix = prefix;
    }

    public TextRange getRange() {
      return range;
    }

    public String getPrefix() {
      return prefix;
    }

    public int getStartOffset() {
      return range.getStartOffset();
    }

    public int getEndOffset() {
      return range.getEndOffset();
    }

    public int getLength() {
      return range.getLength();
    }
  }
}
