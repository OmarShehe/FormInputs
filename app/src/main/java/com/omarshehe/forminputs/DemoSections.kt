package com.omarshehe.forminputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.label_uploaded_documents
import com.omarshehe.forminput.compose.resources.select_date
import com.omarshehe.forminput.compose.resources.select_date_time
import com.omarshehe.forminput.compose.resources.select_time
import com.omarshehe.forminput.compose.ui.CurrencyPlacement
import com.omarshehe.forminput.compose.ui.FormInputButton
import com.omarshehe.forminput.compose.ui.FormInputColorPickerField
import com.omarshehe.forminput.compose.ui.FormInputDatePickerField
import com.omarshehe.forminput.compose.ui.FormInputDateTimePickerField
import com.omarshehe.forminput.compose.ui.FormInputDropDownField
import com.omarshehe.forminput.compose.ui.FormInputImmutableTextField
import com.omarshehe.forminput.compose.ui.FormInputPasswordField
import com.omarshehe.forminput.compose.ui.FormInputPriceField
import com.omarshehe.forminput.compose.ui.FormInputSearchField
import com.omarshehe.forminput.compose.ui.FormInputTextField
import com.omarshehe.forminput.compose.ui.FormInputTheme
import com.omarshehe.forminput.compose.ui.FormInputTimePickerField
import com.omarshehe.forminput.compose.ui.FormInputUploadDocument
import com.omarshehe.forminput.compose.ui.FormInputUploadImage
import com.omarshehe.forminput.compose.ui.FormInputUploadMultiDocument
import com.omarshehe.forminput.compose.ui.LocalFormInputDefaults
import com.omarshehe.forminput.compose.ui.QuantityStepperControl
import com.omarshehe.forminput.compose.ui.model.DropDownOptionModel
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputDropDownState
import com.omarshehe.forminput.compose.ui.model.FormInputFileState
import com.omarshehe.forminput.compose.ui.model.FormInputImageState
import com.omarshehe.forminput.compose.ui.model.FormInputMultiFileState
import com.omarshehe.forminput.compose.ui.model.FormInputPasswordState
import com.omarshehe.forminput.compose.ui.model.FormInputPriceState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource

