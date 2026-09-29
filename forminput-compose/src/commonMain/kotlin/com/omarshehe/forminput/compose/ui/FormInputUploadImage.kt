package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.delete
import com.omarshehe.forminput.compose.resources.image_primary_badge
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.model.FormInputFileType
import com.omarshehe.forminput.compose.ui.model.FormInputImageState
import com.omarshehe.forminput.compose.ui.model.FormInputImageState.ImageUploadValue
import com.omarshehe.forminput.compose.ui.model.FormInputState
import com.omarshehe.forminput.compose.ui.model.withImageRemoved
import com.omarshehe.forminput.compose.ui.model.withPickedImage
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.FilePicker
import com.omarshehe.forminput.compose.ui.utils.Symbols
import com.omarshehe.forminput.compose.ui.utils.image.rememberImageBitmap

/** Lets a caller substitute its own image source (e.g. a camera) for the widget's internal [FilePicker]; call with `null` path to cancel. */
typealias ImageCaptureRequester = (
    onResult: (path: String?, name: String?, size: Long?, error: String?) -> Unit,
) -> Unit

/** Distinguishes "replace this fixed slot" from "append a new item" (unbounded-add mode). */
private sealed interface PendingTarget {
    data object New : PendingTarget

    data class Existing(val index: Int) : PendingTarget
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormInputUploadImage(
    state: FormInputImageState,
    onValueChange: (FormInputState) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteImage: (String) -> Unit = {},
    unboundedAdd: Boolean = false,
    maxItems: Int? = null,
    showPrimaryBadge: Boolean = true,
    captureRequester: ImageCaptureRequester? = null,
    onImageClick: ((url: String) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var pendingTarget by remember { mutableStateOf<PendingTarget?>(null) }

    fun applyResult(
        target: PendingTarget,
        filePath: String?,
        fileName: String?,
        fileSize: Long?,
        error: String?,
    ) {
        if (filePath != null) {
            val targetIndex = (target as? PendingTarget.Existing)?.index
            onValueChange(state.withPickedImage(targetIndex = targetIndex, filePath = filePath, fileName = fileName, fileSize = fileSize))
        }
        error?.let { scope.launch { snackbarHostState.showSnackbar(it) } }
    }

    fun requestImage(target: PendingTarget) {
        val requester = captureRequester
        if (requester != null) {
            requester { path, name, size, error -> applyResult(target, path, name, size, error) }
        } else {
            pendingTarget = target
        }
    }

    fun removeAt(index: Int) {
        onValueChange(state.withImageRemoved(index = index, unboundedAdd = unboundedAdd))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        state.labelRes?.let {
            val label = stringResource(it)

            Text(
                text = if (state.isMandatory) {
                    "$label${Symbols.MANDATORY_SYMBOL}"
                } else {
                    label
                },
                style = typography.labelMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = Dimens.halfGrid),
            )
        }

        Box {
            if (unboundedAdd) {
                // Fixed-size slots that wrap to further rows — count is arbitrary/growing.
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.twoGrid),
                    verticalArrangement = Arrangement.spacedBy(Dimens.twoGrid),
                ) {
                    state.values.forEachIndexed { index, value ->
                        if (value != null) {
                            FilledImageSlot(
                                index = index,
                                value = value,
                                sizeModifier = Modifier.size(Dimens.imageSlotSize),
                                showPrimaryBadge = showPrimaryBadge,
                                onDeleteImage = onDeleteImage,
                                onRemove = ::removeAt,
                                onImageClick = onImageClick,
                            )
                        }
                    }
                    if (maxItems == null || state.values.size < maxItems) {
                        ImageSlotContainer(sizeModifier = Modifier.size(Dimens.imageSlotSize)) {
                            EmptyImage { requestImage(PendingTarget.New) }
                        }
                    }
                }
            } else {
                // Known slot count — sized as an even fraction of the row width, fills edge-to-edge.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.twoGrid),
                ) {
                    state.values.forEachIndexed { slotIndex, value ->
                        val slotModifier = Modifier.weight(1f).aspectRatio(1f).widthIn(min = Dimens.tenGrid, max = Dimens.imageSlotSize)
                        if (value == null) {
                            ImageSlotContainer(sizeModifier = slotModifier) {
                                EmptyImage { requestImage(PendingTarget.Existing(slotIndex)) }
                            }
                        } else {
                            FilledImageSlot(
                                index = slotIndex,
                                value = value,
                                sizeModifier = slotModifier,
                                showPrimaryBadge = showPrimaryBadge,
                                onDeleteImage = onDeleteImage,
                                onRemove = ::removeAt,
                            )
                        }
                    }
                }
            }

