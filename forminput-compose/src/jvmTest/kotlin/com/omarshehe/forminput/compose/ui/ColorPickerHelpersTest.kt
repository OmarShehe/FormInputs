package com.omarshehe.forminput.compose.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ColorPickerHelpersTest {
    // ── hsvToHex ─────────────────────────────────────────────────────────────

    @Test
    fun `hsvToHex red`() {
        assertEquals("#FF0000", hsvToHex(0f, 1f, 1f))
    }

    @Test
    fun `hsvToHex white`() {
        assertEquals("#FFFFFF", hsvToHex(0f, 0f, 1f))
    }

    @Test
    fun `hsvToHex black`() {
        assertEquals("#000000", hsvToHex(0f, 0f, 0f))
    }

    @Test
    fun `hsvToHex green`() {
        assertEquals("#00FF00", hsvToHex(120f, 1f, 1f))
    }

    @Test
    fun `hsvToHex blue`() {
        assertEquals("#0000FF", hsvToHex(240f, 1f, 1f))
    }

    @Test
    fun `hsvToHex output is always 7 chars uppercase`() {
        val result = hsvToHex(200f, 0.5f, 0.8f)
        assertTrue(result.startsWith("#"))
        assertEquals(7, result.length)
        assertEquals(result, result.uppercase())
    }

    // ── hexToHsv ─────────────────────────────────────────────────────────────

    @Test
    fun `hexToHsv red`() {
        val hsv = hexToHsv("#FF0000")
        assertNotNull(hsv)
        assertEquals(0f, hsv[0], 0.5f)
        assertEquals(1f, hsv[1], 0.01f)
        assertEquals(1f, hsv[2], 0.01f)
    }

    @Test
    fun `hexToHsv white has zero saturation`() {
        val hsv = hexToHsv("#FFFFFF")
        assertNotNull(hsv)
        assertEquals(0f, hsv[1], 0.01f)
        assertEquals(1f, hsv[2], 0.01f)
    }

    @Test
    fun `hexToHsv black has zero value`() {
        val hsv = hexToHsv("#000000")
        assertNotNull(hsv)
        assertEquals(0f, hsv[2], 0.01f)
    }

    @Test
    fun `hexToHsv returns null when missing hash`() {
        assertNull(hexToHsv("FF0000"))
    }

    @Test
    fun `hexToHsv returns null for short hex`() {
        assertNull(hexToHsv("#FF00"))
    }

    @Test
    fun `hexToHsv returns null for invalid chars`() {
        assertNull(hexToHsv("#GGGGGG"))
    }

    @Test
    fun `hexToHsv returns null for empty string`() {
        assertNull(hexToHsv(""))
    }

    // ── isValidHex ────────────────────────────────────────────────────────────

    @Test
    fun `isValidHex accepts valid 7-char hex`() {
        assertTrue(isValidHex("#FF0000"))
        assertTrue(isValidHex("#000000"))
        assertTrue(isValidHex("#FFFFFF"))
        assertTrue(isValidHex("#aabbcc"))
    }

    @Test
    fun `isValidHex rejects missing hash`() {
        assertFalse(isValidHex("FF0000"))
    }

    @Test
    fun `isValidHex rejects short hex`() {
        assertFalse(isValidHex("#FF00"))
    }

    @Test
    fun `isValidHex rejects invalid chars`() {
        assertFalse(isValidHex("#GGGGGG"))
    }

    @Test
    fun `isValidHex rejects empty`() {
        assertFalse(isValidHex(""))
    }
}
