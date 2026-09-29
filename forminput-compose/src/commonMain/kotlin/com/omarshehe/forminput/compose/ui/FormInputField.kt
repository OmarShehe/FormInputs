package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.omarshehe.forminput.compose.ui.model.*

@Composable
fun FormInputField(
    state: FormInputState,
    onValueChange: (input: FormInputState) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteImage: (String) -> Unit = {},
) {
    when (state) {
        is FormInputTextFieldState -> {
            FormInputTextField(
                state = state,
                modifier = modifier,
                onValueChange = { newState -> onValueChange(newState) },
            )
        }

        is FormInputFileState -> {
            FormInputUploadDocument(
                state = state,
                onValueChange = onValueChange,
                modifier = modifier,
            )
        }

        is FormInputMultiFileState -> {
            FormInputUploadMultiDocument(
                state = state,
                onValueChange = { onValueChange(it) },
                modifier = modifier,
            )
        }

        is FormInputImageState -> {
            FormInputUploadImage(
                state = state,
                onValueChange = { newValue -> onValueChange(newValue) },
                onDeleteImage = onDeleteImage,
                modifier = modifier,
            )
        }

        is FormInputDropDownState -> {
            FormInputDropDownField(
                state = state,
                modifier = modifier,
                onSelected = { newState -> onValueChange(newState) },
            )
        }

        is FormInputDateTimePickerState -> {
            when (state.type) {
                FormInputType.DATE_PICKER -> {
                    FormInputDatePickerField(
                        state = state,
                        modifier = modifier,
                        onValueChange = { newState -> onValueChange(newState) },
                    )
                }

                FormInputType.TIME_PICKER -> {
                    FormInputTimePickerField(
                        state = state,
                        modifier = modifier,
                        onValueChange = { newState -> onValueChange(newState) },
                    )
                }

                FormInputType.DATE_TIME_PICKER -> {
                    FormInputDateTimePickerField(
                        state = state,
                        modifier = modifier,
                        onValueChange = { newState -> onValueChange(newState) },
                    )
                }

                else -> {}
            }
        }
    }
}
