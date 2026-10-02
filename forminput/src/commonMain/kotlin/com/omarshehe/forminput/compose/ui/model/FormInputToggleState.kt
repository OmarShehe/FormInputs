package com.omarshehe.forminput.compose.ui.model

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

public data class FormInputToggleState(
    override val id: String,
    override val labelRes: StringResource? = null,
    override val placeholderRes: StringResource? = null,
    override val type: FormInputType = FormInputType.TEXT, // Using TEXT as default for now
    override val isMandatory: Boolean = false,
    override val hasError: Boolean = false,
    override val errorRes: StringResource? = null,
    override val icon: ImageVector? = null,
    val value: Boolean = false,
) : FormInputState
