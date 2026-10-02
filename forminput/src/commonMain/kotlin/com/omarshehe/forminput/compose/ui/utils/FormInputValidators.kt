package com.omarshehe.forminput.compose.ui.utils

object FormInputValidators {
    private val emailRegex = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(value: String): Boolean = emailRegex.matches(value)
}
