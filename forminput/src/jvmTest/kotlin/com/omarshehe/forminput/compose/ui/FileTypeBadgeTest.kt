package com.omarshehe.forminput.compose.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.omarshehe.forminput.compose.ui.utils.FormInputTestTags
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.ui.model.FormInputFileState
import com.omarshehe.forminput.compose.ui.model.FormInputFileType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** The extension badge names the type only where the icon cannot: PDF and image icons already say it. */
@OptIn(ExperimentalTestApi::class)
class FileTypeBadgeTest {
    private fun uploaded(fileName: String) = FormInputFileState(
        id = "f", labelRes = null, placeholderRes = null,
        value = FormInputFileState.FileUploadValue(filePath = "/tmp/$fileName", fileName = fileName),
    )

    @Test
    fun pdfIconHasNoBadge() = runComposeUiTest {
        setContent { FormInputUploadDocument(state = uploaded("report.pdf"), onValueChange = {}) }
        onNodeWithText("report.pdf").assertExists()
        onNodeWithText("pdf").assertDoesNotExist()
    }

    @Test
    fun imageIconHasNoBadge() = runComposeUiTest {
        setContent { FormInputUploadDocument(state = uploaded("photo.png"), onValueChange = {}) }
        onNodeWithText("photo.png").assertExists()
        onNodeWithText("png").assertDoesNotExist()
    }

    @Test
    fun genericIconKeepsItsBadge() = runComposeUiTest {
        setContent { FormInputUploadDocument(state = uploaded("sheet.xlsx"), onValueChange = {}) }
        onNodeWithText("xlsx").assertExists()
    }

    @Test
    fun badgeShowsForUppercaseGenericExtensionInLowercase() = runComposeUiTest {
        setContent { FormInputUploadDocument(state = uploaded("NOTES.TXT"), onValueChange = {}) }
        onNodeWithText("txt").assertExists()
    }

    @Test
    fun theBadgeIsPlainTextWithoutAFilledBackground() = runComposeUiTest {
        val colors = lightColorScheme()
        setContent {
            MaterialTheme(colorScheme = colors) {
                FormInputUploadDocument(state = uploaded("sheet.xlsx"), onValueChange = {})
            }
        }
        // The old badge filled its box with the primary colour; the corner of the badge's box must not be that colour any more.
        val corner = onNodeWithText("xlsx").captureToImage().toPixelMap()[0, 0]
        assertNotEquals(colors.primary, corner)
    }

    @Test
    fun csvIsItsOwnFileTypeWithItsOwnIcon() {
        assertEquals(FormInputFileType.CSV, FormInputFileType.fromExtension("csv"))
        assertEquals(FormInputFileType.CSV, FormInputFileType.fromExtension("CSV"))
        assertEquals(listOf("csv"), FormInputFileType.CSV.extensions)
    }

    @Test
    fun csvIconHasNoBadge() = runComposeUiTest {
        setContent { FormInputUploadDocument(state = uploaded("export.csv"), onValueChange = {}) }
        onNodeWithText("export.csv").assertExists()
        onNodeWithText("csv").assertDoesNotExist()
    }

    @Test
    fun theBadgeSitsInsideTheIconBody() = runComposeUiTest {
        setContent {
            androidx.compose.foundation.layout.Box(androidx.compose.ui.Modifier.testTag("host")) {
                FormInputUploadDocument(state = uploaded("sheet.xlsx"), onValueChange = {})
            }
        }
        val badge = onNodeWithText("xlsx").getBoundsInRoot()
        val icon = onNodeWithTag(FormInputTestTags.FileIcon).getBoundsInRoot()
        // The icon is narrower than its box; the text must start and end within the icon's own width, not hang off its edge.
        val iconBodyWidth = icon.bottom - icon.top
        val bodyLeft = icon.left + (icon.right - icon.left - iconBodyWidth * 20 / 24) / 2
        val bodyRight = bodyLeft + iconBodyWidth * 20 / 24
        assertTrue(badge.left >= bodyLeft - 0.5.dp && badge.right <= bodyRight + 0.5.dp, "badge $badge outside body $bodyLeft..$bodyRight")
    }

    @Test
    fun officeAndAudioExtensionsMapToTheirOwnTypes() {
        listOf("doc", "DOCX").forEach { assertEquals(FormInputFileType.WORD, FormInputFileType.fromExtension(it), it) }
        listOf("ppt", "pptx").forEach { assertEquals(FormInputFileType.POWERPOINT, FormInputFileType.fromExtension(it), it) }
        listOf("mp3", "wav", "m4a", "aac", "ogg", "FLAC").forEach {
            assertEquals(FormInputFileType.AUDIO, FormInputFileType.fromExtension(it), it)
        }
        assertEquals(listOf("doc", "docx"), FormInputFileType.WORD.extensions)
        assertEquals(listOf("ppt", "pptx"), FormInputFileType.POWERPOINT.extensions)
    }

    @Test
    fun wordPowerPointAndAudioIconsHaveNoBadge() {
        listOf("letter.docx" to "docx", "slides.pptx" to "pptx", "song.mp3" to "mp3").forEach { (file, badge) ->
            runComposeUiTest {
                setContent { FormInputUploadDocument(state = uploaded(file), onValueChange = {}) }
                onNodeWithText(file).assertExists()
                onNodeWithText(badge).assertDoesNotExist()
            }
        }
    }
}
