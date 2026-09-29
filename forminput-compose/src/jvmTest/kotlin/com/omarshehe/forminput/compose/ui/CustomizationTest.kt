package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.shape.CircleShape
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.label_uploaded_documents
import com.omarshehe.forminput.compose.resources.select_date
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.ui.model.DropDownOptionModel
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputDropDownState
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.model.FormInputFileState
import com.omarshehe.forminput.compose.ui.model.FormInputImageState
import com.omarshehe.forminput.compose.ui.model.FormInputMultiFileState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CustomizationTest {
    private fun date(error: String? = null) = FormInputDateTimePickerState(
        id = "d", labelRes = null, placeholderRes = null, type = FormInputType.DATE_PICKER,
        label = "When", hasError = error != null, error = error,
    )

    @Test
    fun pickerShowsPlainLabelAndHint() = runComposeUiTest {
        setContent { FormInputDatePickerField(state = date(), onValueChange = {}, supportingText = "Pick a day") }
        onNodeWithText("When").assertExists()
        onNodeWithText("Pick a day").assertExists()
    }

    @Test
    fun pickerErrorReplacesHint() = runComposeUiTest {
        setContent { FormInputTimePickerField(state = date("Too late"), onValueChange = {}, supportingText = "Pick a time") }
        onNodeWithText("Too late").assertExists()
        onNodeWithText("Pick a time").assertDoesNotExist()
    }

    @Test
    fun disabledPickerDoesNotOpen() = runComposeUiTest {
        setContent { FormInputDatePickerField(state = date(), onValueChange = {}, enabled = false) }
        onNodeWithText("When").performClick()
        onNodeWithText("OK").assertDoesNotExist()
    }

    @Test
    fun enabledPickerOpens() = runComposeUiTest {
        setContent { FormInputDatePickerField(state = date(), onValueChange = {}, style = FormInputFieldStyle.FILLED) }
        onNodeWithText("When").performClick()
        onNodeWithText("OK").assertExists()
    }

    @Test
    fun colorPickerTakesPlainLabelAndHint() = runComposeUiTest {
        setContent {
            FormInputColorPickerField(label = "Paint", value = "#FF0000", onColorSelected = {}, supportingText = "Body colour")
        }
        onNodeWithText("Paint").assertExists()
        onNodeWithText("Body colour").assertExists()
    }

    @Test
    fun dropdownShowsRuntimeErrorAsWritten() = runComposeUiTest {
        val state = FormInputDropDownState(
            id = "r", label = "Region", value = DropDownOptionModel(), hasError = true, error = "Pick one",
        )
        setContent { FormInputDropDownField(state = state, onSelected = {}) }
        onNodeWithText("Pick one").assertExists()
    }

    @Test
    fun styledSearchHasNoFloatingLabel() = runComposeUiTest {
        val state = FormInputTextFieldState(id = "s", type = FormInputType.TEXT, value = "", label = "Find", placeholder = "Search here")
        setContent { FormInputSearchField(state = state, onValueChange = {}, style = FormInputFieldStyle.FILLED) }
        onNodeWithText("Search here").assertExists()
        onNodeWithText("Find").assertDoesNotExist()
    }

    @Test
    fun uploadsAndStepperTakeAShape() = runComposeUiTest {
        setContent {
            QuantityStepperControl(label = "Seats", value = 1, onValueChange = {}, shape = CircleShape)
            FormInputUploadDocument(
                state = FormInputFileState(id = "f", labelRes = null, placeholderRes = null, label = "Passport", placeholder = "Choose"),
                onValueChange = {},
                shape = CircleShape,
            )
            FormInputUploadMultiDocument(
                state = FormInputMultiFileState(id = "m", placeholder = "Add files"),
                onValueChange = {},
                shape = CircleShape,
            )
            FormInputUploadImage(
                state = FormInputImageState(id = "i", labelRes = null, placeholderRes = null, label = "Photos", values = listOf(null)),
                onValueChange = {},
                shape = CircleShape,
            )
        }
        onNodeWithText("Seats").assertExists()
        onNodeWithText("Passport").assertExists()
        onNodeWithText("Choose").assertExists()
        onNodeWithText("Add files").assertExists()
        onNodeWithText("Photos").assertExists()
    }

    @Test
    fun formThemeSuppliesShapeAndStyle() = runComposeUiTest {
        val state = FormInputTextFieldState(id = "t", type = FormInputType.TEXT, value = "", label = "Name")
        setContent {
            FormInputTheme(FormInputDefaults(style = FormInputFieldStyle.FILLED, shape = CircleShape)) {
                FormInputTextField(state = state, onValueChange = {})
                FormInputDatePickerField(state = date(), onValueChange = {})
            }
        }
        onNodeWithText("Name").assertExists()
        onNodeWithText("When").assertExists()
    }

    @Test
    fun disabledTextFieldIsNotEditable() = runComposeUiTest {
        val state = FormInputTextFieldState(id = "t", type = FormInputType.TEXT, value = "fixed", label = "Name")
        setContent { FormInputTextField(state = state, onValueChange = {}, enabled = false) }
        onAllNodes(hasSetTextAction()).assertCountEquals(0)
    }

    @Test
    fun customTrailingIconReplacesThePasswordToggle() = runComposeUiTest {
        val state = FormInputTextFieldState(id = "p", type = FormInputType.PASSWORD, value = "", label = "Secret")
        setContent { FormInputTextField(state = state, onValueChange = {}, trailingIcon = { Text("custom") }) }
        onNodeWithText("custom").assertExists()
        onNodeWithContentDescription("Show password").assertDoesNotExist()
    }

    @Test
    fun dropdownItemContentReplacesTheOptionText() = runComposeUiTest {
        val state = FormInputDropDownState(
            id = "r", label = "Region", value = DropDownOptionModel(),
            options = listOf(DropDownOptionModel("1", "Dodoma")),
        )
        setContent {
            FormInputDropDownField(state = state, onSelected = {}, menuShape = CircleShape, itemContent = { Text("* ${it.text}") })
        }
        onNodeWithText("Region").performClick()
        onNodeWithText("* Dodoma").assertExists()
    }

    @Test
    fun disabledSearchFieldIsNotEditable() = runComposeUiTest {
        val state = FormInputTextFieldState(id = "s", type = FormInputType.TEXT, value = "")
        setContent { FormInputSearchField(state = state, onValueChange = {}, enabled = false) }
        onAllNodes(hasSetTextAction()).assertCountEquals(0)
    }

    @Test
    fun uploadsAndStepperFollowStyleAndColors() = runComposeUiTest {
        setContent {
            val colors = TextFieldDefaults.colors()
            QuantityStepperControl(label = "Seats", value = 1, onValueChange = {}, style = FormInputFieldStyle.FILLED, colors = colors)
            FormInputUploadDocument(
                state = FormInputFileState(id = "f", labelRes = null, placeholderRes = null, label = "Passport", hasError = true),
                onValueChange = {}, style = FormInputFieldStyle.OUTLINED,
            )
            FormInputUploadMultiDocument(state = FormInputMultiFileState(id = "m"), onValueChange = {}, style = FormInputFieldStyle.FILLED, colors = colors)
            FormInputUploadImage(
                state = FormInputImageState(id = "i", labelRes = null, placeholderRes = null, label = "Photos", values = listOf(null)),
                onValueChange = {}, style = FormInputFieldStyle.FILLED,
            )
        }
        onNodeWithText("Seats").assertExists()
        onNodeWithText("Passport").assertExists()
        onNodeWithText("Browse files").assertExists()
        onNodeWithText("Photos").assertExists()
    }

    @androidx.compose.runtime.Composable
    private fun themed(strings: FormInputStrings, content: @androidx.compose.runtime.Composable () -> Unit) {
        FormInputTheme(FormInputDefaults(strings = strings), content)
    }

    @Test
    fun overriddenTextWinsAndUnsetTextKeepsTheDefault() = runComposeUiTest {
        val state = com.omarshehe.forminput.compose.ui.model.FormInputPasswordState(id = "pw", value = "abc", label = "Password")
        setContent {
            themed(FormInputStrings(showPassword = "Afficher", passwordRulesTitle = "Il faut :", passwordRuleMinLength = { "Au moins $it" })) {
                FormInputPasswordField(state = state, onValueChange = {})
            }
        }
        onNodeWithContentDescription("Afficher").assertExists()
        onNodeWithContentDescription("Show password").assertDoesNotExist()
        onNodeWithText("Il faut :").assertExists()
        onNodeWithText("Au moins 8").assertExists()
        onNodeWithText("Include at least one number").assertExists()
    }

    @Test
    fun dialogButtonsAndSearchHintCanBeOverridden() = runComposeUiTest {
        val search = FormInputTextFieldState(id = "s", type = FormInputType.TEXT, value = "")
        setContent {
            themed(FormInputStrings(ok = "D'accord", cancel = "Annuler", searchHint = "Chercher")) {
                androidx.compose.foundation.layout.Column {
                    FormInputDatePickerField(state = date(), onValueChange = {})
                    FormInputSearchField(state = search, onValueChange = {})
                }
            }
        }
        onNodeWithText("Chercher").assertExists()
        onNodeWithText("When").performClick()
        onNodeWithText("D'accord").assertExists()
        onNodeWithText("Annuler").assertExists()
        onNodeWithText("OK").assertDoesNotExist()
    }

    @Test
    fun withoutAnOverrideTheLibraryTextIsUsed() = runComposeUiTest {
        val search = FormInputTextFieldState(id = "s", type = FormInputType.TEXT, value = "")
        setContent { FormInputSearchField(state = search, onValueChange = {}) }
        onNodeWithText("Search...").assertExists()
    }

    @Test
    fun libraryResourcesPassedAsLabelsFollowTheOverride() = runComposeUiTest {
        val picker = FormInputDateTimePickerState(
            id = "d", labelRes = Res.string.select_date,
            placeholderRes = null, type = FormInputType.DATE_PICKER,
        )
        val docs = FormInputFileState(
            id = "f", labelRes = Res.string.label_uploaded_documents, placeholderRes = null,
        )
        setContent {
            themed(FormInputStrings(selectDate = "Choisir une date", uploadedDocuments = "Documents")) {
                androidx.compose.foundation.layout.Column {
                    FormInputDatePickerField(state = picker, onValueChange = {})
                    FormInputUploadDocument(state = docs, onValueChange = {})
                }
            }
        }
        onNodeWithText("Choisir une date").assertExists()
        onNodeWithText("Select Date").assertDoesNotExist()
        onNodeWithText("Documents").assertExists()
    }
}
