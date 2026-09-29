package com.omarshehe.forminput.compose.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.omarshehe.forminput.compose.ui.libraryString
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.model.FormInputState
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.Symbols

@Composable
fun TextContent(
    modifier: Modifier = Modifier,
    textValue: String,
    textAlignment: TextAlign = TextAlign.Start,
    color: Color = colorScheme.onBackground,
    style: TextStyle = typography.bodyLarge,
    setPadding: Boolean = true,
    lines: Int = Int.MAX_VALUE,
) {
    val resolvedModifier = if (setPadding) modifier.padding(start = Dimens.twoGrid, end = Dimens.twoGrid) else modifier
    Text(
        text = textValue,
        color = color,
        style = style,
        textAlign = textAlignment,
        modifier = resolvedModifier,
        maxLines = lines,
        overflow = TextOverflow.Ellipsis,
    )
}

/** The label as text: the runtime [FormInputState.label] if set, else [FormInputState.labelRes]. */
@Composable
fun FormInputState.resolvedLabel(): String? = label ?: labelRes?.let { libraryString(it) }

@Composable
fun FormInputState.resolvedPlaceholder(): String? = placeholder ?: placeholderRes?.let { libraryString(it) }

@Composable
fun FormInputState.resolvedError(): String? = error ?: errorRes?.let { libraryString(it) }

@Composable
fun FormInputLabel(
    state: FormInputState,
    modifier: Modifier = Modifier,
) {
    state.resolvedLabel()?.let { label ->
        Text(
            text = if (state.isMandatory) "$label${Symbols.MANDATORY_SYMBOL}" else label,
            modifier = modifier,
        )
    }
}
