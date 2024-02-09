package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.omarshehe.forminput.compose.ui.formInput.validate
import com.omarshehe.forminput.compose.ui.model.Dimens
import com.omarshehe.forminput.compose.ui.model.FormInputResultState
import com.omarshehe.forminput.compose.ui.model.FormInputResultState.Idle
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType.TEXT
import com.omarshehe.forminput.compose.ui.model.WhenError
import com.omarshehe.forminput.compose.ui.model.isError
import com.omarshehe.forminput.compose.ui.utils.isTrue

@Composable
fun FormInputTextField(
    formInputData: FormInputTextFieldState,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    singleLine: Boolean = true,
    onValueChange: (FormInputTextFieldState) -> Unit = {}
) {
    val label = formInputData.labelValue.asText()
    val resultState = remember { mutableStateOf<FormInputResultState>(Idle(TEXT)) }

    val shep = shapes.medium.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize)

    Column(modifier = modifier) {
        TextField(
            value = formInputData.value,
            isError = resultState.value.isError,
            modifier = textModifier.fillMaxWidth(),
            onValueChange = { newValue ->
                val isError = resultState.validate(newValue, formInputData).isError
                onValueChange(formInputData.copy(value = newValue, hasError = isError))
            },
            singleLine = singleLine,
            label = { Text(label) },
            placeholder = { Text(formInputData.placeholderValue.asText()) },
            keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words),
            shape = shep,
            colors = TextFieldDefaults.colors(),
            leadingIcon = formInputData.icon?.run {
                { FormInputIcon(icon = this, tint = colorScheme.primary) }
            }
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            resultState.value.WhenError {
                val error = "$label ${errorTextValue.asText()}"
                TextContent(textValue = error, color = colorScheme.error, setPadding = false)
            }
            formInputData.showMaxChar.isTrue {
                TextContent(
                    modifier = Modifier.fillMaxWidth(),
                    textValue = "${formInputData.value.length} / ${formInputData.maxChar}",
                    textAlignment = TextAlign.End,
                    setPadding = false
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormInputTextFieldPreview() {
    val dataModel = FormInputTextFieldState(
        id = "fullName",
        value = "",
        labelValue = "Full name",
        placeholderValue = "Please enter full name",
        type = TEXT
    )

    FormInputTextField(
        formInputData = dataModel,
        onValueChange = { }
    )
}
