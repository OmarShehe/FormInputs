package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.ui.model.FormInputPasswordState
import com.omarshehe.forminput.compose.ui.model.FormInputPriceState
import com.omarshehe.forminput.compose.ui.utils.PasswordRule
import com.omarshehe.forminput.compose.ui.utils.PasswordRules
import com.omarshehe.forminput.compose.ui.utils.PasswordStrength
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class PriceAndPasswordFieldTest {
    private fun price(amount: String = "") = FormInputPriceState(
        id = "p", amount = amount, currency = "TSH", currencies = listOf("TSH", "USD"), label = "Price",
    )

    private fun password(value: String = "") = FormInputPasswordState(id = "pw", value = value, label = "Password")

    @Test
    fun priceKeepsTheAmountAndRefusesTooManyDecimals() = runComposeUiTest {
        var state by mutableStateOf(price())
        setContent { FormInputPriceField(state = state, onValueChange = { state = it }) }
        onNode(hasSetTextAction()).performTextInput("12.5")
        assertEquals("12.5", state.amount)
        onNode(hasSetTextAction()).performTextInput("678")
        assertEquals("12.5", state.amount)
    }

    @Test
    fun priceCurrencyCanBeChanged() = runComposeUiTest {
        var state by mutableStateOf(price())
        setContent { FormInputPriceField(state = state, onValueChange = { state = it }) }
        onNodeWithText("TSH").performClick()
        onNodeWithText("USD").performClick()
        assertEquals("USD", state.currency)
    }

    @Test
    fun singleCurrencyCannotBeChanged() = runComposeUiTest {
        var state by mutableStateOf(FormInputPriceState(id = "p", currency = "TSH", label = "Price"))
        setContent { FormInputPriceField(state = state, onValueChange = { state = it }) }
        onNodeWithText("TSH").performClick()
        onNodeWithText("USD").assertDoesNotExist()
    }

    @Test
    fun priceCanShowGroupedThousandsAndAPlacedCurrency() = runComposeUiTest {
        setContent {
            FormInputPriceField(
                state = price("1234567"), onValueChange = {}, groupThousands = true,
                currencyPlacement = CurrencyPlacement.END, currencyLabel = { "$it!" },
            )
        }
        onNodeWithText("1,234,567").assertExists()
        onNodeWithText("TSH!").assertExists()
    }

    @Test
    fun disabledPriceDoesNotOpenTheList() = runComposeUiTest {
        setContent { FormInputPriceField(state = price(), onValueChange = {}, enabled = false) }
        onNodeWithText("TSH").performClick()
        onNodeWithText("USD").assertDoesNotExist()
    }

    @Test
    fun passwordShowsTheDefaultChecklistAndTicksMetRules() = runComposeUiTest {
        setContent { FormInputPasswordField(state = password("Abcdef1x"), onValueChange = {}) }
        onNodeWithText("Your password needs to:").assertExists()
        onNodeWithText("Include at least one special character").assertExists()
        onNodeWithText("Strong").assertExists()
    }

    private fun androidx.compose.ui.test.ComposeUiTest.shownText(): String =
        onNode(hasSetTextAction()).fetchSemanticsNode().config[SemanticsProperties.EditableText].text

    @Test
    fun passwordIsMaskedUntilRevealed() = runComposeUiTest {
        setContent { FormInputPasswordField(state = password("secret"), onValueChange = {}, rulesVisibility = PasswordRulesVisibility.NEVER) }
        assertEquals("\u2022\u2022\u2022\u2022\u2022\u2022", shownText())
        onNodeWithContentDescription("Show password").performClick()
        assertEquals("secret", shownText())
        onNodeWithContentDescription("Hide password").assertExists()
    }

    @Test
    fun passwordMaskCharacterCanBeChosen() = runComposeUiTest {
        setContent {
            FormInputPasswordField(state = password("abc"), onValueChange = {}, maskCharacter = '*', rulesVisibility = PasswordRulesVisibility.NEVER)
        }
        assertEquals("***", shownText())
    }

    @Test
    fun passwordCanStartRevealedOrHaveNoToggle() = runComposeUiTest {
        setContent {
            FormInputPasswordField(state = password("open"), onValueChange = {}, initiallyRevealed = true, allowReveal = false)
        }
        assertEquals("open", shownText())
        onNodeWithContentDescription("Show password").assertDoesNotExist()
        onNodeWithContentDescription("Hide password").assertDoesNotExist()
    }

    @Test
    fun passwordUsesCustomRulesTitleAndLabels() = runComposeUiTest {
        val rules = listOf(PasswordRules.minLength(3, "Three or more"), PasswordRule("Starts with x") { it.startsWith("x") })
        setContent {
            FormInputPasswordField(
                state = password("abc"), onValueChange = {}, rules = rules, rulesTitle = "Rules",
                strengthLabels = PasswordStrengthLabels(medium = "So-so"),
            )
        }
        onNodeWithText("Rules").assertExists()
        onNodeWithText("Three or more").assertExists()
        onNodeWithText("Starts with x").assertExists()
        onNodeWithText("So-so").assertExists()
        onNodeWithText("Include at least one number").assertDoesNotExist()
    }

    @Test
    fun passwordRulesCanBeHiddenOrCustomDrawn() = runComposeUiTest {
        setContent {
            FormInputPasswordField(
                state = password("a"), onValueChange = {}, rules = listOf(PasswordRules.digit("Needs a digit")),
                ruleContent = { rule, met -> androidx.compose.material3.Text("${rule.text}: $met") },
            )
        }
        onNodeWithText("Needs a digit: false").assertExists()
    }

    @Test
    fun passwordWhenFocusedRulesStartHidden() = runComposeUiTest {
        setContent {
            FormInputPasswordField(state = password(), onValueChange = {}, rulesVisibility = PasswordRulesVisibility.WHEN_FOCUSED)
        }
        onNodeWithText("Your password needs to:").assertDoesNotExist()
    }

    @Test
    fun confirmFieldShowsAMismatchAndReportsValidity() = runComposeUiTest {
        var valid: Boolean? = null
        var other by mutableStateOf("Abcdef1!")
        setContent {
            FormInputPasswordField(
                state = password("Abcdef1?"), onValueChange = {}, confirmWith = other, rules = emptyList(),
                showStrength = false, onValidityChange = { valid = it },
            )
        }
        onNodeWithText("Passwords do not match").assertExists()
        assertEquals(false, valid)
        other = "Abcdef1?"
        waitForIdle()
        onNodeWithText("Passwords do not match").assertDoesNotExist()
        assertEquals(true, valid)
    }

    @Test
    fun customStrengthScoreAndView() = runComposeUiTest {
        setContent {
            FormInputPasswordField(
                state = password("anything"), onValueChange = {}, rulesVisibility = PasswordRulesVisibility.NEVER,
                strengthOf = { PasswordStrength.MEDIUM },
                strengthContent = { strength, label -> androidx.compose.material3.Text("$strength/$label") },
            )
        }
        onNodeWithText("MEDIUM/Medium").assertExists()
    }
}