@Composable
internal fun DemoSection(title: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(FieldGap)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

/** A text field of the demo: style and shape come from the surrounding `FormInputTheme`, the modifier from the look. */
@Composable
private fun DemoTextField(look: DemoLook, state: FormInputTextFieldState, enabled: Boolean = true, onChange: (FormInputTextFieldState) -> Unit = {}) {
    FormInputTextField(state = state, onValueChange = onChange, modifier = look.fieldModifier, enabled = enabled)
}

@Composable
internal fun TextFieldsSection(look: DemoLook) = DemoSection("Text fields") {
    fun field(id: String, type: FormInputType, edit: (FormInputTextFieldState) -> FormInputTextFieldState = { it }) =
        edit(FormInputTextFieldState(id = id, type = type, value = ""))

    var name by remember { mutableStateOf(field("name", FormInputType.TEXT) { it.copy(isMandatory = true) }) }
    var amount by remember { mutableStateOf(field("amount", FormInputType.NUMBER) { it.copy(prefix = "TZS", value = "15000") }) }
    var weight by remember { mutableStateOf(field("weight", FormInputType.NUMBER) { it.copy(suffix = "kg", value = "72") }) }
    var phone by remember { mutableStateOf(field("phone", FormInputType.PHONE)) }
    var email by remember { mutableStateOf(field("email", FormInputType.EMAIL)) }
    var password by remember { mutableStateOf(field("password", FormInputType.PASSWORD)) }
    var code by remember {
        mutableStateOf(field("code", FormInputType.TEXT) { it.copy(autoCapitalize = true, maxChar = 6, showMaxChar = true) })
    }
    var about by remember {
        mutableStateOf(field("about", FormInputType.TEXT) { it.copy(isSingleLine = false, minLines = 3, maxLines = 5) })
    }
    var query by remember { mutableStateOf(field("search", FormInputType.TEXT)) }
    val required = tr("Required", "Obligatoire")

    DemoTextField(look, name.withLabel("Full name", "Nom complet")) { name = it.copy(hasError = it.value.isBlank(), error = required) }
    DemoTextField(look, amount.withLabel("Amount (prefix)", "Montant (préfixe)")) { amount = it }
    DemoTextField(look, weight.withLabel("Weight (suffix)", "Poids (suffixe)")) { weight = it }
    DemoTextField(look, phone.withLabel("Phone (digits and +)", "Téléphone (chiffres et +)")) { phone = it }
    DemoTextField(look, email.withLabel("Email", "E-mail")) { email = it }
    DemoTextField(look, password.withLabel("Password", "Mot de passe")) { password = it }
    DemoTextField(look, code.withLabel("Code (capitals, max 6)", "Code (majuscules, 6 max)")) { code = it }
    DemoTextField(look, about.withLabel("About you", "À propos de vous")) { about = it }
    DemoTextField(look, field("disabled", FormInputType.TEXT) { it.copy(value = "Cannot be edited") }.withLabel("Disabled", "Désactivé"), enabled = false)

    // A search field with no style keeps its compact pill, and the theme sets one for every field, so the pill needs a theme
    // that leaves the style and shape unset. The classic look draws it like the other fields.
    val defaults = LocalFormInputDefaults.current
    FormInputTheme(if (look.classic) defaults else defaults.copy(style = null, shape = null)) {
        FormInputSearchField(
            state = query.withLabel("Search", "Rechercher"),
            onValueChange = { query = query.copy(value = it) },
            modifier = look.fieldModifier,
        )
    }
    FormInputImmutableTextField(label = "Read only", text = "T 123 ABC", modifier = look.fieldModifier)
}

@Composable
internal fun PriceAndPasswordSection(look: DemoLook) = DemoSection("Price and password") {
    var price by remember {
        mutableStateOf(FormInputPriceState(id = "price", currency = "TSH", currencies = listOf("TSH", "USD"), isMandatory = true))
    }
    var deposit by remember { mutableStateOf(FormInputPriceState(id = "deposit", currency = "USD", currencies = listOf("USD", "EUR"))) }
    var password by remember { mutableStateOf(FormInputPasswordState(id = "password", isMandatory = true)) }
    var confirm by remember { mutableStateOf(FormInputPasswordState(id = "confirm", isMandatory = true)) }

    FormInputPriceField(
        state = price.copy(label = tr("Price", "Prix"), placeholder = tr("Enter price", "Entrez le prix")),
        onValueChange = { price = it },
        modifier = look.fieldModifier,
        groupThousands = true,
    )
    FormInputPriceField(
        state = deposit.copy(label = tr("Deposit (currency at the end)", "Acompte (devise à la fin)")),
        onValueChange = { deposit = it },
        modifier = look.fieldModifier,
        currencyPlacement = CurrencyPlacement.END,
    )
    FormInputPasswordField(
        state = password.copy(label = tr("Password", "Mot de passe"), placeholder = tr("Your password", "Votre mot de passe")),
        onValueChange = { password = it },
        modifier = look.fieldModifier,
    )
    FormInputPasswordField(
        state = confirm.copy(
            label = tr("Confirm password", "Confirmer le mot de passe"),
            placeholder = tr("Confirm your password", "Confirmez votre mot de passe"),
        ),
        onValueChange = { confirm = it },
        modifier = look.fieldModifier,
        confirmWith = password.value,
        rules = emptyList(),
        showStrength = false,
    )
}

internal val regions = listOf("Arusha", "Dar es Salaam", "Dodoma", "Geita", "Iringa", "Kagera", "Kigoma", "Mwanza")
    .mapIndexed { index, name -> DropDownOptionModel(index.toString(), name) }

@Composable
internal fun DropdownsSection(look: DemoLook) = DemoSection("Dropdowns") {
    fun dropDown(id: String, label: String, search: Boolean = false, freeText: Boolean = false) = FormInputDropDownState(
        id = id, label = label, placeholder = "Select…", options = regions, isSearchEnable = search, allowFreeText = freeText,
    )

    var plain by remember { mutableStateOf(dropDown("plain", "Region")) }
    var searchable by remember { mutableStateOf(dropDown("searchable", "Region (type to filter)", search = true)) }
    var freeText by remember { mutableStateOf(dropDown("free", "Region (free text)", freeText = true)) }
    FormInputDropDownField(modifier = look.fieldModifier, state = plain, onSelected = { plain = it })
    FormInputDropDownField(modifier = look.fieldModifier, state = searchable, onSelected = { searchable = it })
    FormInputDropDownField(modifier = look.fieldModifier, state = freeText, onSelected = { freeText = it })
    FormInputDropDownField(
        modifier = look.fieldModifier,
        state = dropDown("disabled", "Disabled"),
        enabled = false,
        supportingText = "Select something first",
        onSelected = {},
    )
}

@Composable
internal fun PickersSection(look: DemoLook) = DemoSection("Date, time and colour") {
    fun picker(type: FormInputType, label: StringResource) =
        FormInputDateTimePickerState(id = type.name, labelRes = label, placeholderRes = null, type = type)

    var date by remember { mutableStateOf(picker(FormInputType.DATE_PICKER, Res.string.select_date)) }
    var time by remember { mutableStateOf(picker(FormInputType.TIME_PICKER, Res.string.select_time)) }
    var dateTime by remember { mutableStateOf(picker(FormInputType.DATE_TIME_PICKER, Res.string.select_date_time)) }
    var colour by remember { mutableStateOf("#3F51B5") }

    FormInputDatePickerField(
        modifier = look.fieldModifier,
        state = date,
        supportingText = tr("Supporting text sits below the field", "Le texte d'aide est sous le champ"),
        onValueChange = { date = it },
    )
    // Shows an error until a time is picked.
    FormInputTimePickerField(
        modifier = look.fieldModifier,
        state = time.copy(hasError = time.value.isEmpty(), error = tr("Choose a time", "Choisissez une heure")),
        onValueChange = { time = it },
    )
    FormInputDateTimePickerField(modifier = look.fieldModifier, state = dateTime, onValueChange = { dateTime = it })
    FormInputColorPickerField(
        label = tr("Colour", "Couleur"),
        value = colour,
        onColorSelected = { colour = it },
        modifier = look.fieldModifier,
        supportingText = tr("Used for the badge", "Utilisée pour le badge"),
    )
}

@Composable
internal fun ButtonAndStepperSection(look: DemoLook) = DemoSection("Button and stepper") {
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
        shape = if (look.classic) MaterialTheme.shapes.large else MaterialTheme.shapes.small,
    )
}

@Composable
internal fun UploadsSection() = DemoSection("Uploads") {
    var document by remember {
        mutableStateOf(
            FormInputFileState(
                id = "document", labelRes = Res.string.label_uploaded_documents, placeholderRes = null,
                maxFileSizeBytes = 5L * 1024 * 1024,
            ),
        )
    }
    var documents by remember { mutableStateOf(FormInputMultiFileState(id = "documents")) }
    var images by remember {
        mutableStateOf(FormInputImageState(id = "images", labelRes = null, placeholderRes = null, values = listOf(null, null)))
    }
    FormInputUploadDocument(state = document, onValueChange = { document = it })
    FormInputUploadMultiDocument(state = documents, onValueChange = { documents = it }, areaModifier = Modifier.height(96.dp))
    FormInputUploadImage(
        state = images,
        // The image upload reports a plain FormInputState, so the demo keeps only an image state.
        onValueChange = { (it as? FormInputImageState)?.let { updated -> images = updated } },
        slotModifier = Modifier.widthIn(max = 120.dp),
    )
}
