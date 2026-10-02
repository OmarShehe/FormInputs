package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

@Stable
public data class FormInputFileState(
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
    override val label: String? = null,
    override val placeholder: String? = null,
    override val error: String? = null,
    /**
     * The largest file, in bytes, the field accepts; null means no limit. A bigger pick is refused with a message. Sizes are
     * shown in 1024-based units, so `5L * 1024 * 1024` reads as `5.0 MB` while `5_000_000` reads as `4.8 MB`.
     */
    val maxFileSizeBytes: Long? = null,
) : FormInputState {
    public data class FileUploadValue(
        val filePath: String? = null,
        val url: String? = null,
        val fileKey: String? = null,
        val fileName: String? = null,
        val fileSize: String? = null,
    )
}
