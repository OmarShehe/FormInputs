package com.omarshehe.forminput.compose.ui.utils

import com.omarshehe.forminput.compose.ui.model.FormInputFileType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UtilsTest {
    @Test
    fun parsingNumbers() {
        assertEquals(0.0, NumericUtils.parseDouble(""))
        assertEquals(1234.5, NumericUtils.parseDouble("1,234.5"))
        assertNull(NumericUtils.parseDouble("abc"))
        assertEquals(12, NumericUtils.parseInt("12.9"))
        assertEquals(0, NumericUtils.parseInt(" "))
        assertEquals(1234567L, NumericUtils.parseLong("1,234,567"))
    }

    @Test
    fun formattingNumbers() {
        assertEquals("0.00", NumericUtils.formatDecimal(-0.004))
        assertEquals("1234.57", NumericUtils.formatDecimal(1234.567))
        assertEquals("-2.50", NumericUtils.formatDecimal(-2.5))
        assertEquals("1,234,567", NumericUtils.formatInteger(1234567.0))
        assertEquals("-1,500", NumericUtils.formatInteger(-1500.0))
        assertEquals("0", NumericUtils.formatInteger(0.2))
    }

    @Test
    fun fileSizesAreReadable() {
        assertEquals("0 B", FileUtils.formatFileSize(0))
        assertEquals("0 B", FileUtils.formatFileSize(-5))
        assertEquals("1023.0 B", FileUtils.formatFileSize(1023))
        assertEquals("1.5 KB", FileUtils.formatFileSize(1536))
        assertEquals("5.0 MB", FileUtils.formatFileSize(5L * 1024 * 1024))
        assertEquals("1.0 GB", FileUtils.formatFileSize(1024L * 1024 * 1024))
    }

    @Test
    fun emailValidation() {
        assertTrue(FormInputValidators.isValidEmail("omar@example.co.tz"))
        assertTrue(FormInputValidators.isValidEmail("first.last+tag@sub.example.com"))
        assertTrue(FormInputValidators.isValidEmail("a@b.tz"))
        assertFalse(FormInputValidators.isValidEmail(""))
        assertFalse(FormInputValidators.isValidEmail("omar@"))
        assertFalse(FormInputValidators.isValidEmail("omar@example"))
        assertFalse(FormInputValidators.isValidEmail("omar example@x.com"))
        assertFalse(FormInputValidators.isValidEmail("a@b.c"))
    }

    @Test
    fun fileTypeFromExtension() {
        assertEquals(FormInputFileType.PDF, FormInputFileType.fromExtension("PDF"))
        assertEquals(FormInputFileType.IMAGE, FormInputFileType.fromExtension("Png"))
        assertEquals(FormInputFileType.IMAGE, FormInputFileType.fromExtension("jpeg"))
        assertEquals(FormInputFileType.OTHER, FormInputFileType.fromExtension("docx"))
    }

    @Test
    fun hexColorIsUpperCaseAndPadded() {
        assertEquals("#000000", rgbToHex(0, 0, 0))
        assertEquals("#0A0B0C", rgbToHex(10, 11, 12))
        assertEquals("#FFFFFF", rgbToHex(255, 255, 255))
    }
}
