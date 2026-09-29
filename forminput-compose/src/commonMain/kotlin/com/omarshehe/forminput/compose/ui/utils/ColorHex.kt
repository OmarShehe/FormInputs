package com.omarshehe.forminput.compose.ui.utils

internal fun rgbToHex(r: Int, g: Int, b: Int): String = "#${hexByte(r)}${hexByte(g)}${hexByte(b)}"

private fun hexByte(value: Int): String = value.coerceIn(0, 255).toString(16).uppercase().padStart(2, '0')
