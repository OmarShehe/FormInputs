package com.omarshehe.forminput.compose.ui.model

import com.omarshehe.forminput.compose.ui.utils.Symbols

public enum class FormInputFileType(public val extensions: List<String>) {
    PDF(listOf(Symbols.PDF)),
    IMAGE(listOf(Symbols.JPG, Symbols.PNG, Symbols.JPEG, Symbols.WEBP, Symbols.GIF)),
    CSV(listOf(Symbols.CSV)),
    WORD(listOf(Symbols.DOC, Symbols.DOCX)),
    POWERPOINT(listOf(Symbols.PPT, Symbols.PPTX)),
    AUDIO(listOf(Symbols.MP3, Symbols.WAV, Symbols.M4A, Symbols.AAC, Symbols.OGG, Symbols.FLAC)),
    OTHER(emptyList()),
    ;

    public companion object {
        public fun fromExtension(extension: String): FormInputFileType =
            when (extension.lowercase()) {
                Symbols.PDF -> PDF
                Symbols.JPG, Symbols.PNG, Symbols.JPEG, Symbols.WEBP, Symbols.GIF -> IMAGE
                Symbols.CSV -> CSV
                Symbols.DOC, Symbols.DOCX -> WORD
                Symbols.PPT, Symbols.PPTX -> POWERPOINT
                Symbols.MP3, Symbols.WAV, Symbols.M4A, Symbols.AAC, Symbols.OGG, Symbols.FLAC -> AUDIO
                else -> OTHER
            }
    }
}
