package com.omarshehe.forminput.compose.ui.utils.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.SamplingMode
import org.jetbrains.skia.Surface
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

@Composable
public actual fun rememberImageBitmap(
    path: String,
    maxWidth: Int?,
    maxHeight: Int?,
): ImageBitmap? =
    remember(path, maxWidth, maxHeight) {
        try {
            val bytes = NSData.dataWithContentsOfFile(path)?.toByteArray() ?: return@remember null
            val image = Image.makeFromEncoded(bytes)
            if (maxWidth != null && maxHeight != null) {
                val scale = minOf(maxWidth.toFloat() / image.width, maxHeight.toFloat() / image.height)
                if (scale < 1.0f) {
                    val newWidth = (image.width * scale).toInt()
                    val newHeight = (image.height * scale).toInt()
                    val surface = Surface.makeRasterN32Premul(newWidth, newHeight)
                    surface.canvas.drawImageRect(
                        image,
                        Rect.makeWH(image.width.toFloat(), image.height.toFloat()),
                        Rect.makeWH(newWidth.toFloat(), newHeight.toFloat()),
                        SamplingMode.DEFAULT,
                        null,
                        true,
                    )
                    return@remember surface.makeImageSnapshot().toComposeImageBitmap()
                }
            }
            image.toComposeImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val result = ByteArray(size)
    if (size > 0) result.usePinned { memcpy(it.addressOf(0), bytes, length) }
    return result
}
