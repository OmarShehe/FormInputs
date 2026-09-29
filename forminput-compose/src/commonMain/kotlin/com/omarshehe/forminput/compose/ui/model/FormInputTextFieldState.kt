package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

@Stable
data class FormInputTextFieldState(
    override val id: String,
    override val labelRes: StringResource? = null,
    override val placeholderRes: StringResource? = null,
    override val type: FormInputType,
    override val isMandatory: Boolean = false,
    override val hasError: Boolean = false,
    override val errorRes: StringResource? = null,
    override val icon: ImageVector? = null,
    override val isVisible: Boolean = true,
    override val validation: String? = null,
    val showMaxChar: Boolean = false,
    val isSingleLine: Boolean = true,
    val minLines: Int = 1,
    val maxLines: Int = 1,
    val value: String,
    val maxChar: Int = Int.MAX_VALUE,
    val autoCapitalize: Boolean = false,
    val subtitleRes: StringResource? = null,
    override val onHeaderActionClick: (() -> Unit)? = null,
    override val headerActionLabelRes: StringResource? = null,
    override val headerActionIcon: ImageVector? = null,
    val prefixRes: StringResource? = null,
    val suffixRes: StringResource? = null,
    override val label: String? = null,
    override val placeholder: String? = null,
    override val error: String? = null,
) : FormInputState
