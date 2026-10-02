package com.omarshehe.forminput.compose.ui.model

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

public interface FormInputState {
    public val id: String
    public val labelRes: StringResource?
    public val placeholderRes: StringResource?
    public val type: FormInputType
    public val isMandatory: Boolean
    public val hasError: Boolean
    public val errorRes: StringResource?
    public val icon: ImageVector?
    public val isVisible: Boolean get() = true

    /** Runtime text that wins over [labelRes] / [placeholderRes] / [errorRes] when set. Supported by the text field and the dropdown. */
    public val label: String? get() = null
    public val placeholder: String? get() = null
    public val error: String? get() = null
    public val validation: String? get() = null

    // Exposed at the base interface (not just FormInputTextFieldState) so a generic tab layout can
    // detect "this field renders an extra header-action row" regardless of field type, and reserve
    // matching space on a row-sibling that lacks one — see DocumentsTabContent.
    public val onHeaderActionClick: (() -> Unit)? get() = null
    public val headerActionLabelRes: StringResource? get() = null
    public val headerActionIcon: ImageVector? get() = null
}
