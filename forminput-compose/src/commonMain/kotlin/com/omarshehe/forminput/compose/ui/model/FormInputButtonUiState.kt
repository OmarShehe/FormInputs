package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Composable

sealed class FormInputButtonUiState(val text: Any) {

    data class Loading(val stringText: Any) : FormInputButtonUiState(stringText)

    data class Idle(val stringText: Any) : FormInputButtonUiState(stringText)
}

@Composable
fun FormInputButtonUiState.WhenLoading(code: @Composable (FormInputButtonUiState.Loading) -> Unit) {
    if (this.isLoading()) {
        code(this as FormInputButtonUiState.Loading)
    }
}

fun FormInputButtonUiState.isLoading() = this is FormInputButtonUiState.Loading
