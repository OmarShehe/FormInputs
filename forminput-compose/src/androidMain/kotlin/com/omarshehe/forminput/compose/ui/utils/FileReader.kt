package com.omarshehe.forminput.compose.ui.utils

import android.content.Context
import android.net.Uri
import java.io.File

/** The file picker hands back a `content://` URI as the "path"; only a ContentResolver can read that. */
internal fun readFileBytes(context: Context, path: String): ByteArray {
    if (path.startsWith("content://")) {
        return context.contentResolver.openInputStream(Uri.parse(path))?.use { it.readBytes() }
            ?: error("Unable to open content URI: $path")
    }
    return File(path).readBytes()
}
