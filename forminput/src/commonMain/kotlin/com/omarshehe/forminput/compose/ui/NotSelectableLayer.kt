package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.runtime.Composable

/**
 * Wraps a popup or dialog so its content cannot be selected.
 *
 * A popup or dialog is drawn in its own layout hierarchy but inherits the `SelectionContainer` it is composed in.
 * Its text then registers with that container, and pressing it starts a selection across two hierarchies, which
 * throws "layouts are not part of the same hierarchy".
 */
@Composable
internal fun NotSelectableLayer(content: @Composable () -> Unit) {
    DisableSelection(content = content)
}
