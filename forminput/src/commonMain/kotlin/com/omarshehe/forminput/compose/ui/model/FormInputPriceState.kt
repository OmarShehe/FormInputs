package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

/**
 * State of [com.omarshehe.forminput.compose.ui.FormInputPriceField]: an amount plus the currency picked for it.
 * [amount] is the plain typed number (digits and at most one dot, no grouping), so it can be parsed as it is.
 */
@Stable
data class FormInputPriceState(
    override val id: String,
    val amount: String = "",
    val currency: String,
    val currencies: List<String> = listOf(currency),
    val maxDecimals: Int = 2,
    val maxIntegerDigits: Int = 12,
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
    override val type: FormInputType get() = FormInputType.NUMBER
}
