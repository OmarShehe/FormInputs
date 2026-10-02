package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.omarshehe.forminput.compose.ui.model.*

/**
 * Picks the input that matches [state] (text, password, price, dropdown, date and time, file or image upload) and draws it with
 * its default options, so a form can be drawn from a list of states: call this for each one and replace the state that comes back
 * (by its `id`) in your list. A state type with no input of its own draws nothing. Use the dedicated composable when you need to
 * pass a shape, style, size or other option; a `FormInputTheme` around the form sets style and shape for all of them.
 */
@Composable
public fun FormInputField(
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

        is FormInputPasswordState -> {
            FormInputPasswordField(
                state = state,
                modifier = modifier,
                onValueChange = { newState -> onValueChange(newState) },
            )
        }

        is FormInputPriceState -> {
            FormInputPriceField(
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
