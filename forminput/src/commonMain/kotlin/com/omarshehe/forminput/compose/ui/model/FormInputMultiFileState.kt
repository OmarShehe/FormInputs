package com.omarshehe.forminput.compose.ui.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

@Stable
public data class FormInputMultiFileState(
    override val id: String,
    override val labelRes: StringResource? = null,
    override val placeholderRes: StringResource? = null,
    override val type: FormInputType = FormInputType.FILE_UPLOAD,
    override val isMandatory: Boolean = false,
    override val hasError: Boolean = false,
    override val errorRes: StringResource? = null,
    override val icon: ImageVector? = null,
    override val isVisible: Boolean = true,
    val values: List<FormInputFileState.FileUploadValue> = emptyList(),
    val allowedExtensions: List<String> = emptyList(),
    override val label: String? = null,
    override val placeholder: String? = null,
    override val error: String? = null,
    /**
     * The largest file, in bytes, the field accepts; null means no limit. A bigger pick is refused with a message. Sizes are
     * shown in 1024-based units, so `5L * 1024 * 1024` reads as `5.0 MB` while `5_000_000` reads as `4.8 MB`.
     */
    val maxFileSizeBytes: Long? = null,
) : FormInputState
