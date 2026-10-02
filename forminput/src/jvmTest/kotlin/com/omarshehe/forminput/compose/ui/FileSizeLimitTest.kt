package com.omarshehe.forminput.compose.ui

import com.omarshehe.forminput.compose.ui.model.FormInputFileState
import com.omarshehe.forminput.compose.ui.model.FormInputImageState
import com.omarshehe.forminput.compose.ui.model.FormInputMultiFileState
import com.omarshehe.forminput.compose.ui.utils.FileUtils
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FileSizeLimitTest {
    @Test
    fun noLimitAcceptsAnySize() {
        assertFalse(FileUtils.isTooLarge(size = Long.MAX_VALUE, maxSize = null))
    }

    @Test
    fun aFileExactlyAtTheLimitIsAccepted() {
        assertFalse(FileUtils.isTooLarge(size = 1_000, maxSize = 1_000))
    }

    @Test
    fun oneByteOverTheLimitIsRejected() {
        assertTrue(FileUtils.isTooLarge(size = 1_001, maxSize = 1_000))
    }

    @Test
    fun oneByteUnderTheLimitIsAccepted() {
        assertFalse(FileUtils.isTooLarge(size = 999, maxSize = 1_000))
    }

    @Test
    fun anUnknownSizeIsAcceptedBecauseItCannotBeJudged() {
        assertFalse(FileUtils.isTooLarge(size = null, maxSize = 1_000))
    }

    @Test
    fun aZeroLimitRejectsAnythingButAnEmptyFile() {
        assertFalse(FileUtils.isTooLarge(size = 0, maxSize = 0))
        assertTrue(FileUtils.isTooLarge(size = 1, maxSize = 0))
    }

    @Test
    fun theThreeUploadStatesHaveNoLimitByDefault() {
        assertNull(FormInputFileState(id = "f", labelRes = null, placeholderRes = null).maxFileSizeBytes)
        assertNull(FormInputMultiFileState(id = "m").maxFileSizeBytes)
        assertNull(FormInputImageState(id = "i", labelRes = null, placeholderRes = null).maxFileSizeBytes)
    }

    @Test
    fun theLimitSurvivesACopy() {
        val state = FormInputFileState(id = "f", labelRes = null, placeholderRes = null, maxFileSizeBytes = 5_000)
        assertEquals(5_000L, state.copy(label = "x").maxFileSizeBytes)
    }

    // ── the new texts exist in both languages ─────────────────────────────────

    private fun keys(path: String): Set<String> =
        Regex("""<string name="([^"]+)"""").findAll(File(path).readText()).map { it.groupValues[1] }.toSet()

    @Test
    fun everyStringExistsInEnglishAndSwahili() {
        val english = keys("src/commonMain/composeResources/values/strings.xml")
        val swahili = keys("src/commonMain/composeResources/values-sw/strings.xml")
        assertEquals(english, swahili, "keys missing from one language: ${(english - swahili) + (swahili - english)}")
        assertTrue("error_file_too_large" in english)
    }
}
