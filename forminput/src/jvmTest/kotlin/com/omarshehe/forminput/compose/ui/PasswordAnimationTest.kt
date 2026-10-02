package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.ui.model.FormInputPasswordState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The checklist and the strength meter move instead of jumping, and `animated = false` turns that off. */
@OptIn(ExperimentalTestApi::class)
class PasswordAnimationTest {
    private val met = "Requirement met"
    private val unmet = "Requirement not met"
    private val strong = "Abcdef1!"

    private fun androidx.compose.ui.test.ComposeUiTest.count(description: String) =
        onAllNodesWithContentDescription(description).fetchSemanticsNodes().size

    @Test
    fun aRuleTurnsFromUnmetToMetThroughATransition() = runComposeUiTest {
        var value by mutableStateOf("")
        setContent {
            FormInputPasswordField(
                state = FormInputPasswordState(id = "p", value = value), onValueChange = {}, showStrength = false,
            )
        }
        mainClock.autoAdvance = false
        value = strong
        mainClock.advanceTimeByFrame()
        // Mid-transition both icons exist: the old one leaving and the new one arriving.
        assertTrue(count(unmet) > 0 && count(met) > 0, "expected both icons mid-transition, got unmet=${count(unmet)} met=${count(met)}")
        mainClock.advanceTimeBy(2_000)
        assertEquals(0, count(unmet))
        assertEquals(4, count(met))
    }

    @Test
    fun withAnimationOffARuleChangesAtOnce() = runComposeUiTest {
        var value by mutableStateOf("")
        setContent {
            FormInputPasswordField(
                state = FormInputPasswordState(id = "p", value = value), onValueChange = {}, showStrength = false, animated = false,
            )
        }
        mainClock.autoAdvance = false
        value = strong
        mainClock.advanceTimeByFrame()
        assertEquals(0, count(unmet))
        assertEquals(4, count(met))
    }

    @Test
    fun theChecklistEasesInAndOutWhenItsVisibilityChanges() = runComposeUiTest {
        var value by mutableStateOf("")
        setContent {
            FormInputPasswordField(
                state = FormInputPasswordState(id = "p", value = value), onValueChange = {}, showStrength = false,
                rulesVisibility = PasswordRulesVisibility.WHEN_NOT_EMPTY,
            )
        }
        mainClock.autoAdvance = false
        assertEquals(0, count(unmet) + count(met), "hidden while empty")
        value = "a"
        mainClock.advanceTimeByFrame()
        assertTrue(count(unmet) + count(met) > 0, "present as soon as it starts to appear")
        mainClock.advanceTimeBy(2_000)
        value = ""
        mainClock.advanceTimeByFrame()
        assertTrue(count(unmet) + count(met) > 0, "still present while it leaves")
        mainClock.advanceTimeBy(2_000)
        assertEquals(0, count(unmet) + count(met), "gone once the exit finishes")
    }

    @Test
    fun withAnimationOffTheChecklistAppearsAndVanishesAtOnce() = runComposeUiTest {
        var value by mutableStateOf("")
        setContent {
            FormInputPasswordField(
                state = FormInputPasswordState(id = "p", value = value), onValueChange = {}, showStrength = false,
                rulesVisibility = PasswordRulesVisibility.WHEN_NOT_EMPTY, animated = false,
            )
        }
        mainClock.autoAdvance = false
        value = "a"
        mainClock.advanceTimeByFrame()
        assertTrue(count(unmet) + count(met) > 0)
        value = ""
        mainClock.advanceTimeByFrame()
        mainClock.advanceTimeByFrame()
        assertEquals(0, count(unmet) + count(met))
    }

    @Test
    fun theStrengthMeterEasesInAndOut() = runComposeUiTest {
        var value by mutableStateOf("")
        setContent {
            FormInputPasswordField(
                state = FormInputPasswordState(id = "p", value = value), onValueChange = {}, rulesVisibility = PasswordRulesVisibility.NEVER,
            )
        }
        mainClock.autoAdvance = false
        value = strong
        mainClock.advanceTimeByFrame()
        mainClock.advanceTimeBy(2_000)
        onNodeWithText("Very strong").assertExists()
        value = ""
        mainClock.advanceTimeByFrame()
        // The meter stays on screen while it leaves, then is gone.
        onNodeWithText("Very strong").assertExists()
        mainClock.advanceTimeBy(2_000)
        onNodeWithText("Very strong").assertDoesNotExist()
    }

    @Test
    fun aVisibleChecklistFollowsTheFieldWhenItIsCleared() = runComposeUiTest {
        var value by mutableStateOf(strong)
        setContent {
            FormInputPasswordField(
                state = FormInputPasswordState(id = "p", value = value), onValueChange = {}, showStrength = false, animated = false,
            )
        }
        waitForIdle()
        assertEquals(4, count(met))
        value = ""
        waitForIdle()
        // The checklist stays on screen (visibility ALWAYS), so it must show the empty field, not the last password.
        assertEquals(0, count(met))
        assertEquals(4, count(unmet))
    }
}
