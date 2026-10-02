package com.omarshehe.forminput.compose.ui

import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.PaddingValues
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import androidx.compose.ui.graphics.Shape
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextField
import com.omarshehe.forminput.compose.ui.utils.rgbToHex
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omarshehe.forminput.compose.ui.composables.FormInputBoxField
import com.omarshehe.forminput.compose.ui.composables.PickerSupportingText
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.omarshehe.forminput.compose.ui.utils.FormInputTestTags
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.cancel
import com.omarshehe.forminput.compose.resources.color_picker_hex_label
import com.omarshehe.forminput.compose.resources.ok
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.utils.Dimens
import kotlin.math.max
import kotlin.math.min

// ── Pure Kotlin HSV ↔ hex helpers ────────────────────────────────────────────

internal fun hsvToHex(
    h: Float,
    s: Float,
    v: Float,
): String {
    val hi = (h / 60f).toInt() % 6
    val f = h / 60f - (h / 60f).toInt()
    val p = v * (1f - s)
    val q = v * (1f - f * s)
    val t = v * (1f - (1f - f) * s)
    val (r, g, b) = when (hi) {
        0 -> Triple(v, t, p)
        1 -> Triple(q, v, p)
        2 -> Triple(p, v, t)
        3 -> Triple(p, q, v)
        4 -> Triple(t, p, v)
        else -> Triple(v, p, q)
    }
    return rgbToHex((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt())
}

internal fun hexToHsv(hex: String): FloatArray? {
    if (!isValidHex(hex)) return null
    val clean = hex.removePrefix("#")
    val r = clean.substring(0, 2).toIntOrNull(16)?.toFloat()?.div(255f) ?: return null
    val g = clean.substring(2, 4).toIntOrNull(16)?.toFloat()?.div(255f) ?: return null
    val b = clean.substring(4, 6).toIntOrNull(16)?.toFloat()?.div(255f) ?: return null

    val maxC = max(r, max(g, b))
    val minC = min(r, min(g, b))
    val delta = maxC - minC

    val h = when {
        delta == 0f -> 0f
        maxC == r -> 60f * (((g - b) / delta) % 6f)
        maxC == g -> 60f * ((b - r) / delta + 2f)
        else -> 60f * ((r - g) / delta + 4f)
    }.let { if (it < 0f) it + 360f else it }

    val s = if (maxC == 0f) 0f else delta / maxC
    return floatArrayOf(h, s, maxC)
}

internal fun isValidHex(hex: String): Boolean = hex.matches(Regex("^#[0-9a-fA-F]{6}$"))

private fun parseHexColor(hex: String): Color {
    val clean = hex.removePrefix("#")
    val value = clean.toLong(16)
    return Color(
        red = ((value shr 16) and 0xFF).toInt() / 255f,
        green = ((value shr 8) and 0xFF).toInt() / 255f,
        blue = (value and 0xFF).toInt() / 255f,
    )
}

/** The popup width, the height of its saturation and brightness area and the selection marker, unless the caller sets them. */
private val DefaultPickerWidth = 300.dp
private val DefaultSpectrumHeight = 180.dp
private val DefaultColorThumbSize = 16.dp

// ── Component ─────────────────────────────────────────────────────────────────

/**
 * A read-only field that shows the chosen colour as a swatch and opens a popup to pick another. [value] and the result of
 * [onColorSelected] are `#RRGGBB` strings. [popupModifier] sets the popup's width (300dp unless it says otherwise) and
 * [spectrumModifier] the height of its saturation and brightness area (180dp); [thumbSize] and [thumbColor] style the selection
 * marker. [textStyle] and [contentPadding] style the field, and [fieldModifier] reaches the field itself.
 */
@Composable
public fun FormInputColorPickerField(
    label: StringResource,
    value: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    colors: TextFieldColors? = null,
    enabled: Boolean = true,
    hasError: Boolean = false,
    error: String? = null,
    supportingText: String? = null,
    fieldModifier: Modifier = Modifier,
    dialogShape: Shape? = null,
    dialogContainerColor: Color? = null,
    popupModifier: Modifier = Modifier,
    spectrumModifier: Modifier = Modifier,
    thumbSize: Dp = DefaultColorThumbSize,
    thumbColor: Color = Color.White,
    textStyle: TextStyle? = null,
    contentPadding: PaddingValues? = null,
) {
    FormInputColorPickerField(
        label = libraryString(label),
        value = value,
        onColorSelected = onColorSelected,
        modifier = modifier,
        shape = shape,
        style = style,
        colors = colors,
        enabled = enabled,
        hasError = hasError,
        error = error,
        supportingText = supportingText,
        fieldModifier = fieldModifier,
        dialogShape = dialogShape,
        dialogContainerColor = dialogContainerColor,
        popupModifier = popupModifier,
        spectrumModifier = spectrumModifier,
        thumbSize = thumbSize,
        thumbColor = thumbColor,
        textStyle = textStyle,
        contentPadding = contentPadding,
    )
}

/** Same field with a runtime [label]. While [hasError] is set, [error] replaces [supportingText] below the field. */
@Composable
public fun FormInputColorPickerField(
    label: String,
    value: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    colors: TextFieldColors? = null,
    enabled: Boolean = true,
    hasError: Boolean = false,
    error: String? = null,
    supportingText: String? = null,
    fieldModifier: Modifier = Modifier,
    dialogShape: Shape? = null,
    dialogContainerColor: Color? = null,
    popupModifier: Modifier = Modifier,
    spectrumModifier: Modifier = Modifier,
    thumbSize: Dp = DefaultColorThumbSize,
    thumbColor: Color = Color.White,
    textStyle: TextStyle? = null,
    contentPadding: PaddingValues? = null,
) {
    val defaults = LocalFormInputDefaults.current
    val filled = (style ?: defaults.style) == FormInputFieldStyle.FILLED
    val fieldShape = shape ?: defaults.shape ?: if (filled) TextFieldDefaults.shape else OutlinedTextFieldDefaults.shape
    var showPicker by remember { mutableStateOf(false) }
    val swatchColor = if (isValidHex(value)) parseHexColor(value) else null

    // OutlinedTextField gives stable, fixed sizing (no layout shift on click/focus).
    // A transparent matchParentSize overlay on top captures all clicks reliably —
    // the TextField itself is non-focusable so it won't steal the tap.
    Column(modifier = modifier.fillMaxWidth()) {
        Box {
            FormInputBoxField(
                textStyle = textStyle,
                contentPadding = contentPadding,
                filled = filled,
                focusable = false,
                value = TextFieldValue(value),
                onValueChange = {},
                readOnly = true,
                modifier = fieldModifier
                    .fillMaxWidth()
                    .focusProperties { canFocus = false }
                    .focusable(false)
                    .pointerHoverIcon(PointerIcon.Hand, overrideDescendants = true),
                label = { Text(label) },
                enabled = enabled,
                isError = hasError,
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(Dimens.threeGrid),
                    ) {
                        VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(Modifier.width(Dimens.oneGrid))
                        Box(
                            modifier = Modifier
                                .size(Dimens.threeGrid)
                                .clip(RoundedCornerShape(Dimens.quarterGrid))
                                .border(
                                    Dimens.stroke,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(Dimens.quarterGrid),
                                )
                                .background(swatchColor ?: MaterialTheme.colorScheme.surfaceVariant),
                        )
                        Spacer(Modifier.width(Dimens.oneGrid))
                    }
                },
                shape = fieldShape,
                colors = colors ?: if (filled) TextFieldDefaults.colors() else OutlinedTextFieldDefaults.colors(),
            )
        
            if (enabled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(fieldShape)
                        .clickable { showPicker = true },
                )
            }
    
        }
        PickerSupportingText(hasError, error, supportingText)
    }

    if (showPicker) {
        ColorPickerPopup(
            shape = dialogShape ?: RoundedCornerShape(Dimens.oneGrid),
            containerColor = dialogContainerColor ?: MaterialTheme.colorScheme.surface,
            initialHex = value.takeIf { isValidHex(it) } ?: "",
            popupModifier = popupModifier,
            spectrumModifier = spectrumModifier,
            thumbSize = thumbSize,
            thumbColor = thumbColor,
            onDismiss = { showPicker = false },
            onConfirm = { hex ->
                showPicker = false
                onColorSelected(hex)
            },
        )
    }
}

