package com.omarshehe.forminput.compose.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.ui.model.FormInputPasswordState
import com.omarshehe.forminput.compose.ui.utils.PasswordRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The default checklist can be trimmed rule by rule, so a form can drop what it does not ask of its users. */
@OptIn(ExperimentalTestApi::class)
class PasswordRulesCustomizationTest {
    private fun androidx.compose.ui.test.ComposeUiTest.rules(build: @androidx.compose.runtime.Composable () -> List<PasswordRule>): List<PasswordRule> {
        lateinit var result: List<PasswordRule>
        setContent { result = build() }
        waitForIdle()
        return result
    }

    @Test
    fun theDefaultChecklistKeepsAllFourRules() = runComposeUiTest {
        val rules = rules { defaultPasswordRules() }
        assertEquals(4, rules.size)
        assertFalse(rules.all { it.isMet("Abcdef1x") }, "no special character yet")
        assertTrue(rules.all { it.isMet("Abcdef1!") })
    }

    @Test
    fun theSpecialCharacterRuleCanBeDropped() = runComposeUiTest {
        val rules = rules { defaultPasswordRules(special = false) }
        assertEquals(3, rules.size)
        assertTrue(rules.all { it.isMet("Abcdef1x") })
    }

    @Test
    fun theUpperCaseRuleCanBeDropped() = runComposeUiTest {
        val rules = rules { defaultPasswordRules(upperCase = false) }
        assertEquals(3, rules.size)
        assertTrue(rules.all { it.isMet("abcdef1!") })
    }

    @Test
    fun theDigitRuleCanBeDropped() = runComposeUiTest {
        val rules = rules { defaultPasswordRules(digit = false) }
        assertEquals(3, rules.size)
        assertTrue(rules.all { it.isMet("Abcdefg!") })
    }

    @Test
    fun aMinimumLengthOfZeroDropsTheLengthRule() = runComposeUiTest {
        val rules = rules { defaultPasswordRules(minLength = 0) }
        assertEquals(3, rules.size)
        assertTrue(rules.all { it.isMet("A1!") }, "short passwords are fine now")
    }

    @Test
    fun theMinimumLengthCanBeChanged() = runComposeUiTest {
        val rules = rules { defaultPasswordRules(minLength = 12) }
        assertEquals(4, rules.size)
        assertFalse(rules.all { it.isMet("Abcdef1!") }, "8 characters is below 12")
        assertTrue(rules.all { it.isMet("Abcdefghij1!") })
    }

    @Test
    fun everyRuleCanBeSwitchedOffLeavingAnEmptyChecklist() = runComposeUiTest {
        val rules = rules { defaultPasswordRules(minLength = 0, upperCase = false, special = false, digit = false) }
        assertTrue(rules.isEmpty())
    }

    @Test
    fun aDroppedRuleIsNotShownAndTheOthersAre() = runComposeUiTest {
        setContent {
            FormInputPasswordField(
                state = FormInputPasswordState(id = "p", value = "Abcdef1x"), onValueChange = {},
                rules = defaultPasswordRules(special = false),
            )
        }
        onNodeWithText("Include at least one special character").assertDoesNotExist()
        onNodeWithText("Include at least one upper case letter").assertExists()
        onNodeWithText("Include at least one number").assertExists()
    }

    @Test
    fun theFieldIsValidWhenOnlyTheRemainingRulesAreMet() = runComposeUiTest {
        var valid: Boolean? = null
        setContent {
            FormInputPasswordField(
                state = FormInputPasswordState(id = "p", value = "Abcdef1x"), onValueChange = {},
                rules = defaultPasswordRules(special = false), onValidityChange = { valid = it },
            )
        }
        waitForIdle()
        assertEquals(true, valid)
    }
}
