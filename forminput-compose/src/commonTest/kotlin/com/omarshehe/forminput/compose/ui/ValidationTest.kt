package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.mutableStateOf
import com.omarshehe.forminput.compose.ui.formInput.isValidEmail
import com.omarshehe.forminput.compose.ui.formInput.validate
import com.omarshehe.forminput.compose.ui.model.FormInputResultState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import com.omarshehe.forminput.compose.ui.model.PasswordLevel
import com.omarshehe.forminput.compose.ui.utils.PasswordStrength
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationTest {
    private fun field(type: FormInputType, mandatory: Boolean = false, maxChar: Int = 5) = FormInputTextFieldState(
        value = "", id = "f", labelValue = "F", placeholderValue = "", type = type, isMandatory = mandatory, maxChar = maxChar,
    )

    @Test
    fun emailAcceptsOrdinaryAddressesAndRejectsBadOnes() {
        assertTrue("omar@example.co.tz".isValidEmail())
        assertTrue("first.last+tag@sub.example.com".isValidEmail())
        assertFalse("".isValidEmail())
        assertFalse("omar@".isValidEmail())
        assertFalse("omar@example".isValidEmail())
        assertFalse("omar example@x.com".isValidEmail())
    }

    @Test
    fun mandatoryEmptyIsAnError() {
        val state = mutableStateOf<FormInputResultState>(FormInputResultState.Idle(FormInputType.TEXT))
        assertTrue(state.validate("", field(FormInputType.TEXT, mandatory = true)) is FormInputResultState.Error)
    }

    @Test
    fun textOverMaxCharIsAnError() {
        val state = mutableStateOf<FormInputResultState>(FormInputResultState.Idle(FormInputType.TEXT))
        assertTrue(state.validate("toolong", field(FormInputType.TEXT)) is FormInputResultState.Error)
        assertTrue(state.validate("ok", field(FormInputType.TEXT)) is FormInputResultState.Idle)
    }

    @Test
    fun emailTypeUsesEmailRule() {
        val state = mutableStateOf<FormInputResultState>(FormInputResultState.Idle(FormInputType.EMAIL))
        assertTrue(state.validate("nope", field(FormInputType.EMAIL)) is FormInputResultState.Error)
        assertTrue(state.validate("a@b.co", field(FormInputType.EMAIL)) is FormInputResultState.Idle)
    }

    @Test
    fun passwordStrengthLevels() {
        val strength = PasswordStrength()
        assertEquals(PasswordLevel.WEAK, strength.calculateStrength(8, "abc").passLevel)
        assertEquals(PasswordLevel.STRONG, strength.calculateStrength(8, "Abcdef1!").passLevel)
        assertTrue(strength.calculateStrength(8, "Abcdef1!").isSpecialCharPresent)
    }
}
