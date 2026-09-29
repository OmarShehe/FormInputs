package com.omarshehe.forminputs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.label_uploaded_documents
import com.omarshehe.forminput.compose.resources.select_date
import com.omarshehe.forminput.compose.resources.select_date_time
import com.omarshehe.forminput.compose.resources.select_time
import com.omarshehe.forminput.compose.ui.FormInputButton
import com.omarshehe.forminput.compose.ui.FormInputColorPickerField
import com.omarshehe.forminput.compose.ui.FormInputDatePickerField
import com.omarshehe.forminput.compose.ui.FormInputDateTimePickerField
import com.omarshehe.forminput.compose.ui.FormInputDropDownField
import com.omarshehe.forminput.compose.ui.FormInputImmutableTextField
import com.omarshehe.forminput.compose.ui.FormInputSearchField
import com.omarshehe.forminput.compose.ui.FormInputDefaults
import com.omarshehe.forminput.compose.ui.FormInputStrings
import com.omarshehe.forminput.compose.ui.FormInputTheme
import com.omarshehe.forminput.compose.ui.CurrencyPlacement
import com.omarshehe.forminput.compose.ui.FormInputPasswordField
import com.omarshehe.forminput.compose.ui.FormInputPriceField
import com.omarshehe.forminput.compose.ui.model.FormInputPasswordState
import com.omarshehe.forminput.compose.ui.model.FormInputPriceState
import com.omarshehe.forminput.compose.ui.FormInputTextField
import com.omarshehe.forminput.compose.ui.FormInputTimePickerField
import com.omarshehe.forminput.compose.ui.FormInputUploadDocument
import com.omarshehe.forminput.compose.ui.FormInputUploadImage
import com.omarshehe.forminput.compose.ui.FormInputUploadMultiDocument
import com.omarshehe.forminput.compose.ui.QuantityStepperControl
import com.omarshehe.forminput.compose.ui.model.DropDownOptionModel
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputDropDownState
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.model.FormInputFileState
import com.omarshehe.forminput.compose.ui.model.FormInputImageState
import com.omarshehe.forminput.compose.ui.model.FormInputMultiFileState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import com.omarshehe.forminput.compose.ui.utils.formInputModifier
import com.omarshehe.forminput.compose.ui.utils.formInputShape
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource

