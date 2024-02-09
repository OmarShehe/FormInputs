package com.omarshehe.forminput.compose.ui.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.omarshehe.forminput.compose.ui.model.Dimens

@Composable
fun formInputModifier(): Modifier = Modifier
    .fillMaxWidth()
    .padding(start = Dimens.normal, top = Dimens.normal, end = Dimens.normal)
    .clip(
        MaterialTheme.shapes.medium.copy(
            bottomEnd = ZeroCornerSize,
            bottomStart = ZeroCornerSize
        )
    )
