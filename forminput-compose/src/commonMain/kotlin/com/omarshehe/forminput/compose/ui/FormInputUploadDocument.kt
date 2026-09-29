package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.TextFieldColors
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.composables.formInputContainer
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.calculating
import com.omarshehe.forminput.compose.resources.click_to_upload
import com.omarshehe.forminput.compose.resources.delete
import com.omarshehe.forminput.compose.resources.ic_image
import com.omarshehe.forminput.compose.resources.ic_other
import com.omarshehe.forminput.compose.resources.ic_pdf
import com.omarshehe.forminput.compose.resources.unknown_file
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.model.FormInputFileState
import com.omarshehe.forminput.compose.ui.model.FormInputFileType
import com.omarshehe.forminput.compose.ui.model.FormInputType
import com.omarshehe.forminput.compose.ui.composables.resolvedLabel
import com.omarshehe.forminput.compose.ui.composables.resolvedPlaceholder
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.FilePicker
import com.omarshehe.forminput.compose.ui.utils.FileUtils
import com.omarshehe.forminput.compose.ui.utils.Symbols

@Composable
fun FormInputUploadDocument(
    state: FormInputFileState,
    onValueChange: (FormInputFileState) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    colors: TextFieldColors? = null,
) {
    val boxShape = shape ?: LocalFormInputDefaults.current.shape ?: RoundedCornerShape(Dimens.oneGrid)
    var showFilePicker by remember { mutableStateOf(false) }
    val isSelected = state.value != null

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    FilePicker(
        show = showFilePicker,
        extensions = state.allowedExtensions.ifEmpty { FormInputFileType.PDF.extensions },
        onFileSelected = { filePath, fileName, fileSize, error ->
            showFilePicker = false
            filePath?.let {
                onValueChange(
                    state.copy(
                        value = FormInputFileState.FileUploadValue(
                            filePath = it,
                            fileName = fileName,
                            fileSize = fileSize?.let { size -> FileUtils.formatFileSize(size) },
                        ),
                    ),
                )
            }
            error?.let { scope.launch { snackbarHostState.showSnackbar(it) } }
        },
    )

    // An outline is cut by the label sitting on it; a filled box has nothing to cut, so its label goes above it.
    val labelAbove = (style ?: LocalFormInputDefaults.current.style) == FormInputFieldStyle.FILLED && state.resolvedLabel() != null
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = if (labelAbove) Dimens.threeGrid else Dimens.default)
                .fillMaxWidth()
                .height(Dimens.sevenGrid)
                .formInputContainer(
                    shape = boxShape,
                    style = style,
                    colors = colors,
                    hasError = state.hasError,
                    defaultBorder = colorScheme.outlineVariant,
                    defaultBackground = colorScheme.surface,
                ),
        ) {
            if (!isSelected) {
                IdleState(
                    state = state,
                    onClick = { showFilePicker = true },
                )
            } else {
                FileSelectedState(
                    state = state,
                    onDeleteClick = { onValueChange(state.copy(value = null)) },
                )
            }
        }

        state.resolvedLabel()?.let { label ->
    Text(
                text = if (state.isMandatory) "$label${Symbols.MANDATORY_SYMBOL}" else label,
                style = typography.labelMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = if (labelAbove) {
                    Modifier.padding(start = Dimens.oneAndHalfGrid)
                } else {
                    Modifier
                        .padding(start = Dimens.oneAndHalfGrid)
                        .offset(y = -(Dimens.oneGrid))
                        .background(colorScheme.surface)
                        .padding(horizontal = Dimens.halfGrid)
                },
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun IdleState(
    state: FormInputFileState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxSize().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        val painter = state.icon?.let { rememberVectorPainter(it) } ?: rememberVectorPainter(Icons.Default.AttachFile)
        Icon(
            painter = painter,
            contentDescription = null,
            tint = if (state.hasError) colorScheme.error else colorScheme.primary,
            modifier = Modifier.size(Dimens.threeGrid),
        )
        Spacer(Modifier.width(Dimens.oneGrid))
        Text(
            state.resolvedPlaceholder() ?: formInputString(FormInputStrings::clickToUpload, Res.string.click_to_upload),
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FileSelectedState(
    state: FormInputFileState,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fileValue = state.value
    Row(
        modifier = modifier.padding(Dimens.oneGrid),
        verticalAlignment = Alignment.Top,
    ) {
        FileIconWithBadge(
            fileName = fileValue?.fileName,
            modifier = Modifier.size(Dimens.sixGrid).padding(Dimens.halfGrid),
        )

        Spacer(Modifier.width(Dimens.halfGrid))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = fileValue?.fileName ?: formInputString(FormInputStrings::unknownFile, Res.string.unknown_file),
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = colorScheme.onSurface,
                )
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(Dimens.threeGrid),
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = formInputString(FormInputStrings::delete, Res.string.delete),
                        tint = colorScheme.onSurface,
                        modifier = Modifier.size(Dimens.twoAndHalfGrid),
                    )
                }
            }

            val statusText = buildString {
                fileValue?.fileSize?.let { append(it) }
                if (state.statusRes != null) {
                    if (isNotEmpty()) append(Symbols.SEPARATOR)
                    append(stringResource(state.statusRes))
                }
            }
            if (statusText.isNotEmpty()) {
                Text(
                    text = statusText,
                    style = typography.bodySmall,
                    color = colorScheme.onSurface,
                )
            }

            state.progress?.let { progress ->
                Spacer(Modifier.height(Dimens.oneGrid))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.stroke * 6)
                            .clip(RoundedCornerShape(Dimens.stroke * 3)),
                        color = colorScheme.primary,
                        trackColor = colorScheme.surfaceVariant,
                    )
                    Spacer(Modifier.width(Dimens.oneGrid))
                    Text(
                        text = "${(progress * 100).toInt()}${Symbols.PERCENT}",
                        style = typography.labelSmall,
                        color = colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun FileIconWithBadge(
    fileName: String?,
    modifier: Modifier = Modifier,
) {
    val extension = fileName?.substringAfterLast(Symbols.DOT, Symbols.EMPTY)?.lowercase() ?: Symbols.EMPTY
    val fileType = FormInputFileType.fromExtension(extension)
    Box(modifier = modifier) {
        val iconRes = when (fileType) {
            FormInputFileType.PDF -> Res.drawable.ic_pdf
            FormInputFileType.IMAGE -> Res.drawable.ic_image
            else -> Res.drawable.ic_other
        }
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = null,
        )
        if (extension.isNotEmpty()) {
            Surface(
                color = colorScheme.primary,
                shape = RoundedCornerShape(Dimens.quarterGrid),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(bottom = Dimens.oneGrid),
            ) {
                Text(
                    text = extension,
                    color = colorScheme.onPrimary,
                    fontSize = Dimens.labelFontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = Dimens.quarterGrid, vertical = Dimens.stroke),
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FormInputUploadDocumentPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(Dimens.twoGrid)) {
            FormInputUploadDocument(
                state = FormInputFileState(
                    id = "id_card",
                    labelRes = Res.string.calculating,
                    placeholderRes = null,
                    type = FormInputType.FILE_UPLOAD,
                    isMandatory = true,
                    value = FormInputFileState.FileUploadValue(fileName = "id_card.pdf", filePath = "id_card.pdf"),
                ),
                onValueChange = { },
            )
        }
    }
}
