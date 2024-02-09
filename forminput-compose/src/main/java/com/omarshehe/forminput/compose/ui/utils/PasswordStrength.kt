package com.omarshehe.forminput.compose.ui.utils

import com.omarshehe.forminput.compose.ui.model.PasswordLevel.FAIR
import com.omarshehe.forminput.compose.ui.model.PasswordLevel.GOOD
import com.omarshehe.forminput.compose.ui.model.PasswordLevel.STRONG
import com.omarshehe.forminput.compose.ui.model.PasswordLevel.WEAK
import com.omarshehe.forminput.compose.ui.model.PasswordResultModel

internal class PasswordStrength {

    fun calculateStrength(maxLength: Int, input: String): PasswordResultModel {
        var strengthLevel = 0
        var upperCasePresent = false
        var specialCharPresent = false
        var numberPresent = false
        val isValidLength = input.length >= maxLength

        input.forEach {
            when {
                Character.isUpperCase(it) -> upperCasePresent = true
                Character.isLetterOrDigit(it).not() && Character.isWhitespace(it).not() -> specialCharPresent = true
                Character.isDigit(it) -> numberPresent = true
            }
        }

        if (upperCasePresent) ++strengthLevel
        if (specialCharPresent) ++strengthLevel
        if (numberPresent) ++strengthLevel
        if (isValidLength) ++strengthLevel

        val level = when (strengthLevel) {
            0 -> WEAK
            1 -> WEAK
            2 -> FAIR
            3 -> GOOD
            else -> STRONG
        }
        return PasswordResultModel(
            passLevel = level,
            isUpperCasePresent = upperCasePresent,
            isSpecialCharPresent = specialCharPresent,
            isNumberPresent = numberPresent,
            isValidLength = isValidLength
        )
    }
}
