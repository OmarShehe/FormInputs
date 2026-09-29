package com.omarshehe.forminput.compose.ui.utils.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.io.File

@Composable
actual fun rememberImageBitmap(
    path: String,
    maxWidth: Int?,
    maxHeight: Int?,
): ImageBitmap? {
    return remember(path, maxWidth, maxHeight) {
        try {
            val file = File(path)
            if (!file.exists()) return@remember null

            val bytes = file.readBytes()
            val skiaImage = Image.makeFromEncoded(bytes)

            if (maxWidth != null && maxHeight != null) {
                val width = skiaImage.width
                val height = skiaImage.height

                val scale = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)

                if (scale < 1.0f) {
                    val newWidth = (width * scale).toInt()
                    val newHeight = (height * scale).toInt()

                    val surface = org.jetbrains.skia.Surface.makeRasterN32Premul(newWidth, newHeight)
                    val canvas = surface.canvas
                    canvas.drawImageRect(
                        skiaImage,
                        org.jetbrains.skia.Rect.makeWH(width.toFloat(), height.toFloat()),
                        org.jetbrains.skia.Rect.makeWH(newWidth.toFloat(), newHeight.toFloat()),
                        org.jetbrains.skia.SamplingMode.DEFAULT,
                        null,
                        true,
                    )
                    surface.makeImageSnapshot().toComposeImageBitmap()
                } else {
                    skiaImage.toComposeImageBitmap()
                }
            } else {
                skiaImage.toComposeImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }
}
