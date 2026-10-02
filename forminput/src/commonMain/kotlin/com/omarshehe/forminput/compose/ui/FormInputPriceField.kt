package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.select_currency
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.model.FormInputPriceState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.ThousandsSeparatorTransformation
import com.omarshehe.forminput.compose.ui.utils.sanitizeAmount
import com.omarshehe.forminput.compose.ui.utils.withCleanedText
import org.jetbrains.compose.resources.stringResource

/** Which side of the amount the currency selector sits on. */
public enum class CurrencyPlacement { START, END }

/**
 * An amount with a currency picked from [FormInputPriceState.currencies]. It is a [FormInputTextField] underneath, so
 * [style], [shape], [colors], [enabled] and the text options behave the same way.
 *
 * The amount keeps digits and one dot, at most [FormInputPriceState.maxIntegerDigits] whole digits and
 * [FormInputPriceState.maxDecimals] decimals. [groupThousands] only changes how it is shown. With one currency the selector is
 * plain text. [currencyLabel] and [currencyContent] change how a currency is drawn, [menuShape] and [menuContainerColor] style
 * the list.
 */
@Composable
public fun FormInputPriceField(
    state: FormInputPriceState,
    onValueChange: (FormInputPriceState) -> Unit,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    colors: TextFieldColors? = null,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    contentPadding: PaddingValues? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    currencyPlacement: CurrencyPlacement = CurrencyPlacement.START,
    groupThousands: Boolean = false,
    currencyLabel: (String) -> String = { it },
    currencyTextStyle: TextStyle? = null,
    menuShape: Shape? = null,
    menuContainerColor: Color? = null,
    currencyContent: (@Composable (currency: String) -> Unit)? = null,
) {
    var textValue by remember { mutableStateOf(TextFieldValue(text = state.amount)) }
    LaunchedEffect(state.amount) {
        if (textValue.text != state.amount) {
            textValue = textValue.copy(text = state.amount, selection = TextRange(state.amount.length))
        }
    }
    var menuOpen by remember { mutableStateOf(false) }
    val canPick = enabled && !readOnly && state.currencies.size > 1

    val selector: @Composable () -> Unit = {
        // The outer padding keeps the selector off the field's edge; the padding inside the clickable makes the whole pill
        // (not just the text) the tap and ripple area.
        Box(
            modifier = Modifier.padding(
                start = if (currencyPlacement == CurrencyPlacement.START) Dimens.oneGrid else Dimens.default,
                end = if (currencyPlacement == CurrencyPlacement.END) Dimens.oneGrid else Dimens.default,
            ),
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.oneGrid))
                    .clickable(enabled = canPick, role = Role.DropdownList) { menuOpen = true }
                    .defaultMinSize(minHeight = Dimens.fiveGrid)
                    .padding(horizontal = Dimens.oneAndHalfGrid),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (currencyContent != null) {
                    currencyContent(state.currency)
                } else {
                    Text(
                        text = currencyLabel(state.currency),
                        style = currencyTextStyle ?: MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                }
                if (state.currencies.size > 1) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = formInputString(FormInputStrings::selectCurrency, Res.string.select_currency),
                        modifier = Modifier.size(Dimens.threeGrid),
                    )
                }
            }
            DropdownMenu(
                expanded = menuOpen && canPick,
                onDismissRequest = { menuOpen = false },
                shape = menuShape ?: MenuDefaults.shape,
                containerColor = menuContainerColor ?: MenuDefaults.containerColor,
            ) {
                state.currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currencyLabel(currency)) },
                        onClick = {
                            menuOpen = false
                            if (currency != state.currency) onValueChange(state.copy(currency = currency))
                        },
                    )
                }
            }
        }
    }

    val fieldState = FormInputTextFieldState(
        id = state.id,
        labelRes = state.labelRes,
        placeholderRes = state.placeholderRes,
        type = FormInputType.NUMBER,
        isMandatory = state.isMandatory,
        hasError = state.hasError,
        errorRes = state.errorRes,
        icon = state.icon,
        isVisible = state.isVisible,
        value = state.amount,
        label = state.label,
        placeholder = state.placeholder,
        error = state.error,
    )

    FormInputTextField(
        state = fieldState,
        value = textValue,
        onValueChange = { typed ->
            val amount = sanitizeAmount(typed.text, state.maxIntegerDigits, state.maxDecimals)
            if (amount != null) {
                textValue = if (amount == typed.text) typed else typed.withCleanedText(amount)
                if (amount != state.amount) onValueChange(state.copy(amount = amount))
            }
        },
        modifier = modifier,
        textModifier = textModifier,
        colors = colors,
        shape = shape,
        contentPadding = contentPadding,
        style = style,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (state.maxDecimals > 0) KeyboardType.Decimal else KeyboardType.Number,
        ),
        keyboardActions = keyboardActions,
        visualTransformation = if (groupThousands) ThousandsSeparatorTransformation() else null,
        leadingIcon = if (currencyPlacement == CurrencyPlacement.START) selector else null,
        trailingIcon = if (currencyPlacement == CurrencyPlacement.END) selector else null,
    )
}
