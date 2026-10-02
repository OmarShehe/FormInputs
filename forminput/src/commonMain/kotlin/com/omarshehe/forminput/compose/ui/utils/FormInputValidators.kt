package com.omarshehe.forminput.compose.ui.utils

public object FormInputValidators {
    private val emailRegex = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")

    public fun isValidEmail(value: String): Boolean = emailRegex.matches(value)
}
