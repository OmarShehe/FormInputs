package com.omarshehe.forminput.compose.ui.formInput

import androidx.compose.runtime.MutableState
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.cant_be_empty
import com.omarshehe.forminput.compose.resources.invalid_email_address
import com.omarshehe.forminput.compose.resources.maximum_limit
import com.omarshehe.forminput.compose.ui.model.FormInputResultState
import com.omarshehe.forminput.compose.ui.model.FormInputResultState.Error
import com.omarshehe.forminput.compose.ui.model.FormInputResultState.Idle
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType

fun MutableState<FormInputResultState>.validate(textValue: String, formInputData: FormInputTextFieldState): FormInputResultState {
    val inputType = value.inputType
    val inputError = when {
        textValue.isEmpty() && formInputData.isMandatory -> Error(inputType, Res.string.cant_be_empty)
        inputType == FormInputType.TEXT -> if (formInputData.maxChar != -1 && textValue.length > formInputData.maxChar) {
            Error(inputType, Res.string.maximum_limit)
        } else {
            Idle(inputType)
        }

        inputType == FormInputType.EMAIL -> if (textValue.isValidEmail()) {
            Idle(inputType)
        } else {
            Error(inputType, Res.string.invalid_email_address)
        }

        else -> Idle(inputType)
    }
    value = inputError
    return inputError
}

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9](?:[A-Za-z0-9\\-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9\\-]{0,61}[A-Za-z0-9])?)+$")

public fun String.isValidEmail(): Boolean = isNotEmpty() && EMAIL_REGEX.matches(this)