// ── Popup ─────────────────────────────────────────────────────────────────────

@Composable
private fun ColorPickerPopup(
    shape: Shape,
    containerColor: Color,
    initialHex: String,
    popupModifier: Modifier,
    spectrumModifier: Modifier,
    thumbSize: Dp,
    thumbColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val initialHsv = hexToHsv(initialHex) ?: floatArrayOf(0f, 1f, 1f)
    var hue by remember(initialHex) { mutableStateOf(initialHsv[0]) }
    var saturation by remember(initialHex) { mutableStateOf(initialHsv[1]) }
    var brightness by remember(initialHex) { mutableStateOf(initialHsv[2]) }
    var hexInput by remember(initialHex) {
        mutableStateOf(
            if (isValidHex(initialHex)) {
                initialHex.uppercase()
            } else {
                hsvToHex(initialHsv[0], initialHsv[1], initialHsv[2])
            },
        )
    }

    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Surface(
            modifier = popupModifier
                .shadow(elevation = Dimens.twoGrid, shape = shape)
                .width(DefaultPickerWidth)
                .testTag(FormInputTestTags.ColorPickerPopup),
            shape = shape,
            color = containerColor,
            tonalElevation = Dimens.twoGrid,
        ) {
            Column(modifier = Modifier.padding(Dimens.twoGrid)) {
                SaturationValueCanvas(
                    hue = hue,
                    saturation = saturation,
                    brightness = brightness,
                    onChanged = { s, v ->
                        saturation = s
                        brightness = v
                        hexInput = hsvToHex(hue, s, v)
                    },
                    thumbSize = thumbSize,
                    thumbColor = thumbColor,
                    modifier = spectrumModifier.fillMaxWidth().height(DefaultSpectrumHeight).testTag(FormInputTestTags.ColorSpectrum),
                )
                Spacer(Modifier.height(Dimens.twoGrid))
                HueSlider(
                    hue = hue,
                    onHueChanged = { h ->
                        hue = h
                        hexInput = hsvToHex(h, saturation, brightness)
                    },
                    modifier = Modifier.fillMaxWidth().height(Dimens.threeGrid),
                )
                Spacer(Modifier.height(Dimens.twoGrid))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.oneGrid),
                ) {
                    Text(
                        text = formInputString(FormInputStrings::colorHexLabel, Res.string.color_picker_hex_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    BasicTextField(
                        value = hexInput,
                        onValueChange = { raw ->
                            val typed = raw.uppercase().take(7)
                            hexInput = typed
                            val hsv = hexToHsv(typed)
                            if (hsv != null) {
                                hue = hsv[0]
                                saturation = hsv[1]
                                brightness = hsv[2]
                            }
                        },
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                Dimens.stroke,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(Dimens.halfGrid),
                            )
                            .padding(horizontal = Dimens.oneGrid, vertical = Dimens.halfGrid),
                    )
                    val previewColor = if (isValidHex(hexInput)) {
                        parseHexColor(hexInput)
                    } else {
                        parseHexColor(hsvToHex(hue, saturation, brightness))
                    }
                    Box(
                        modifier = Modifier
                            .size(Dimens.fourGrid)
                            .clip(RoundedCornerShape(Dimens.halfGrid))
                            .border(
                                Dimens.stroke,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(Dimens.halfGrid),
                            )
                            .background(previewColor),
                    )
                }

                Spacer(Modifier.height(Dimens.twoGrid))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FormInputButton(
                        text = formInputString(FormInputStrings::cancel, Res.string.cancel),
                        onClick = onDismiss,
                        style = FormInputButtonStyle.OUTLINED,
                    )
                    Spacer(Modifier.width(Dimens.oneGrid))
                    FormInputButton(
                        text = formInputString(FormInputStrings::ok, Res.string.ok),
                        onClick = {
                            val finalHex = if (isValidHex(hexInput)) {
                                hexInput.uppercase()
                            } else {
                                hsvToHex(hue, saturation, brightness)
                            }
                            onConfirm(finalHex)
                        },
                    )
                }
            }
        }
    }
}

