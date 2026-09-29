package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.cd_rule_met
import com.omarshehe.forminput.compose.resources.cd_rule_not_met
import com.omarshehe.forminput.compose.resources.hide_password
import com.omarshehe.forminput.compose.resources.password_mismatch
import com.omarshehe.forminput.compose.resources.password_rule_digit
import com.omarshehe.forminput.compose.resources.password_rule_min_length
import com.omarshehe.forminput.compose.resources.password_rule_special
import com.omarshehe.forminput.compose.resources.password_rule_upper_case
import com.omarshehe.forminput.compose.resources.password_rules_title
import com.omarshehe.forminput.compose.resources.password_strength_medium
import com.omarshehe.forminput.compose.resources.password_strength_strong
import com.omarshehe.forminput.compose.resources.password_strength_very_strong
import com.omarshehe.forminput.compose.resources.password_strength_weak
import com.omarshehe.forminput.compose.resources.show_password
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.model.FormInputPasswordState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.PasswordRule
import com.omarshehe.forminput.compose.ui.utils.PasswordRules
import com.omarshehe.forminput.compose.ui.utils.PasswordStrength
import com.omarshehe.forminput.compose.ui.utils.passwordStrength
import org.jetbrains.compose.resources.stringResource

/** When the requirement checklist under the field is shown. */
enum class PasswordRulesVisibility { ALWAYS, WHEN_FOCUSED, WHEN_NOT_EMPTY, NEVER }

/** The text shown for each strength; null falls back to the library's (translated) words. */
data class PasswordStrengthLabels(
    val weak: String? = null,
    val medium: String? = null,
    val strong: String? = null,
    val veryStrong: String? = null,
)

/** The colour used for each strength, and for a met or unmet requirement; null unmet uses the theme's error colour. */
data class PasswordColors(
    val weak: Color? = null,
    val medium: Color = Color(0xFFF9A825),
    val strong: Color = Color(0xFF7CB342),
    val veryStrong: Color = Color(0xFF2E7D32),
    val ruleMet: Color = Color(0xFF2E7D32),
    val ruleUnmet: Color? = null,
)

/** The library's default checklist: an upper case letter, a special character, a number and a minimum length. */
@Composable
fun defaultPasswordRules(minLength: Int = 8): List<PasswordRule> {
    val minLengthText = LocalFormInputDefaults.current.strings.passwordRuleMinLength?.invoke(minLength)
        ?: stringResource(Res.string.password_rule_min_length, minLength)
    return listOf(
        PasswordRules.upperCase(formInputString(FormInputStrings::passwordRuleUpperCase, Res.string.password_rule_upper_case)),
        PasswordRules.special(formInputString(FormInputStrings::passwordRuleSpecial, Res.string.password_rule_special)),
        PasswordRules.digit(formInputString(FormInputStrings::passwordRuleDigit, Res.string.password_rule_digit)),
        PasswordRules.minLength(minLength, minLengthText),
    )
}

/**
 * A password field with a show/hide toggle, a requirement checklist, a strength indicator and an optional match check.
 * It is a [FormInputTextField] underneath, so [style], [shape], [colors], [enabled], [textStyle] and [contentPadding] apply.
 *
 * - [rules]: the checklist and the input to the strength. Null uses [defaultPasswordRules]; an empty list has none.
 *   [rulesVisibility] chooses when it shows, [rulesTitle] replaces the heading (empty hides it), [ruleContent] draws a row yourself.
 * - [showStrength] with [strengthLabels], [passwordColors], [strengthOf] (your own scoring) or [strengthContent] (your own view).
 * - [confirmWith]: the other password this one must equal; a mismatch shows [mismatchMessage] as the field's error.
 * - [allowReveal], [initiallyRevealed], [revealIcon] and [hideIcon] control the toggle; [maskCharacter] the dots.
 * - [onValidityChange] reports whether every rule is met and the match check passes, so the caller can enable a submit button.
 */
