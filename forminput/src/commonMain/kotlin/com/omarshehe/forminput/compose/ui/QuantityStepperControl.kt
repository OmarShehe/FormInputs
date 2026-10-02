package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.TextFieldColors
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.composables.formInputContainer
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.omarshehe.forminput.compose.ui.utils.Dimens

@Composable
fun QuantityStepperControl(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null,
    minValue: Int = 0,
    maxValue: Int = 9,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    colors: TextFieldColors? = null,
) {
    val boxShape = shape ?: LocalFormInputDefaults.current.shape ?: RoundedCornerShape(Dimens.oneAndHalfGrid)
    Box(
        modifier = modifier.formInputContainer(
            shape = boxShape,
            style = style,
            colors = colors,
            hasError = false,
            defaultBorder = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            defaultBackground = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier.padding(Dimens.twoGrid),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                border = BorderStroke(Dimens.stroke, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            ) {
                IconButton(
                    onClick = { onValueChange((value - 1).coerceAtLeast(minValue)) },
                    enabled = value > minValue,
                    modifier = Modifier.size(Dimens.fiveGrid),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    ),
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(Dimens.twoAndHalfGrid))
                }
            }

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(Dimens.fiveGrid),
            )

            Surface(
                shape = CircleShape,
                border = BorderStroke(Dimens.stroke, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
            ) {
                IconButton(
                    onClick = { onValueChange((value + 1).coerceAtMost(maxValue)) },
                    enabled = value < maxValue,
                    modifier = Modifier.size(Dimens.fiveGrid),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    ),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(Dimens.twoAndHalfGrid))
                }
            }

            Spacer(Modifier.width(Dimens.twoGrid))

            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(Dimens.fiveGrid)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(Dimens.oneGrid),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.threeGrid),
                    )
                }
                Spacer(Modifier.width(Dimens.oneAndHalfGrid))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
