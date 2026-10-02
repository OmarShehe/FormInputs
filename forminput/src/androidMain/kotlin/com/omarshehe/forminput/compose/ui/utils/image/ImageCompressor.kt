package com.omarshehe.forminput.compose.ui.utils.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

const val MAX_IMAGE_DIMENSION_PX = 1920
const val JPEG_UPLOAD_QUALITY = 80

/** Resizes [bitmap] so its longest edge is at most [maxDimension] (returns it unchanged if
 * already smaller — never upscales) and JPEG-encodes the result at [quality] in one pass. Callers
 * that already hold a decoded [Bitmap] (e.g. `CameraCaptureScreen`'s rotation pass) should call
 * this directly instead of round-tripping through [compressImageBytes]. */
fun resizeAndEncodeJpeg(
    bitmap: Bitmap,
    maxDimension: Int = MAX_IMAGE_DIMENSION_PX,
    quality: Int = JPEG_UPLOAD_QUALITY,
): Pair<Bitmap, ByteArray> {
    val longestEdge = maxOf(bitmap.width, bitmap.height)
    val resized = if (longestEdge <= maxDimension) {
        bitmap
    } else {
        val scale = maxDimension.toFloat() / longestEdge
        Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
    }
    val bytes = ByteArrayOutputStream().use { stream ->
        resized.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        stream.toByteArray()
    }
    return resized to bytes
}

/** Decodes [bytes], resizes/re-encodes via [resizeAndEncodeJpeg], and returns just the bytes —
 * for call sites (gallery-picked files) that only have raw bytes, not an already-decoded Bitmap.
 * Returns [bytes] unchanged if they can't be decoded as an image (defensive; callers only invoke
 * this for image uploads, never signatures/documents). */
fun compressImageBytes(
    bytes: ByteArray,
    maxDimension: Int = MAX_IMAGE_DIMENSION_PX,
    quality: Int = JPEG_UPLOAD_QUALITY,
): ByteArray {
    val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return bytes
    return resizeAndEncodeJpeg(decoded, maxDimension, quality).second
}
