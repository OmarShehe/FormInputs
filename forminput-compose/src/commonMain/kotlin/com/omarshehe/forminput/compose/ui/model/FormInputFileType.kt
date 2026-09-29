package com.omarshehe.forminput.compose.ui.model

import com.omarshehe.forminput.compose.ui.utils.Symbols

enum class FormInputFileType(val extensions: List<String>) {
    PDF(listOf(Symbols.PDF)),
    IMAGE(listOf(Symbols.JPG, Symbols.PNG, Symbols.JPEG, Symbols.WEBP, Symbols.GIF)),
    OTHER(emptyList()),
    ;

    companion object {
        fun fromExtension(extension: String): FormInputFileType =
            when (extension.lowercase()) {
                Symbols.PDF -> PDF
                Symbols.JPG, Symbols.PNG, Symbols.JPEG, Symbols.WEBP, Symbols.GIF -> IMAGE
                else -> OTHER
            }
    }
}
