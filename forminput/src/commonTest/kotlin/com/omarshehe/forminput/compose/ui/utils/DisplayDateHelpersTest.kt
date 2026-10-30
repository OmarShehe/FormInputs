package com.omarshehe.forminput.compose.ui.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Typed text in a display pattern becomes a stored `yyyy-MM-dd` date only when it is exactly a valid date in that pattern. */
class DisplayDateHelpersTest {
    private val pattern = "dd/MM/yyyy"

    @Test
    fun typedDateInThePatternParsesToIso() {
        assertEquals("2026-12-01", parseDisplayDate("01/12/2026", pattern))
    }

    @Test
    fun monthThirteenIsRejected() {
        assertNull(parseDisplayDate("01/13/2026", pattern))
    }

    @Test
    fun dayThirtyOneInAThirtyDayMonthIsRejected() {
        assertNull(parseDisplayDate("31/04/2026", pattern))
    }

    @Test
    fun leapDayIsOnlyValidInLeapYears() {
        assertEquals("2028-02-29", parseDisplayDate("29/02/2028", pattern))
        assertNull(parseDisplayDate("29/02/2027", pattern))
    }

    @Test
    fun shortYearIsRejected() {
        assertNull(parseDisplayDate("01/12/26", pattern))
    }

    @Test
    fun trailingTextIsRejected() {
        assertNull(parseDisplayDate("01/12/2026x", pattern))
        assertNull(parseDisplayDate(" 01/12/2026", pattern))
    }

    @Test
    fun emptyAndHalfTypedTextAreNull() {
        assertNull(parseDisplayDate("", pattern))
        assertNull(parseDisplayDate("01/1", pattern))
        assertNull(parseDisplayDate("01/12/", pattern))
    }

    @Test
    fun otherNumericPatternsParse() {
        assertEquals("2026-12-01", parseDisplayDate("2026-12-01", "yyyy-MM-dd"))
        assertEquals("2026-12-01", parseDisplayDate("12/01/2026", "MM/dd/yyyy"))
    }

    @Test
    fun formatDisplayDateShowsTheValueInThePattern() {
        assertEquals("01/12/2026", formatDisplayDate("2026-12-01", pattern))
    }

    @Test
    fun formatDisplayDateWithTheIsoDefaultIsUnchanged() {
        assertEquals("2026-12-01", formatDisplayDate("2026-12-01", DateUtils.DATE_FORMAT))
    }

    @Test
    fun formatDisplayDateOfAnEmptyValueIsEmpty() {
        assertEquals("", formatDisplayDate("", pattern))
    }

    @Test
    fun formatDisplayDateShowsABadValueAsItIs() {
        assertEquals("nope", formatDisplayDate("nope", pattern))
        assertEquals("2026-13-45", formatDisplayDate("2026-13-45", pattern))
    }

    @Test
    fun formatThenParseRoundTrips() {
        listOf("2026-01-01", "2026-12-31", "2028-02-29", "1999-07-09").forEach { iso ->
            assertEquals(iso, parseDisplayDate(formatDisplayDate(iso, pattern), pattern))
        }
    }
}
