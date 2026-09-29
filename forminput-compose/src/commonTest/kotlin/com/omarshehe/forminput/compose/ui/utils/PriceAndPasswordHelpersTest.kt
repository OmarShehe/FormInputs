package com.omarshehe.forminput.compose.ui.utils

import androidx.compose.ui.text.AnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PriceAndPasswordHelpersTest {
    @Test
    fun amountKeepsDigitsAndOneDot() {
        assertEquals("12.5", sanitizeAmount("1a2.5", 12, 2))
        assertEquals("1.25", sanitizeAmount("1.2.5", 12, 2))
        assertEquals("", sanitizeAmount("abc", 12, 2))
    }

    @Test
    fun amountDropsLeadingZerosButKeepsOne() {
        assertEquals("7", sanitizeAmount("007", 12, 2))
        assertEquals("0", sanitizeAmount("000", 12, 2))
        assertEquals("0.5", sanitizeAmount("0.5", 12, 2))
        assertEquals("0.5", sanitizeAmount(".5", 12, 2))
    }

    @Test
    fun amountRefusesTooManyDigitsOrDecimals() {
        assertNull(sanitizeAmount("1.234", 12, 2))
        assertNull(sanitizeAmount("1234", 3, 2))
        assertEquals("123", sanitizeAmount("123", 3, 2))
    }

    @Test
    fun noDecimalsMeansNoDot() {
        assertEquals("125", sanitizeAmount("12.5", 12, 0))
    }

    @Test
    fun thousandsAreGroupedOnTheWholePartOnly() {
        val shown = ThousandsSeparatorTransformation().filter(AnnotatedString("1234567.891")).text.text
        assertEquals("1,234,567.891", shown)
        assertEquals("123", ThousandsSeparatorTransformation().filter(AnnotatedString("123")).text.text)
        assertEquals("1,234", ThousandsSeparatorTransformation().filter(AnnotatedString("1234")).text.text)
    }

    @Test
    fun thousandsCursorMappingRoundTrips() {
        val transformed = ThousandsSeparatorTransformation().filter(AnnotatedString("1234567"))
        val mapping = transformed.offsetMapping
        assertEquals("1,234,567", transformed.text.text)
        for (offset in 0..7) {
            assertEquals(offset, mapping.transformedToOriginal(mapping.originalToTransformed(offset)))
        }
        assertEquals(9, mapping.originalToTransformed(7))
        assertEquals(2, mapping.originalToTransformed(1))
    }

    private val rules = listOf(
        PasswordRules.upperCase("upper"),
        PasswordRules.special("special"),
        PasswordRules.digit("digit"),
        PasswordRules.minLength(8, "length"),
    )

    @Test
    fun strengthFollowsTheShareOfRulesMet() {
        assertEquals(PasswordStrength.NONE, passwordStrength("", rules))
        assertEquals(PasswordStrength.WEAK, passwordStrength("abc", rules))
        assertEquals(PasswordStrength.WEAK, passwordStrength("Abc", rules))
        assertEquals(PasswordStrength.MEDIUM, passwordStrength("Abc1", rules))
        assertEquals(PasswordStrength.MEDIUM, passwordStrength("abc1abcdefg", rules))
        assertEquals(PasswordStrength.STRONG, passwordStrength("Abcdef1x", rules))
        assertEquals(PasswordStrength.VERY_STRONG, passwordStrength("Abcdef1!", rules))
    }

    @Test
    fun strengthWithNoRulesIsVeryStrongOnceTyped() {
        assertEquals(PasswordStrength.NONE, passwordStrength("", emptyList()))
        assertEquals(PasswordStrength.VERY_STRONG, passwordStrength("a", emptyList()))
    }

    @Test
    fun readyMadeRulesCheckWhatTheySay() {
        assertEquals(true, PasswordRules.upperCase("").isMet("aB"))
        assertEquals(false, PasswordRules.upperCase("").isMet("ab"))
        assertEquals(true, PasswordRules.special("").isMet("a!"))
        assertEquals(false, PasswordRules.special("").isMet("a b1"))
        assertEquals(true, PasswordRules.noWhitespace("").isMet("ab"))
        assertEquals(false, PasswordRules.noWhitespace("").isMet("a b"))
        assertEquals(true, PasswordRules.maxLength(3, "").isMet("abc"))
        assertEquals(false, PasswordRules.maxLength(3, "").isMet("abcd"))
    }
}
