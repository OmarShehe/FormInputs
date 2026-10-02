package com.omarshehe.forminput.compose.ui

import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.preview
import com.omarshehe.forminput.compose.resources.zoom_out
import com.omarshehe.forminput.compose.resources.zoom_in
import com.omarshehe.forminput.compose.resources.close
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.omarshehe.forminput.compose.ui.utils.Dimens

private const val MIN_ZOOM = 1f
private const val MAX_ZOOM = 5f
private const val ZOOM_STEP = 1f

/** Full-screen zoomable image viewer — Compose Desktop counterpart of
 * `app/androidApp/.../ui/components/image/FullscreenImageViewer.kt` (which is touch-gesture-based
 * via `detectTransformGestures` and lives outside shared code, so it can't be referenced directly).
 * Desktop-native interaction: manual zoom-in/zoom-out buttons (trackpad pinch isn't reliably
 * delivered as a Compose Desktop pointer gesture, so buttons are the primary control), scroll-wheel
 * zoom as a secondary input, click-drag pan when zoomed, double-click to toggle zoom, and a close
 * control.
 *
 * Rendered as plain composable content — **not** wrapped in its own `Dialog`/`Popup` window. It must
 * be composed from the caller's own `overlayContent`-equivalent slot (e.g. `AppDialogWindow`'s
 * `overlayContent`), which already stacks this on top of that dialog's own content, full-screen,
 * within the same native window. An earlier version wrapped this in `androidx.compose.ui.window.
 * Dialog(usePlatformDefaultWidth = false)`, which opens a second native window on desktop; dismissing
 * that second window could also close the owning `AppDialogWindow`. Rendering as a plain overlay
 * avoids a second window entirely, so there's no window to interact badly with the owner. */
@Composable
public fun ZoomableImageViewer(imageUrl: String, onDismiss: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    fun zoomBy(delta: Float) {
        scale = (scale + delta).coerceIn(MIN_ZOOM, MAX_ZOOM)
        if (scale <= MIN_ZOOM) offset = Offset.Zero
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 3f
                        }
                    },
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { _, drag ->
                    if (scale > 1f) offset += drag
                }
            },
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = formInputString(FormInputStrings::preview, Res.string.preview),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                )
                .onPointerEventScroll { deltaY -> zoomBy(-deltaY * 0.1f) },
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Dimens.twoGrid)
                .padding(top = Dimens.threeGrid),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            shape = CircleShape,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = formInputString(FormInputStrings::close, Res.string.close),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(Dimens.threeGrid),
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(Dimens.threeGrid),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            shape = RoundedCornerShape(Dimens.oneGrid),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { zoomBy(-ZOOM_STEP) }, enabled = scale > MIN_ZOOM) {
                    Icon(
                        imageVector = Icons.Default.ZoomOut,
                        contentDescription = formInputString(FormInputStrings::zoomOut, Res.string.zoom_out),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                IconButton(onClick = { zoomBy(ZOOM_STEP) }, enabled = scale < MAX_ZOOM) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = formInputString(FormInputStrings::zoomIn, Res.string.zoom_in),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

/** Scroll-wheel zoom is desktop-only input (no touch pinch on this platform) — isolated behind this
 * small `onPointerEvent` wrapper so the gesture math above stays readable. */
private fun Modifier.onPointerEventScroll(onScroll: (deltaY: Float) -> Unit): Modifier =
    this.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (event.type == PointerEventType.Scroll) {
                    val deltaY = event.changes.firstOrNull()?.scrollDelta?.y ?: continue
                    onScroll(deltaY)
                }
            }
        }
    }
