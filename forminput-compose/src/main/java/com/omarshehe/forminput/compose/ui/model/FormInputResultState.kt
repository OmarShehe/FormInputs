package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Composable

sealed class FormInputResultState(val inputType: FormInputType = FormInputType.TEXT) {
    data class Error(val type: FormInputType, val errorTextValue: Any) : FormInputResultState(type)
    data class Idle(val type: FormInputType) : FormInputResultState(type)
}

fun FormInputResultState.isValid(): Boolean = this is FormInputResultState.Idle

val FormInputResultState.isError get() = this !is FormInputResultState.Idle

@Composable
fun FormInputResultState.WhenError(codes: @Composable FormInputResultState.Error.() -> Unit) {
    if (this is FormInputResultState.Error) codes(this)
}
