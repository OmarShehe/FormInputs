package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

/** State of [com.omarshehe.forminput.compose.ui.FormInputPasswordField]. What the field checks is set on the composable, not here. */
@Stable
public data class FormInputPasswordState(
    override val id: String,
    val value: String = "",
    override val labelRes: StringResource? = null,
    override val placeholderRes: StringResource? = null,
    override val isMandatory: Boolean = false,
    override val hasError: Boolean = false,
    override val errorRes: StringResource? = null,
    override val icon: ImageVector? = null,
    override val isVisible: Boolean = true,
    override val label: String? = null,
    override val placeholder: String? = null,
    override val error: String? = null,
) : FormInputState {
    override val type: FormInputType get() = FormInputType.PASSWORD
}
