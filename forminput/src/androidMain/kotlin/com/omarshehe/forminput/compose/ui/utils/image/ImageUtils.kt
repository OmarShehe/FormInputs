package com.omarshehe.forminput.compose.ui.utils.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.omarshehe.forminput.compose.ui.utils.readFileBytes

/** `BitmapFactory.decodeFile` returns null for `content://` paths — read via [readFileBytes] instead. */
@Composable
actual fun rememberImageBitmap(
    path: String,
    maxWidth: Int?,
    maxHeight: Int?,
): ImageBitmap? {
    val context = LocalContext.current
    return remember(path, maxWidth, maxHeight) {
        try {
            val bytes = if (path.startsWith("content://")) readFileBytes(context, path) else null

            fun decode(options: BitmapFactory.Options?): Bitmap? =
                if (bytes != null) {
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                } else {
                    BitmapFactory.decodeFile(path, options)
                }

            if (maxWidth != null && maxHeight != null) {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                decode(options)

                options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
                options.inJustDecodeBounds = false
                decode(options)?.asImageBitmap()
            } else {
                decode(null)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int,
): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
