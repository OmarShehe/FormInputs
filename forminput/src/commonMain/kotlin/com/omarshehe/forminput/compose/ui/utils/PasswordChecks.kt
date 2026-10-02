package com.omarshehe.forminput.compose.ui.utils

/** One requirement shown in the password checklist. [isMet] is checked on every keystroke. */
public data class PasswordRule(val text: String, val isMet: (String) -> Boolean)

public enum class PasswordStrength { NONE, WEAK, MEDIUM, STRONG, VERY_STRONG }

/** Ready-made rules; pass your own [PasswordRule] for anything else. */
public object PasswordRules {
    public fun minLength(min: Int, text: String): PasswordRule = PasswordRule(text) { it.length >= min }
    public fun maxLength(max: Int, text: String): PasswordRule = PasswordRule(text) { it.length <= max }
    public fun upperCase(text: String): PasswordRule = PasswordRule(text) { value -> value.any { it.isUpperCase() } }
    public fun lowerCase(text: String): PasswordRule = PasswordRule(text) { value -> value.any { it.isLowerCase() } }
    public fun digit(text: String): PasswordRule = PasswordRule(text) { value -> value.any { it.isDigit() } }
    public fun special(text: String): PasswordRule = PasswordRule(text) { value -> value.any { !it.isLetterOrDigit() && !it.isWhitespace() } }
    public fun noWhitespace(text: String): PasswordRule = PasswordRule(text) { value -> value.none { it.isWhitespace() } }
}

/**
 * Strength from the share of [rules] that [value] meets: under half is weak, then medium, strong, and very strong when all
 * are met. An empty value is [PasswordStrength.NONE]; with no rules any non-empty value is very strong.
 */
public fun passwordStrength(value: String, rules: List<PasswordRule>): PasswordStrength {
    if (value.isEmpty()) return PasswordStrength.NONE
    if (rules.isEmpty()) return PasswordStrength.VERY_STRONG
    val met = rules.count { it.isMet(value) }
    return when {
        met == rules.size -> PasswordStrength.VERY_STRONG
        met * 4 >= rules.size * 3 -> PasswordStrength.STRONG
        met * 2 >= rules.size -> PasswordStrength.MEDIUM
        else -> PasswordStrength.WEAK
    }
}
