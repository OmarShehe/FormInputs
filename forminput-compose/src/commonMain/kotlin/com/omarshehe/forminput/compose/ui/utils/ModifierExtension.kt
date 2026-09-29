package com.omarshehe.forminput.compose.ui.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun Modifier.formInputModifier(): Modifier =
    this
        .fillMaxWidth()
        .padding(start = Dimens.twoGrid, top = Dimens.twoGrid, end = Dimens.twoGrid)
        .clip(
            MaterialTheme.shapes.medium.copy(
                bottomEnd = ZeroCornerSize,
                bottomStart = ZeroCornerSize,
            ),
        )
