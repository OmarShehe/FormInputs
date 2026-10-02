package com.omarshehe.forminput.compose.ui

import com.omarshehe.forminput.compose.resources.delete
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.TextFieldColors
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.composables.formInputContainer
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.omarshehe.forminput.compose.ui.utils.FormInputTestTags
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.label_uploaded_documents
import com.omarshehe.forminput.compose.resources.browse_files
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.model.FormInputFileState
import com.omarshehe.forminput.compose.ui.model.FormInputFileType
import com.omarshehe.forminput.compose.ui.model.FormInputMultiFileState
import com.omarshehe.forminput.compose.ui.composables.resolvedPlaceholder
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.FilePicker
import com.omarshehe.forminput.compose.ui.utils.FileUtils

/** The height of the drop area unless the caller's `areaModifier` sets one. */
private val DefaultUploadAreaHeight = 120.dp

/**
 * A picker for several documents, listed under a drop area. The area is 120dp high unless [areaModifier] sets another height
 * (for example `Modifier.height(80.dp)`). A file larger than [FormInputMultiFileState.maxFileSizeBytes] is refused with a message,
 * and a picker error is shown the same way.
 */
@Composable
public fun FormInputUploadMultiDocument(
    state: FormInputMultiFileState,
    onValueChange: (FormInputMultiFileState) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    colors: TextFieldColors? = null,
    areaModifier: Modifier = Modifier,
) {
    val boxShape = shape ?: LocalFormInputDefaults.current.shape ?: RoundedCornerShape(Dimens.oneGrid)
    var showFilePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val tooLargeMessage = fileTooLargeMessage(state.maxFileSizeBytes)

    FilePicker(
        show = showFilePicker,
        extensions = state.allowedExtensions.ifEmpty {
            FormInputFileType.PDF.extensions + FormInputFileType.IMAGE.extensions
        },
        onFileSelected = { filePath, fileName, fileSize, error ->
            showFilePicker = false
            if (FileUtils.isTooLarge(fileSize, state.maxFileSizeBytes)) {
                tooLargeMessage?.let { scope.launch { snackbarHostState.showSnackbar(it) } }
                return@FilePicker
            }
            error?.let { scope.launch { snackbarHostState.showSnackbar(it) } }
            if (filePath != null) {
                val newValue = FormInputFileState.FileUploadValue(
                    filePath = filePath,
                    fileName = fileName,
                    fileSize = fileSize?.let { FileUtils.formatFileSize(it) },
                )
                onValueChange(state.copy(values = state.values + newValue))
            }
        },
    )

    // The snackbar host sits outside the spaced column: a zero-height child there would still add a gap below the area.
    Box(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.twoGrid)) {
            // 1. Upload Area
            Box(
                modifier = areaModifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = DefaultUploadAreaHeight)
                    .testTag(FormInputTestTags.UploadArea)
                    .formInputContainer(
                        shape = boxShape,
                        style = style,
                        colors = colors,
                        hasError = state.hasError,
                        defaultBorder = colorScheme.outlineVariant.copy(alpha = 0.5f),
                        defaultBackground = colorScheme.surfaceVariant.copy(alpha = 0.1f),
                    )
                    .clickable { showFilePicker = true },
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null, // decorative: the visible text next to it names the action
                        tint = colorScheme.primary,
                        modifier = Modifier.size(Dimens.fiveGrid),
                    )
                    Spacer(Modifier.height(Dimens.oneGrid))
                    Text(
                        text = state.resolvedPlaceholder() ?: formInputString(FormInputStrings::browseFiles, Res.string.browse_files),
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface,
                    )
                }
            }

            // 3. File List
            if (state.values.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.oneGrid)) {
                    Text(
                        text = formInputString(FormInputStrings::uploadedDocuments, Res.string.label_uploaded_documents),
                        style = typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurfaceVariant,
                    )

                    state.values.forEachIndexed { index, file ->
                        FileListItem(
                            file = file,
                            shape = boxShape,
                            onDelete = {
                                val newList = state.values.toMutableList().apply { removeAt(index) }
                                onValueChange(state.copy(values = newList))
                            },
                        )
                    }
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun FileListItem(
    file: FormInputFileState.FileUploadValue,
    shape: Shape,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = shape,
    ) {
        Row(
            modifier = Modifier.padding(Dimens.oneAndHalfGrid),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.oneAndHalfGrid),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.InsertDriveFile,
                contentDescription = null, // decorative: the visible text next to it names the action
                tint = colorScheme.primary,
                modifier = Modifier.size(Dimens.threeGrid),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.fileName ?: "Unknown File",
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                )
                file.fileSize?.let {
                    Text(
                        text = it,
                        style = typography.labelSmall,
                        color = colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(Dimens.threeGrid)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = formInputString(FormInputStrings::delete, Res.string.delete),
                    tint = colorScheme.error,
                    modifier = Modifier.size(Dimens.twoGrid),
                )
            }
        }
    }
}
