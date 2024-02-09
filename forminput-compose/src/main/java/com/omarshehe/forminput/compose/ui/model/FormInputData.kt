package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

interface FormInputData {
    val id: String
    val labelValue: Any
    val placeholderValue: Any
    val type: FormInputType
    val isMandatory: Boolean
    val hasError: Boolean
    val icon: ImageVector?
}

@Stable
data class FormInputDropDownState(
    var value: DropDownOptionModel = DropDownOptionModel(),
    val options: List<DropDownOptionModel> = emptyList(),
    override val id: String,
    override val labelValue: Any,
    override val placeholderValue: Any,
    override val type: FormInputType,
    override val isMandatory: Boolean = false,
    override val hasError: Boolean = false,
    override val icon: ImageVector? = null
) : FormInputData {
    val textValue get() = value.textValue
}

@Stable
data class FormInputTextFieldState(
    val value: String,
    override val id: String,
    override val labelValue: Any,
    override val placeholderValue: Any,
    override val type: FormInputType,
    override val isMandatory: Boolean = false,
    override val hasError: Boolean = false,
    override val icon: ImageVector? = null,
    val showMaxChar: Boolean = false,
    val maxChar: Int = Int.MAX_VALUE
) : FormInputData
