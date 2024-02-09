package com.omarshehe.forminput.compose.ui.model

internal data class PasswordResultModel(
    val passLevel: PasswordLevel,
    val isUpperCasePresent: Boolean,
    val isSpecialCharPresent: Boolean,
    val isNumberPresent: Boolean,
    val isValidLength: Boolean
)
