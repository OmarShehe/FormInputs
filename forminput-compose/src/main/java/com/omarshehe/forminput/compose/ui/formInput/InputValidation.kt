package com.omarshehe.forminput.compose.ui.formInput

import androidx.compose.runtime.MutableState
import androidx.core.util.PatternsCompat
import com.omarshehe.forminput.compose.R
import com.omarshehe.forminput.compose.ui.model.FormInputResultState
import com.omarshehe.forminput.compose.ui.model.FormInputResultState.Error
import com.omarshehe.forminput.compose.ui.model.FormInputResultState.Idle
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType

fun MutableState<FormInputResultState>.validate(textValue: String, formInputData: FormInputTextFieldState): FormInputResultState {
    val inputType = value.inputType
    val inputError = when {
        textValue.isEmpty() && formInputData.isMandatory -> Error(inputType, R.string.cant_be_bmpty)
        inputType == FormInputType.TEXT -> if (formInputData.maxChar != -1 && textValue.length > formInputData.maxChar) {
            Error(inputType, R.string.maximum_limit)
        } else {
            Idle(inputType)
        }

        inputType == FormInputType.EMAIL -> if (textValue.isValidEmail()) {
            Idle(inputType)
        } else {
            Error(inputType, R.string.invalid_email_address)
        }

        else -> Idle(inputType)
    }
    value = inputError
    return inputError
}

fun String.isValidEmail() = this.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
