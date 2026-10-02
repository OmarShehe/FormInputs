package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Shape
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle

/**
 * Form-wide defaults. A null [style] or [shape] leaves each input on its own default, and a `style` or `shape` passed to an
 * input always wins over these. [strings] replaces the texts the library draws itself; see [FormInputStrings].
 */
@Immutable
public data class FormInputDefaults(
    val style: FormInputFieldStyle? = null,
    val shape: Shape? = null,
    val strings: FormInputStrings = FormInputStrings(),
)

public val LocalFormInputDefaults: ProvidableCompositionLocal<FormInputDefaults> = compositionLocalOf { FormInputDefaults() }

/** Sets [FormInputDefaults] for every form input inside [content], so a screen does not repeat `style` and `shape` on each field. */
@Composable
public fun FormInputTheme(defaults: FormInputDefaults, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalFormInputDefaults provides defaults, content = content)
}