@Composable
fun FormInputPasswordField(
    state: FormInputPasswordState,
    onValueChange: (FormInputPasswordState) -> Unit,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    colors: TextFieldColors? = null,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    contentPadding: PaddingValues? = null,
    enabled: Boolean = true,
    textStyle: TextStyle? = null,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: (@Composable () -> Unit)? = null,
    rules: List<PasswordRule>? = null,
    rulesVisibility: PasswordRulesVisibility = PasswordRulesVisibility.ALWAYS,
    rulesTitle: String? = null,
    ruleContent: (@Composable (rule: PasswordRule, met: Boolean) -> Unit)? = null,
    showStrength: Boolean = true,
    strengthLabels: PasswordStrengthLabels = PasswordStrengthLabels(),
    passwordColors: PasswordColors = PasswordColors(),
    strengthOf: ((String) -> PasswordStrength)? = null,
    strengthContent: (@Composable (strength: PasswordStrength, label: String) -> Unit)? = null,
    confirmWith: String? = null,
    mismatchMessage: String? = null,
    maskCharacter: Char = '•',
    allowReveal: Boolean = true,
    initiallyRevealed: Boolean = false,
    revealIcon: ImageVector = Icons.Default.Visibility,
    hideIcon: ImageVector = Icons.Default.VisibilityOff,
    onValidityChange: ((Boolean) -> Unit)? = null,
) {
    val activeRules = rules ?: defaultPasswordRules()
    var revealed by remember { mutableStateOf(initiallyRevealed) }
    var focused by remember { mutableStateOf(false) }
    val value = state.value

    val mismatchText = mismatchMessage ?: formInputString(FormInputStrings::passwordMismatch, Res.string.password_mismatch)
    val mismatch = confirmWith != null && value.isNotEmpty() && value != confirmWith
    val allRulesMet = activeRules.all { it.isMet(value) }
    val valid = value.isNotEmpty() && allRulesMet && (confirmWith == null || value == confirmWith)
    LaunchedEffect(valid) { onValidityChange?.invoke(valid) }

    val fieldState = FormInputTextFieldState(
        id = state.id,
        labelRes = state.labelRes,
        placeholderRes = state.placeholderRes,
        type = FormInputType.PASSWORD,
        isMandatory = state.isMandatory,
        hasError = state.hasError || mismatch,
        errorRes = if (mismatch) null else state.errorRes,
        icon = state.icon,
        isVisible = state.isVisible,
        value = value,
        label = state.label,
        placeholder = state.placeholder,
        error = if (mismatch) mismatchText else state.error,
    )

    val toggle: (@Composable () -> Unit)? = if (allowReveal) {
        {
            IconButton(onClick = { revealed = !revealed }, enabled = enabled) {
                Icon(
                    imageVector = if (revealed) hideIcon else revealIcon,
                    contentDescription = if (revealed) formInputString(FormInputStrings::hidePassword, Res.string.hide_password) else formInputString(FormInputStrings::showPassword, Res.string.show_password),
                    modifier = Modifier.size(Dimens.twoAndHalfGrid),
                )
            }
        }
    } else {
        null
    }

    Column(modifier = modifier.fillMaxWidth()) {
        FormInputTextField(
            state = fieldState,
            onValueChange = { onValueChange(state.copy(value = it.value)) },
            textModifier = textModifier.onFocusChanged { focused = it.isFocused },
            colors = colors,
            shape = shape,
            style = style,
            contentPadding = contentPadding,
            enabled = enabled,
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
            keyboardActions = keyboardActions,
            visualTransformation = if (revealed) VisualTransformation.None else PasswordVisualTransformation(maskCharacter),
            leadingIcon = leadingIcon,
            trailingIcon = toggle,
        )

        if (showStrength && value.isNotEmpty()) {
            val strength = strengthOf?.invoke(value) ?: passwordStrength(value, activeRules)
            val label = strengthLabel(strength, strengthLabels)
            if (strengthContent != null) {
                strengthContent(strength, label)
            } else {
                StrengthIndicator(strength, label, passwordColors)
            }
        }

        val showRules = activeRules.isNotEmpty() && when (rulesVisibility) {
            PasswordRulesVisibility.ALWAYS -> true
            PasswordRulesVisibility.WHEN_FOCUSED -> focused
            PasswordRulesVisibility.WHEN_NOT_EMPTY -> value.isNotEmpty()
            PasswordRulesVisibility.NEVER -> false
        }
        if (showRules) {
            RuleChecklist(
                title = rulesTitle ?: formInputString(FormInputStrings::passwordRulesTitle, Res.string.password_rules_title),
                rules = activeRules,
                value = value,
                colors = passwordColors,
                ruleContent = ruleContent,
            )
        }
    }
}