/** Every forminput-compose input on one screen, for trying on a device. */
class ComposeDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var classic by remember { mutableStateOf(false) }
            var french by remember { mutableStateOf(false) }
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        Text("forminput-compose", style = MaterialTheme.typography.headlineSmall)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Switch(checked = classic, onCheckedChange = { classic = it })
                            Text("Classic style (formInputModifier)")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Switch(checked = french, onCheckedChange = { french = it })
                            Text("Own texts (FormInputStrings, French)")
                        }
                        // The stepper and the uploads take no style or shape here: they follow this form-wide default.
                        val look = fieldStyle(classic)
                        FormInputTheme(FormInputDefaults(style = look.kind, shape = look.shape, strings = if (french) frenchStrings else FormInputStrings())) {
                            // The field names below are the app's own text, so they follow this switch through tr().
                            CompositionLocalProvider(LocalFrench provides french) {
                                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                    TextFields(classic)
                                    PriceAndPassword(classic)
                                    Dropdowns(classic)
                                    Pickers(classic)
                                    Misc(classic)
                                    Uploads(classic)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private val LocalFrench = compositionLocalOf { false }

/** The app's own wording for a field name: the French text while the switch is on. */
@Composable
private fun tr(en: String, fr: String) = if (LocalFrench.current) fr else en

@Composable
private fun FormInputTextFieldState.t(en: String, fr: String) = copy(label = tr(en, fr), placeholder = tr(en, fr))

/** An app's own wording: any text left null keeps the library's default. */
private val frenchStrings = FormInputStrings(
    ok = "D'accord", cancel = "Annuler", next = "Suivant", back = "Retour", delete = "Supprimer",
    selectDate = "Choisir une date", selectTime = "Choisir une heure", selectDateTime = "Choisir date et heure",
    searchHint = "Rechercher...", clearSearch = "Effacer", browseFiles = "Parcourir les fichiers",
    clickToUpload = "Touchez pour choisir un fichier", uploadedDocuments = "Documents",
    showPassword = "Afficher le mot de passe", hidePassword = "Masquer le mot de passe",
    passwordRulesTitle = "Votre mot de passe doit :", passwordRuleUpperCase = "Contenir une majuscule",
    passwordRuleSpecial = "Contenir un caractère spécial", passwordRuleDigit = "Contenir un chiffre",
    passwordRuleMinLength = { "Avoir au moins $it caractères" }, passwordMismatch = "Les mots de passe ne correspondent pas",
    strengthWeak = "Faible", strengthMedium = "Moyen", strengthStrong = "Fort", strengthVeryStrong = "Très fort",
)

private class FieldStyle(val modifier: Modifier, val shape: Shape, val kind: FormInputFieldStyle)

/** The old Compose look (filled fields, formInputModifier, square bottom corners) or the default outlined look. */
@Composable
private fun fieldStyle(classic: Boolean): FieldStyle =
    if (classic) {
        // The screen already pads its content, so the modifier adds no padding of its own here.
        FieldStyle(Modifier.formInputModifier(padding = PaddingValues(0.dp)), formInputShape(), FormInputFieldStyle.FILLED)
    } else {
        FieldStyle(Modifier, OutlinedTextFieldDefaults.shape, FormInputFieldStyle.OUTLINED)
    }

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@Composable
private fun TextFields(classic: Boolean) = Section("Text fields") {
    val style = fieldStyle(classic)
    fun field(id: String, label: String, type: FormInputType, extra: (FormInputTextFieldState) -> FormInputTextFieldState = { it }) =
        extra(FormInputTextFieldState(id = id, type = type, value = "", label = label, placeholder = label))

    var name by remember { mutableStateOf(field("name", "Full name", FormInputType.TEXT) { it.copy(isMandatory = true) }) }
    var number by remember {
        mutableStateOf(field("number", "Amount (prefix)", FormInputType.NUMBER) { it.copy(prefix = "TZS", value = "15000", placeholder = "0") })
    }
    var weight by remember {
        mutableStateOf(field("weight", "Weight (suffix)", FormInputType.NUMBER) { it.copy(suffix = "kg", value = "72", placeholder = "0") })
    }
    var phone by remember { mutableStateOf(field("phone", "Phone (digits and +)", FormInputType.PHONE)) }
    var email by remember { mutableStateOf(field("email", "Email", FormInputType.EMAIL)) }
    var password by remember { mutableStateOf(field("password", "Password", FormInputType.PASSWORD)) }
    var code by remember {
        mutableStateOf(field("code", "Code (capitals, max 6)", FormInputType.TEXT) {
            it.copy(
                autoCapitalize = true,
                maxChar = 6,
                showMaxChar = true
            )
        })
    }
    var about by remember {
        mutableStateOf(field("about", "About you", FormInputType.TEXT) { it.copy(isSingleLine = false, minLines = 3, maxLines = 5) })
    }
    FormInputTextField(
        modifier = style.modifier,
        shape = style.shape,
        style = style.kind,
        state = name.t("Full name", "Nom complet"),
        onValueChange = { name = it.copy(hasError = it.value.isBlank(), error = "Required") })
    FormInputTextField(modifier = style.modifier, shape = style.shape, style = style.kind, state = number.t("Amount (prefix)", "Montant (préfixe)"), onValueChange = { number = it })
    FormInputTextField(modifier = style.modifier, shape = style.shape, style = style.kind, state = weight.t("Weight (suffix)", "Poids (suffixe)"), onValueChange = { weight = it })
    FormInputTextField(modifier = style.modifier, shape = style.shape, style = style.kind, state = phone.t("Phone (digits and +)", "Téléphone (chiffres et +)"), onValueChange = { phone = it })
    FormInputTextField(modifier = style.modifier, shape = style.shape, style = style.kind, state = email.t("Email", "E-mail"), onValueChange = { email = it })
    FormInputTextField(modifier = style.modifier, shape = style.shape, style = style.kind, state = password.t("Password", "Mot de passe"), onValueChange = { password = it })
    FormInputTextField(modifier = style.modifier, shape = style.shape, style = style.kind, state = code.t("Code (capitals, max 6)", "Code (majuscules, 6 max)"), onValueChange = { code = it })
    FormInputTextField(modifier = style.modifier, shape = style.shape, style = style.kind, state = about.t("About you", "À propos de vous"), onValueChange = { about = it })
    FormInputTextField(
        modifier = style.modifier,
        shape = style.shape,
        style = style.kind,
        state = field("off", "Disabled", FormInputType.TEXT) { it.copy(value = "Cannot be edited") }.t("Disabled", "Désactivé"),
        enabled = false,
    )
    var query by remember { mutableStateOf(field("q", "Search", FormInputType.TEXT)) }
    FormInputSearchField(
        state = query.t("Search", "Rechercher"),
        onValueChange = { query = query.copy(value = it) },
        modifier = style.modifier,
        // No style keeps the compact pill; classic mode draws it like the other fields.
        style = if (classic) style.kind else null,
        shape = if (classic) style.shape else null,
    )
    FormInputImmutableTextField(
        label = "Read only",
        text = "T 123 ABC",
        modifier = style.modifier,
        style = style.kind,
        shape = style.shape,
    )
}

@Composable
private fun PriceAndPassword(classic: Boolean) = Section("Price and password") {
    val style = fieldStyle(classic)
    var price by remember {
        mutableStateOf(
            FormInputPriceState(
                id = "price", currency = "TSH", currencies = listOf("TSH", "USD"),
                label = "Price", placeholder = "Enter price", isMandatory = true,
            ),
        )
    }
    var deposit by remember {
        mutableStateOf(FormInputPriceState(id = "deposit", currency = "USD", currencies = listOf("USD", "EUR"), label = "Deposit (currency at the end)"))
    }
    var password by remember { mutableStateOf(FormInputPasswordState(id = "pw", label = "Password", placeholder = "Your password", isMandatory = true)) }
    var confirm by remember {
        mutableStateOf(FormInputPasswordState(id = "pw2", label = "Confirm password", placeholder = "Confirm your password", isMandatory = true))
    }
    FormInputPriceField(
        state = price.copy(label = tr("Price", "Prix"), placeholder = tr("Enter price", "Entrez le prix")), onValueChange = { price = it }, modifier = style.modifier, shape = style.shape, style = style.kind,
        groupThousands = true,
    )
    FormInputPriceField(
        state = deposit.copy(label = tr("Deposit (currency at the end)", "Acompte (devise à la fin)")), onValueChange = { deposit = it }, modifier = style.modifier, shape = style.shape, style = style.kind,
        currencyPlacement = CurrencyPlacement.END,
    )
    FormInputPasswordField(
        state = password.copy(label = tr("Password", "Mot de passe"), placeholder = tr("Your password", "Votre mot de passe")), onValueChange = { password = it }, modifier = style.modifier, shape = style.shape, style = style.kind,
    )
    FormInputPasswordField(
        state = confirm.copy(label = tr("Confirm password", "Confirmer le mot de passe"), placeholder = tr("Confirm your password", "Confirmez votre mot de passe")), onValueChange = { confirm = it }, modifier = style.modifier, shape = style.shape, style = style.kind,
        confirmWith = password.value, rules = emptyList(), showStrength = false,
    )
}

private val regions = listOf("Arusha", "Dar es Salaam", "Dodoma", "Geita", "Iringa", "Kagera", "Kigoma", "Mwanza")
    .mapIndexed { i, n -> DropDownOptionModel(i.toString(), n) }

@Composable
private fun Dropdowns(classic: Boolean) = Section("Dropdowns") {
    val style = fieldStyle(classic)
    fun state(id: String, label: String, search: Boolean = false, free: Boolean = false) =
        FormInputDropDownState(id = id, label = label, placeholder = "Select…", options = regions, isSearchEnable = search, allowFreeText = free)

    var plain by remember { mutableStateOf(state("plain", "Region")) }
    var search by remember { mutableStateOf(state("search", "Region (type to filter)", search = true)) }
    var free by remember { mutableStateOf(state("free", "Region (free text)", free = true)) }
    FormInputDropDownField(modifier = style.modifier, shape = style.shape, style = style.kind, state = plain, onSelected = { plain = it })
    FormInputDropDownField(modifier = style.modifier, shape = style.shape, style = style.kind, state = search, onSelected = { search = it })
    FormInputDropDownField(modifier = style.modifier, shape = style.shape, style = style.kind, state = free, onSelected = { free = it })
    FormInputDropDownField(
        modifier = style.modifier,
        shape = style.shape,
        style = style.kind,
        state = state("off", "Disabled"),
        enabled = false,
        supportingText = "Select something first",
        onSelected = {})
}

@Composable
private fun Pickers(classic: Boolean) = Section("Date, time and colour") {
    val style = fieldStyle(classic)
    fun picker(type: FormInputType, label: StringResource, value: String = "") =
        FormInputDateTimePickerState(id = type.name, labelRes = label, placeholderRes = null, type = type, value = value)

    var date by remember { mutableStateOf(picker(FormInputType.DATE_PICKER, Res.string.select_date)) }
    var time by remember { mutableStateOf(picker(FormInputType.TIME_PICKER, Res.string.select_time)) }
    var dateTime by remember { mutableStateOf(picker(FormInputType.DATE_TIME_PICKER, Res.string.select_date_time)) }
    var color by remember { mutableStateOf("#3F51B5") }
    FormInputDatePickerField(
        modifier = style.modifier,
        shape = style.shape,
        style = style.kind,
        state = date,
        supportingText = tr("Supporting text sits below the field", "Le texte d'aide est sous le champ"),
        onValueChange = { date = it },
    )
    // Shows an error until a time is picked.
    FormInputTimePickerField(
        modifier = style.modifier,
        shape = style.shape,
        style = style.kind,
        state = time.copy(hasError = time.value.isEmpty(), error = tr("Choose a time", "Choisissez une heure")),
        onValueChange = { time = it },
    )
    FormInputDateTimePickerField(
        modifier = style.modifier,
        shape = style.shape,
        style = style.kind,
        state = dateTime,
        onValueChange = { dateTime = it })
    FormInputColorPickerField(
        label = tr("Colour", "Couleur"),
        value = color,
        onColorSelected = { color = it },
        modifier = style.modifier,
        shape = style.shape,
        style = style.kind,
        supportingText = tr("Used for the badge", "Utilisée pour le badge"),
    )
}

@Composable
private fun Misc(classic: Boolean) = Section("Button and stepper") {
    var seats by remember { mutableIntStateOf(2) }
    var loading by remember { mutableStateOf(false) }
    QuantityStepperControl(label = "Seats", value = seats, onValueChange = { seats = it }, minValue = 1, maxValue = 7)
    // A loading button is disabled, so the demo stops the spinner itself after two seconds.
    LaunchedEffect(loading) {
        if (loading) {
            delay(2_000)
            loading = false
        }
    }
    FormInputButton(
        onClick = { loading = true },
        text = "Save",
        isLoading = loading,
        shape = if (classic) MaterialTheme.shapes.large else MaterialTheme.shapes.small,
    )
}

@Composable
private fun Uploads(classic: Boolean) = Section("Uploads") {
    var doc by remember { mutableStateOf(FormInputFileState(id = "doc", labelRes = Res.string.label_uploaded_documents, placeholderRes = null)) }
    var docs by remember { mutableStateOf(FormInputMultiFileState(id = "docs")) }
    var images by remember { mutableStateOf(FormInputImageState(id = "img", labelRes = null, placeholderRes = null, values = listOf(null, null))) }
    FormInputUploadDocument(state = doc, onValueChange = { doc = it })
    FormInputUploadMultiDocument(state = docs, onValueChange = { docs = it })
    FormInputUploadImage(state = images, onValueChange = { images = it as FormInputImageState })
}
