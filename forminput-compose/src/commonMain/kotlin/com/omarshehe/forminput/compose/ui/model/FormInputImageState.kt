package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import com.omarshehe.forminput.compose.ui.utils.FileUtils

@Stable
data class FormInputImageState(
    override val id: String,
    override val labelRes: StringResource?,
    override val placeholderRes: StringResource?,
    override val type: FormInputType = FormInputType.IMAGE_PICKER,
    override val isMandatory: Boolean = false,
    override val hasError: Boolean = false,
    override val errorRes: StringResource? = null,
    override val icon: ImageVector? = null,
    override val isVisible: Boolean = true,
    val values: List<ImageUploadValue?> = emptyList(),
    val allowedExtensions: List<String> = emptyList(),
) : FormInputState {
    data class ImageUploadValue(
        val filePath: String?,
        val progress: Float? = null,
        val url: String? = null,
        val fileKey: String? = null,
        val fileName: String? = null,
        val fileSize: String? = null,
        val isPrimary: Boolean = false,
        val thumbnailUrl: String? = null,
    )
}

/** Applies a picked image to [targetIndex] (fixed-slot) or appends it (`null` = unbounded-add). */
internal fun FormInputImageState.withPickedImage(
    targetIndex: Int?,
    filePath: String,
    fileName: String?,
    fileSize: Long?,
): FormInputImageState {
    val newValue = FormInputImageState.ImageUploadValue(
        filePath = filePath,
        fileName = fileName,
        fileSize = fileSize?.let { FileUtils.formatFileSize(it) },
    )
    val updated = values.toMutableList()
    if (targetIndex == null) updated.add(newValue) else updated[targetIndex] = newValue
    return copy(values = updated)
}

/** Removes the image at [index]: shrinks the list (unbounded-add) or nulls the slot in place (fixed-slot). */
internal fun FormInputImageState.withImageRemoved(
    index: Int,
    unboundedAdd: Boolean,
): FormInputImageState {
    val updated = values.toMutableList()
    if (unboundedAdd) updated.removeAt(index) else updated[index] = null
    return copy(values = updated)
}