            FilePicker(
                show = pendingTarget != null,
                extensions = state.allowedExtensions.ifEmpty {
                    FormInputFileType.IMAGE.extensions
                },
                onFileSelected = { filePath, fileName, fileSize, error ->
                    val target = pendingTarget
                    pendingTarget = null
                    if (target != null) applyResult(target, filePath, fileName, fileSize, error)
                },
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

/** The one construction shared by both the fixed-slot and unbounded-add render branches. */
@Composable
private fun FilledImageSlot(
    index: Int,
    value: ImageUploadValue,
    sizeModifier: Modifier,
    showPrimaryBadge: Boolean,
    onDeleteImage: (String) -> Unit,
    onRemove: (Int) -> Unit,
    onImageClick: ((url: String) -> Unit)? = null,
) {
    ImageSlotContainer(sizeModifier = sizeModifier) {
        ImageSlot(
            value = value,
            progress = value.progress,
            isPrimary = showPrimaryBadge && index == 0,
            onDeleteClick = {
                value.url?.let(onDeleteImage)
                onRemove(index)
            },
            onImageClick = onImageClick,
        )
    }
}

@Composable
private fun ImageSlotContainer(
    sizeModifier: Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = sizeModifier
            .clip(RoundedCornerShape(Dimens.oneGrid))
            .border(
                width = Dimens.stroke,
                color = colorScheme.outlineVariant,
                shape = RoundedCornerShape(Dimens.oneGrid),
            ),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
private fun EmptyImage(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(Dimens.fourGrid),
        )

        Spacer(Modifier.height(Dimens.oneGrid))
    }
}

/** Previews `value.url` via Coil when present (no local-file fetcher configured — see TechnovationsAgentToolbox's kmp/desktop.mdc "Image loading"), else the local `filePath`. */
@Composable
private fun BoxScope.ImageSlot(
    value: ImageUploadValue,
    progress: Float?,
    isPrimary: Boolean,
    onDeleteClick: () -> Unit,
    onImageClick: ((url: String) -> Unit)? = null,
) {
    val url = value.url
    val hasUrl = !url.isNullOrBlank()
    val isPdf = url?.endsWith(".pdf", ignoreCase = true) == true
    val bitmap = if (hasUrl) null else rememberImageBitmap(value.filePath.orEmpty())
    // hasUrl, not "did Coil's load actually succeed" — a transient load failure must not permanently
    // hide the delete button (Coil doesn't retry a failed load on its own).
    val hasImage = hasUrl || bitmap != null
    val clickModifier = if (hasUrl && onImageClick != null) Modifier.clickable { onImageClick(url) } else Modifier

    if (isPdf) {
        Box(modifier = Modifier.fillMaxSize().then(clickModifier), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimens.fourGrid),
            )
        }
    } else if (hasUrl) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().then(clickModifier),
            contentScale = ContentScale.Crop,
        )
    } else if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }

    if (hasImage) {
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(Dimens.oneGrid)
                .background(
                    color = colorScheme.surface.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(Dimens.halfGrid),
                )
                .size(Dimens.threeAndHalfGrid),
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(Res.string.delete),
                tint = colorScheme.onSurface,
                modifier = Modifier.size(Dimens.twoAndHalfGrid),
            )
        }

        if (isPrimary) {
            Text(
                text = stringResource(Res.string.image_primary_badge),
                color = colorScheme.onPrimaryContainer,
                style = typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Dimens.oneGrid)
                    .background(
                        colorScheme.primaryContainer,
                        RoundedCornerShape(Dimens.halfGrid),
                    )
                    .padding(horizontal = Dimens.oneGrid, vertical = Dimens.quarterGrid),
            )
        }

        if (progress != null && progress < 1f) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(Dimens.halfGrid),
                color = colorScheme.primary,
                trackColor = colorScheme.primaryContainer.copy(alpha = 0.5f),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FormInputUploadImagePreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(Dimens.twoGrid)) {
            FormInputUploadImage(
                state = FormInputImageState(
                    id = "car_images",
                    labelRes = null,
                    placeholderRes = null,
                    values = mutableListOf(null, null, null),
                    isMandatory = true,
                ),
                onValueChange = { },
                onDeleteImage = { },
            )
        }
    }
}
