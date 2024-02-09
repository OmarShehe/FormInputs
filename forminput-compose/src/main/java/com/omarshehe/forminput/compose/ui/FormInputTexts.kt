package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.omarshehe.forminput.compose.ui.model.Dimens

@Composable
fun TextContent(
    modifier: Modifier = Modifier,
    textValue: Any,
    textAlignment: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onBackground,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    setPadding: Boolean = true,
    lines: Int = Int.MAX_VALUE
) {
    var mModifier = modifier
    if (setPadding) {
        mModifier =
            modifier.padding(start = Dimens.normal, end = Dimens.normal)
    }
    Text(
        text = textValue.asText(),
        color = color,
        style = style,
        textAlign = textAlignment,
        modifier = mModifier,
        maxLines = lines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun Any.asText() = when (this) {
    is Int -> stringResource(this)
    else -> this.toString()
}
