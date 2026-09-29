package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

@Stable
data class FormInputDateTimePickerState(
    override val id: String,
    override val labelRes: StringResource?,
    override val placeholderRes: StringResource?,
    override val type: FormInputType,
    override val isMandatory: Boolean = false,
    override val hasError: Boolean = false,
    override val errorRes: StringResource? = null,
    override val icon: ImageVector? = null,
    override val isVisible: Boolean = true,
    val value: String = "",
    val isManualEditable: Boolean = false,
    val minDateMillis: Long? = null,
    val maxDateMillis: Long? = null,
) : FormInputState
