package com.omarshehe.forminput.compose.ui.utils

import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DateHelpersTest {
    private val dayMillis = 86_400_000L

    @Test
    fun dateStringParsesToUtcMidnight() {
        assertEquals(dayMillis, "1970-01-02".dateToUtcMillis())
        assertEquals(946_684_800_000L, "2000-01-01".dateToUtcMillis())
    }

    @Test
    fun badDateStringsGiveNull() {
        assertNull("".dateToUtcMillis())
        assertNull("abc".dateToUtcMillis())
        assertNull("2026-13-40".dateToUtcMillis())
        assertNull("29/09/2026".dateToUtcMillis())
    }

    @Test
    fun utcMillisFormatBackToTheSameDate() {
        assertEquals("2000-01-01", 946_684_800_000L.utcMillisToDate())
        assertEquals("1999-12-31", 946_684_799_999L.utcMillisToDate())
        assertEquals("2026-09-29", "2026-09-29".dateToUtcMillis()!!.utcMillisToDate())
    }

    @Test
    fun yearIsReadInUtc() {
        assertEquals(2000, 946_684_800_000L.utcYear())
        assertEquals(1999, 946_684_799_999L.utcYear())
    }

    @Test
    fun todayUtcMidnightHasNoTimeOfDay() {
        val today = todayUtcMidnightMillis()
        assertEquals(0L, today % dayMillis)
    }

    @Test
    fun timeStringParsesToHourAndMinute() {
        assertEquals(9 to 5, "09:05".toHourMinute())
        assertEquals(0 to 0, "00:00".toHourMinute())
        assertEquals(23 to 59, "23:59".toHourMinute())
    }

    @Test
    fun badTimeStringsGiveNull() {
        assertNull("".toHourMinute())
        assertNull("24:00".toHourMinute())
        assertNull("12:60".toHourMinute())
        assertNull("9:5".toHourMinute())
        assertNull("noon".toHourMinute())
    }

    @Test
    fun timeIsZeroPadded() {
        assertEquals("07:05", formatHourMinute(7, 5))
        assertEquals("23:59", formatHourMinute(23, 59))
    }

    @Test
    fun dateTimeStringSplitsIntoParts() {
        val parts = assertNotNull("2026-09-29 14:30".toDateTimeParts())
        assertEquals("2026-09-29".dateToUtcMillis(), parts.dateUtcMillis)
        assertEquals(14, parts.hour)
        assertEquals(30, parts.minute)
        assertEquals("2026-09-29 14:30", formatDateTime(parts.dateUtcMillis, parts.hour, parts.minute))
    }

    @Test
    fun badDateTimeStringsGiveNull() {
        assertNull("".toDateTimeParts())
        assertNull("2026-09-29".toDateTimeParts())
        assertNull("2026-09-29 25:00".toDateTimeParts())
        assertNull("nope 14:30".toDateTimeParts())
    }

    @Test
    fun displayPatternIsAppliedInUtc() {
        // Locale-aware month names come from the platform, so only the numeric parts are asserted here.
        val text = formatUtcMillisPattern("2026-09-29".dateToUtcMillis()!! + 14 * 3_600_000L + 30 * 60_000L, "yyyy-MM-dd HH:mm")
        assertEquals("2026-09-29 14:30", text)
        assertTrue(formatUtcMillisPattern(0L, "dd MMM yyyy").startsWith("01 "))
    }

    @Test
    fun todayIsTheLocalDayNotTheUtcDay() {
        // 2026-09-28 22:00 UTC is already 2026-09-29 in Tanzania (UTC+3).
        val instant = "2026-09-28".dateToUtcMillis()!! + 22 * 3_600_000L
        assertEquals("2026-09-29".dateToUtcMillis(), localDayAsUtcMillis(instant, TimeZone.of("Africa/Dar_es_Salaam")))
        assertEquals("2026-09-28".dateToUtcMillis(), localDayAsUtcMillis(instant, TimeZone.UTC))
        assertEquals("2026-09-28".dateToUtcMillis(), localDayAsUtcMillis(instant, TimeZone.of("America/New_York")))
    }
}
