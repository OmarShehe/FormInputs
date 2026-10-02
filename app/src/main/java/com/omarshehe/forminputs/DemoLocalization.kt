package com.omarshehe.forminputs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.omarshehe.forminput.compose.ui.FormInputStrings
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState

internal val LocalFrench = compositionLocalOf { false }

/** The app's own wording for a field name: the French text while the switch is on. */
@Composable
internal fun tr(en: String, fr: String): String = if (LocalFrench.current) fr else en

/** This state with its label and placeholder set to the English or French wording, whichever the switch is on. */
@Composable
internal fun FormInputTextFieldState.withLabel(en: String, fr: String): FormInputTextFieldState =
    copy(label = tr(en, fr), placeholder = tr(en, fr))

/** An app's own wording: any text left null keeps the library's default. */
internal val frenchStrings = FormInputStrings(
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
