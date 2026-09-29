package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

@Stable
data class FormInputFileState(
    override val id: String,
    override val labelRes: StringResource?,
    override val placeholderRes: StringResource?,
    override val type: FormInputType = FormInputType.FILE_UPLOAD,
    override val isMandatory: Boolean = false,
    override val hasError: Boolean = false,
    override val errorRes: StringResource? = null,
    override val icon: ImageVector? = null,
    override val isVisible: Boolean = true,
    val value: FileUploadValue? = null,
    val allowedExtensions: List<String> = emptyList(),
    val progress: Float? = null,
    val statusRes: StringResource? = null,
) : FormInputState {
    data class FileUploadValue(
        val filePath: String? = null,
        val url: String? = null,
        val fileKey: String? = null,
        val fileName: String? = null,
        val fileSize: String? = null,
    )
}
