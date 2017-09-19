# emacs-plus

Some patches to make the IntelliJ Emacs+ plugin work correctly with recent versions of IntelliJ.

This is a repackaged version of the original Mulgasoft [Emacs+](http://www.mulgasoft.com/intellemacsplus) plugin for 
IntelliJ. Unfortunately the project was never open sourced and was effectively abandoned in a state which made the
plugin unusable on recent IntelliJ versions. I've repackaged the original jar and have made some minimal fixes to keep
the plugin working. I made several attempts to contact the original author offering to take over maintenance of the
plugin but never received a reply. I'm releasing this under the 
[original licence](http://www.mulgasoft.com/intellemacsplus/mpl-v-1-0) which seems to permit this.

I don't actually use this myself but many of my users do. So if something doesn't work, please file 
[an issue](https://github.com/cursive-ide/emacs-plus/issues) and I'll do what I can.

Here are the changes from the original website, preserved for posterity.

### Search and Replace 

- Enhance `C-s` and `C-r` to support these additional sub-commands on the search string.  When using the replace versions 
  (`M-%` and `C-M-%`), `ENTER` is a no-op in the search field, and executes a single replace when entered in the replace 
  field (unless in multiline mode):
    * `M-p` - Search for the previous item in the search history
    * `M-n` - Search for the next item in the search history  
    NB: IntelliJ IDEA uses the same history ring for normal and regexp searches.
    * `C-w` - Yank the next word from the buffer onto end of the search string and search for result
    * `C-y` - Yank the rest of line onto the end of the search string and search for result
    * `M-y` - Yank the last killed text onto end of the search string and search for result
    * `C-M-y` - Yank one character from current position and search for result  
    NB: When using the single line search buffer (and regexp is not enabled), the yank commands will not yank past the 
    end of a line.
    * `C-M-w` - Delete one character from the end of the search string and search for result
    * `ENTER` - Exit, leaving the caret at the most recent found text
    * `C-g` - While searching or when search has failed cancels the input back to what has been found successfully.
    * `C-g` - When search is successful aborts and moves back to the starting point.
    * `M-c` - Toggle case sensitivity
    * `M-m` - Toggle multi-line search
    * `M-w` - Toggle word search
    * `M-r` - Toggle regexp search

- Regexp I-Search forward (`C-M-s`) - Incremental search forward for a regular expression.
- Regexp I-Search backward (`C-M-r`) - Incremental search backward for a regular expression.  
NB: These commands just enter the search with the Regexp option already selected

### Kill Ring Behavior 

Extends the default Kill Ring behavior in IntelliJ IDEA so that the following bindings act as expected:

- Yank (`C-y`):  Insert the last stretch of killed text
- Yank-Pop (`M-y`) or (`ESC-y`): Replace a just-yanked stretch of killed text with a different stretch
- Append Next Kill (`C-M-w`): If the following command is a kill command, append its text to the last killed text 
  instead of starting a new entry on the kill ring

NB: When Kill Ring entry has been constructed with multiple carets, the yank operation will convert it to a single 
string before inserting the text into the buffer.  This may change in the future to support multiple text for multiple 
carets on yank.

### Comment behavior
- Comment Dwim (`M-;`):  Call the comment command you want (Do what I mean)
- Comment Next Line (`M-n`): Go to/Insert comment on the following line. 
- Comment Previous Line (`M-p`): Go to/Insert comment on the previous line.
- Indent New Comment Line (`C-M-j`): Break line at point and indent, continuing comment.
- Comment Kill (`C-u M-;`): Kill comment on the current line
- IntelliJ's Comment By Line Comment (`C-c C-c`)

NB: The comment commands rely on a file's PSI implementation.

### Movement behavior 

- Forward Word (`M-f`):  Move forward one word (providing the expected Emacs behavior).
- Backward Word (`M-b`): Move backward one word (providing the expected Emacs behavior).

### Transposition behavior

- Transpose Characters (`C-t`):  Interchange characters around point
    * When point is at the end of a line it exchanges the previous two characters.
- Transpose Words (`M-t`): Interchange words around point, leaving point at end of them.
- Transpose Lines (`C-x C-t`): Exchange current line and previous line, leaving point after both.

### Space handling

- Delete Blank Lines (`C-x C-o`): 
    * On blank line, delete all surrounding blank lines, leaving just one
    * On isolated blank line, delete that one
    * On non-blank line, delete any immediately following blank
- Delete Horizontal Space (`M-\`): Delete all tabs and spaces from around the cursor
- Just One Space (`M-SPACE`): Delete all spaces and tabs around the cursor, leaving one space

### Indentation handling
- Back to Indentation (`M-m`):  Position point at the first nonblank character on the line
- Delete Indentation (`M-^`):  Join two lines cleanly

### Recenter 

- Recenter Top Bottom (`C-l`): Provide a recenter to move current line to window center, top, and bottom, successively.
    * The first call redraws the frame and centers point vertically within the window.  Successive calls scroll the 
      window, placing point on the top, bottom,  and middle consecutively. The cycling order is middle -> top -> bottom.

### Case conversion commands

- Capitalize (`M-c`): Capitalize the following word
- Lowercase (`M-l`): Lower case the selected region or the following word
- Uppercase (`M-u`): Upper case the selected region or the following word

### Information commands 

- What Cursor Position (`C-x =`):  Print information on cursor position
- What Line (unbound):  Print the current buffer line number

### VCS commands

- Diff Keymap Group (`C-x C-e`):  Popup menu of applicable vcs diff commands
- VCS Popup (`C-x v`):  Popup menu of applicable vcs commands

### Window navigation
 
- Switch To Buffer (`C-x b`): Use Recent Files switcher panel (that supports searching) and enhance to support navigation keys (when not searching).
- delete-other-windows (`C-x 1`): If in split window(s), unsplit all, else (toggle) maximize within frame
    * if in a tool window, (toggle) maximize it (and `C-x 0 will hide it).

### Miscellaneous changes - to more closely emulate Emacs:

- Emacs Tab (`M-i`):
- Extend Selection (`C-M-u`):
- Find in Path... (`M-s`):
- Goto Line (`M-g g`, `M-g M-g`)
- Kill to Word End (`C-M-k`): Temporary binding for kill-sexp
- Move Caret to Next Word (`C-M-f`): Temporary binding for forward-sexp
- Move Caret to Previous Word (`C-M-b`): Temporary binding for backward-sexp
- Next Error (`M-g n`, `M-g M-n`): Navigate to next highlighted error
- Open Declaration (`M-.`):
- Open Implementation (`C-u M-.`):
- Reload from Disk (`C-x C-v`):
- Start Newline (`C-m`):

### GNU-style GUD bindings for debugging:

- toggle-breakpoint (`C-x <SPACE>`): Toggle a breakpoint on the source line that point is on.
- step-into (`C-c C-s`): Execute a single line of code.  If the line contains a function call, execution stops after entering the called function.
- step-over (`C-c C-n`): Execute a single line of code, stepping across entire function calls
- resume (`C-c C-r`): Continue execution without specifying any stopping point.
- run-to-line (`C-c C-u`): Continue execution to the line point is on
- temporary-breakpoint (`C-c C-t`): Toggle temporary line breakpoint at caret.
- evaluate-expression (`C-c C-p`): Evaluate expression.
