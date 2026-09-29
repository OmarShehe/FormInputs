package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.label_uploaded_documents
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.model.FormInputFileState
import com.omarshehe.forminput.compose.ui.model.FormInputFileType
import com.omarshehe.forminput.compose.ui.model.FormInputMultiFileState
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.FilePicker
import com.omarshehe.forminput.compose.ui.utils.FileUtils

@Composable
fun FormInputUploadMultiDocument(
    state: FormInputMultiFileState,
    onValueChange: (FormInputMultiFileState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showFilePicker by remember { mutableStateOf(false) }

    FilePicker(
        show = showFilePicker,
        extensions = state.allowedExtensions.ifEmpty {
            FormInputFileType.PDF.extensions + FormInputFileType.IMAGE.extensions
        },
        onFileSelected = { filePath, fileName, fileSize, error ->
            showFilePicker = false
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

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.twoGrid)) {
        // 1. Upload Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(Dimens.oneGrid))
                .border(
                    width = Dimens.stroke,
                    color = colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(Dimens.oneGrid),
                )
                .background(colorScheme.surfaceVariant.copy(alpha = 0.1f))
                .clickable { showFilePicker = true },
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(Dimens.fiveGrid),
                )
                Spacer(Modifier.height(Dimens.oneGrid))
                Text(
                    text = state.placeholderRes?.let { stringResource(it) } ?: "Browse Files",
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
                    text = stringResource(Res.string.label_uploaded_documents),
                    style = typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurfaceVariant,
                )

                state.values.forEachIndexed { index, file ->
                    FileListItem(
                        file = file,
                        onDelete = {
                            val newList = state.values.toMutableList().apply { removeAt(index) }
                            onValueChange(state.copy(values = newList))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun FileListItem(
    file: FormInputFileState.FileUploadValue,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(Dimens.oneGrid),
    ) {
        Row(
            modifier = Modifier.padding(Dimens.oneAndHalfGrid),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.oneAndHalfGrid),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.InsertDriveFile,
                contentDescription = null,
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
                    contentDescription = null,
                    tint = colorScheme.error,
                    modifier = Modifier.size(Dimens.twoGrid),
                )
            }
        }
    }
}