// ── Canvas composables ────────────────────────────────────────────────────────

@Composable
private fun SaturationValueCanvas(
    hue: Float,
    saturation: Float,
    brightness: Float,
    onChanged: (saturation: Float, brightness: Float) -> Unit,
    thumbSize: Dp,
    thumbColor: Color,
    modifier: Modifier = Modifier,
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val hueColor = parseHexColor(hsvToHex(hue, 1f, 1f))
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.halfGrid))
            .onSizeChanged { canvasSize = it }
            .background(brush = Brush.horizontalGradient(listOf(Color.White, hueColor)))
            .background(brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            .pointerInput(canvasSize) {
                detectDragGestures { change, _ ->
                    if (canvasSize.width > 0 && canvasSize.height > 0) {
                        val s = (change.position.x / canvasSize.width).coerceIn(0f, 1f)
                        val v = 1f - (change.position.y / canvasSize.height).coerceIn(0f, 1f)
                        onChanged(s, v)
                    }
                }
            }
            .pointerInput(canvasSize) {
                detectTapGestures { offset ->
                    if (canvasSize.width > 0 && canvasSize.height > 0) {
                        val s = (offset.x / canvasSize.width).coerceIn(0f, 1f)
                        val v = 1f - (offset.y / canvasSize.height).coerceIn(0f, 1f)
                        onChanged(s, v)
                    }
                }
            },
    ) {
        val thumbPxX = saturation * canvasSize.width
        val thumbPxY = (1f - brightness) * canvasSize.height
        val thumbSizePx = with(density) { thumbSize.toPx() }
        val offsetX = with(density) {
            (thumbPxX - thumbSizePx / 2).coerceIn(0f, (canvasSize.width - thumbSizePx).coerceAtLeast(0f)).toDp()
        }
        val offsetY = with(density) {
            (thumbPxY - thumbSizePx / 2).coerceIn(0f, (canvasSize.height - thumbSizePx).coerceAtLeast(0f)).toDp()
        }

        Box(
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .size(thumbSize)
                .clip(CircleShape)
                .border(Dimens.quarterGrid, thumbColor, CircleShape)
                .background(parseHexColor(hsvToHex(hue, saturation, brightness)), CircleShape),
        )
    }
}

