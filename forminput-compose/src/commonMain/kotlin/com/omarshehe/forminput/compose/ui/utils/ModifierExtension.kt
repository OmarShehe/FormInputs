package com.omarshehe.forminput.compose.ui.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape

/** The classic form-input corners: the theme's medium shape with the bottom corners squared off. */
@Composable
fun formInputShape(): Shape = MaterialTheme.shapes.medium.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize)

/**
 * Full width, padded, and clipped to [formInputShape]; pass the same shape to the field so its outline follows the corners.
 * The default [padding] suits a screen with no padding of its own; pass `PaddingValues(0.dp)` inside a padded container so
 * the spacing is not applied twice.
 */
@Composable
fun Modifier.formInputModifier(
    padding: PaddingValues = PaddingValues(start = Dimens.twoGrid, top = Dimens.twoGrid, end = Dimens.twoGrid),
): Modifier =
    this
        .fillMaxWidth()
        .padding(padding)
        .clip(formInputShape())
