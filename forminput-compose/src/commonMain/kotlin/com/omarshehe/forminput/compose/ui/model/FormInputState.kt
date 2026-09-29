package com.omarshehe.forminput.compose.ui.model

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

interface FormInputState {
    val id: String
    val labelRes: StringResource?
    val placeholderRes: StringResource?
    val type: FormInputType
    val isMandatory: Boolean
    val hasError: Boolean
    val errorRes: StringResource?
    val icon: ImageVector?
    val isVisible: Boolean get() = true

    /** Runtime text that wins over [labelRes] / [placeholderRes] / [errorRes] when set. Supported by the text field and the dropdown. */
    val label: String? get() = null
    val placeholder: String? get() = null
    val error: String? get() = null
    val validation: String? get() = null

    // Exposed at the base interface (not just FormInputTextFieldState) so a generic tab layout can
    // detect "this field renders an extra header-action row" regardless of field type, and reserve
    // matching space on a row-sibling that lacks one — see DocumentsTabContent.
    val onHeaderActionClick: (() -> Unit)? get() = null
    val headerActionLabelRes: StringResource? get() = null
    val headerActionIcon: ImageVector? get() = null
}
