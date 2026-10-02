package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

@Stable
public data class FormInputDropDownState(
    override val id: String,
    override val labelRes: StringResource? = null,
    override val placeholderRes: StringResource? = null,
    override val type: FormInputType = FormInputType.DROP_DOWN,
    override val isMandatory: Boolean = false,
    override val hasError: Boolean = false,
    override val errorRes: StringResource? = null,
    override val icon: ImageVector? = null,
    override val isVisible: Boolean = true,
    val value: DropDownOptionModel = DropDownOptionModel(),
    val options: List<DropDownOptionModel> = emptyList(),
    val isSearchEnable: Boolean = false,
    /** When true, the field stays freely editable and every keystroke is reported (as a
     * [DropDownOptionModel] carrying the raw typed text) instead of only on tapping a suggestion —
     * for "type anything, optionally pick a known suggestion" fields. Implies editable text entry
     * regardless of [isSearchEnable]. */
    val allowFreeText: Boolean = false,
    override val label: String? = null,
    override val placeholder: String? = null,
    override val error: String? = null,
) : FormInputState {
    val valueText: String get() = value.text
    val valueTextRes: StringResource? get() = value.textRes
}

public data class DropDownOptionModel(
    val id: String = "",
    val text: String = "",
    val textRes: StringResource? = null,
)
