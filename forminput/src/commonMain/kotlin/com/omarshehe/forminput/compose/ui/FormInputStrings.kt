package com.omarshehe.forminput.compose.ui

import com.omarshehe.forminput.compose.ui.utils.FileUtils
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.omarshehe.forminput.compose.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * The texts the library draws on its own (button names, content descriptions, hints), so an app can supply its own wording
 * or language. Every field starts as null and a null field keeps the library's English or Swahili text, so set only what
 * you want to change. Provide it once with `FormInputTheme(FormInputDefaults(strings = FormInputStrings(...)))`; values
 * can come from the app's own string resources, for example `showPassword = stringResource(Res.string.my_show_password)`.
 *
 * Build it once (a top-level value, or `remember`) rather than inline on every recomposition: an instance holding a new lambda
 * each time counts as changed, and the whole form would recompose.
 *
 * Labels, placeholders, errors, prefixes and suffixes are not here: they are set per input as plain strings.
 */
@Immutable
public data class FormInputStrings(
    val ok: String? = null,
    val cancel: String? = null,
    val next: String? = null,
    val back: String? = null,
    val delete: String? = null,
    val selectDate: String? = null,
    val selectTime: String? = null,
    val selectDateTime: String? = null,
    val selectCurrency: String? = null,
    val colorHexLabel: String? = null,
    val searchHint: String? = null,
    val clearSearch: String? = null,
    val uploadedDocuments: String? = null,
    val clickToUpload: String? = null,
    val browseFiles: String? = null,
    val unknownFile: String? = null,
    val primaryBadge: String? = null,
    val showPassword: String? = null,
    val hidePassword: String? = null,
    val passwordRulesTitle: String? = null,
    val passwordRuleUpperCase: String? = null,
    val passwordRuleSpecial: String? = null,
    val passwordRuleDigit: String? = null,
    /** Receives the minimum length, so the number can sit anywhere in the sentence. */
    val passwordRuleMinLength: ((minLength: Int) -> String)? = null,
    val passwordMismatch: String? = null,
    val strengthWeak: String? = null,
    val strengthMedium: String? = null,
    val strengthStrong: String? = null,
    val strengthVeryStrong: String? = null,
    val ruleMet: String? = null,
    val ruleNotMet: String? = null,
    val filePickerUnsupported: String? = null,
    val selectFile: String? = null,
    /** Receives the limit as text (for example `5.0 MB`), so it can sit anywhere in the sentence. */
    val fileTooLarge: ((limit: String) -> String)? = null,
    val quantityIncrease: String? = null,
    val quantityDecrease: String? = null,
    val close: String? = null,
    val zoomIn: String? = null,
    val zoomOut: String? = null,
    val addPhoto: String? = null,
    val preview: String? = null,
)

/** The override from [LocalFormInputDefaults] for [field] when there is one, otherwise the library's own resource. */
@Composable
internal fun formInputString(field: (FormInputStrings) -> String?, resource: StringResource): String =
    field(LocalFormInputDefaults.current.strings) ?: stringResource(resource)

/**
 * Text for [resource]. When it is one of the library's own strings (for example `Res.string.select_date` passed as a label) and
 * [FormInputStrings] overrides it, the override is used; any other resource is read as usual.
 */
@Composable
internal fun libraryString(resource: StringResource): String {
    val s = LocalFormInputDefaults.current.strings
    val override = when (resource) {
        Res.string.ok -> s.ok
        Res.string.cancel -> s.cancel
        Res.string.next -> s.next
        Res.string.back -> s.back
        Res.string.delete -> s.delete
        Res.string.select_date -> s.selectDate
        Res.string.select_time -> s.selectTime
        Res.string.select_date_time -> s.selectDateTime
        Res.string.select_currency -> s.selectCurrency
        Res.string.color_picker_hex_label -> s.colorHexLabel
        Res.string.search_hint -> s.searchHint
        Res.string.clear_search -> s.clearSearch
        Res.string.label_uploaded_documents -> s.uploadedDocuments
        Res.string.click_to_upload -> s.clickToUpload
        Res.string.browse_files -> s.browseFiles
        Res.string.unknown_file -> s.unknownFile
        Res.string.image_primary_badge -> s.primaryBadge
        Res.string.show_password -> s.showPassword
        Res.string.hide_password -> s.hidePassword
        Res.string.password_rules_title -> s.passwordRulesTitle
        Res.string.password_rule_upper_case -> s.passwordRuleUpperCase
        Res.string.password_rule_special -> s.passwordRuleSpecial
        Res.string.password_rule_digit -> s.passwordRuleDigit
        Res.string.password_mismatch -> s.passwordMismatch
        Res.string.password_strength_weak -> s.strengthWeak
        Res.string.password_strength_medium -> s.strengthMedium
        Res.string.password_strength_strong -> s.strengthStrong
        Res.string.password_strength_very_strong -> s.strengthVeryStrong
        Res.string.cd_rule_met -> s.ruleMet
        Res.string.cd_rule_not_met -> s.ruleNotMet
        Res.string.error_file_picker_unsupported -> s.filePickerUnsupported
        Res.string.select_file -> s.selectFile
        else -> null
    }
    return override ?: stringResource(resource)
}

/** The message for a file over [maxSizeBytes], or null when there is no limit. */
@Composable
internal fun fileTooLargeMessage(maxSizeBytes: Long?): String? {
    val limit = maxSizeBytes?.let { FileUtils.formatFileSize(it) } ?: return null
    return LocalFormInputDefaults.current.strings.fileTooLarge?.invoke(limit) ?: stringResource(Res.string.error_file_too_large, limit)
}