@Composable
private fun strengthLabel(strength: PasswordStrength, labels: PasswordStrengthLabels): String = when (strength) {
    PasswordStrength.NONE -> ""
    PasswordStrength.WEAK -> labels.weak ?: formInputString(FormInputStrings::strengthWeak, Res.string.password_strength_weak)
    PasswordStrength.MEDIUM -> labels.medium ?: formInputString(FormInputStrings::strengthMedium, Res.string.password_strength_medium)
    PasswordStrength.STRONG -> labels.strong ?: formInputString(FormInputStrings::strengthStrong, Res.string.password_strength_strong)
    PasswordStrength.VERY_STRONG -> labels.veryStrong ?: formInputString(FormInputStrings::strengthVeryStrong, Res.string.password_strength_very_strong)
}

@Composable
private fun StrengthIndicator(strength: PasswordStrength, label: String, colors: PasswordColors) {
    val color = when (strength) {
        PasswordStrength.NONE, PasswordStrength.WEAK -> colors.weak ?: MaterialTheme.colorScheme.error
        PasswordStrength.MEDIUM -> colors.medium
        PasswordStrength.STRONG -> colors.strong
        PasswordStrength.VERY_STRONG -> colors.veryStrong
    }
    val filled = when (strength) {
        PasswordStrength.NONE -> 0
        PasswordStrength.WEAK -> 1
        PasswordStrength.MEDIUM -> 2
        PasswordStrength.STRONG -> 3
        PasswordStrength.VERY_STRONG -> 4
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = Dimens.oneGrid),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(Dimens.halfGrid)) {
            repeat(4) { index ->
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimens.halfGrid)
                        .background(
                            color = if (index < filled) color else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(Dimens.quarterGrid),
                        ),
                )
            }
        }
        Spacer(Modifier.width(Dimens.oneAndHalfGrid))
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun RuleChecklist(
    title: String,
    rules: List<PasswordRule>,
    value: String,
    colors: PasswordColors,
    ruleContent: (@Composable (PasswordRule, Boolean) -> Unit)?,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = Dimens.oneAndHalfGrid),
        verticalArrangement = Arrangement.spacedBy(Dimens.oneGrid),
    ) {
        if (title.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(Dimens.twoAndHalfGrid))
                Spacer(Modifier.width(Dimens.oneGrid))
                Text(text = title, style = MaterialTheme.typography.titleSmall)
            }
        }
        rules.forEach { rule ->
            val met = rule.isMet(value)
            if (ruleContent != null) {
                ruleContent(rule, met)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (met) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = if (met) formInputString(FormInputStrings::ruleMet, Res.string.cd_rule_met) else formInputString(FormInputStrings::ruleNotMet, Res.string.cd_rule_not_met),
                        tint = if (met) colors.ruleMet else colors.ruleUnmet ?: MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(Dimens.twoAndHalfGrid),
                    )
                    Spacer(Modifier.width(Dimens.oneGrid))
                    Text(text = rule.text, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