@Composable
private fun HueSlider(
    hue: Float,
    onHueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sliderWidth by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val hueGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFF0000.toInt()),
            Color(0xFFFFFF00.toInt()),
            Color(0xFF00FF00.toInt()),
            Color(0xFF00FFFF.toInt()),
            Color(0xFF0000FF.toInt()),
            Color(0xFFFF00FF.toInt()),
            Color(0xFFFF0000.toInt()),
        ),
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.halfGrid))
            .onSizeChanged { sliderWidth = it.width }
            .background(hueGradient)
            .pointerInput(sliderWidth) {
                detectDragGestures { change, _ ->
                    if (sliderWidth > 0) onHueChanged((change.position.x / sliderWidth * 360f).coerceIn(0f, 360f))
                }
            }
            .pointerInput(sliderWidth) {
                detectTapGestures { offset ->
                    if (sliderWidth > 0) onHueChanged((offset.x / sliderWidth * 360f).coerceIn(0f, 360f))
                }
            },
    ) {
        val thumbPxX = (hue / 360f) * sliderWidth
        val thumbWidthDp = Dimens.halfGrid
        val thumbWidthPx = with(density) { thumbWidthDp.toPx() }
        val offsetX = with(density) {
            (thumbPxX - thumbWidthPx / 2).coerceIn(0f, (sliderWidth - thumbWidthPx).coerceAtLeast(0f)).toDp()
        }

        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .width(thumbWidthDp)
                .height(Dimens.threeGrid)
                .background(Color.White)
                .border(Dimens.stroke, Color.Gray),
        )
    }
}
