---
name: use-forminput-compose
description: Use when building or changing forms with forminput-compose (io.github.omarshehe:forminput) in a Compose Multiplatform or Android app - text, price, password, dropdown, date/time, colour, search and upload inputs, styling (outlined or filled), form-wide defaults with FormInputTheme, and replacing the library's texts or language with FormInputStrings.
---

# Using forminput-compose

A Kotlin Multiplatform form-input library (Android, desktop JVM, iOS arm64 and simulator arm64) on Compose Multiplatform 1.12 and Kotlin 2.4.
All packages are under `com.omarshehe.forminput.compose.ui` (composables), `.ui.model` (state classes) and `.ui.utils` (helpers).
For the full parameter lists read [reference.md](reference.md); for prose and screenshots read the "Compose Multiplatform" section of the repository `README.md`.
The runnable sample is `app/src/main/java/com/omarshehe/forminputs/ComposeDemoActivity.kt` (every input, with switches for classic style and custom texts).

## Setup

```kotlin
commonMain.dependencies { implementation("io.github.omarshehe:forminput:2.1.0") }
```

The artifact is not on a public repository yet. Publish it locally (`./gradlew :forminput-compose:publishToMavenLocal` in this repo) and
add `mavenLocal()` to the consuming project's repositories. JitPack cannot build the iOS targets.
Coil is only used by image upload; loading images from a URL needs a Coil network module (for example `coil-network-ktor3`) in the app.

## The model in one paragraph

Every input is a composable that takes an immutable **state** data class and returns a changed copy through `onValueChange`. The caller owns
the state (`var s by remember { mutableStateOf(State(...)) }`) and writes the copy back. Do not mutate; use `state.copy(...)`. A field's name comes
from plain strings on the state (`label`, `placeholder`, `error`) or from `labelRes` / `placeholderRes` / `errorRes` (`StringResource`); the plain string wins.
`hasError` turns the error styling on; the error text is the `error` string. Validation is the caller's job.

## Choosing the input

| Need | Composable | State |
|---|---|---|
| Text, number, phone, email, URL | `FormInputTextField` | `FormInputTextFieldState` (`type = FormInputType.X`) |
| Amount with a currency | `FormInputPriceField` | `FormInputPriceState` |
| Password with rules and strength | `FormInputPasswordField` | `FormInputPasswordState` |
| Pick from a list (search or free text optional) | `FormInputDropDownField` | `FormInputDropDownState` + `DropDownOptionModel` |
| Date, time, date and time | `FormInputDatePickerField`, `FormInputTimePickerField`, `FormInputDateTimePickerField` | `FormInputDateTimePickerState` |
| Colour | `FormInputColorPickerField(label, value, onColorSelected)` | a `#RRGGBB` string |
| Search box | `FormInputSearchField` | `FormInputTextFieldState` |
| Read-only value | `FormInputImmutableTextField(label, text)` | none |
| Files and images | `FormInputUploadDocument`, `FormInputUploadMultiDocument`, `FormInputUploadImage` | `FormInputFileState`, `FormInputMultiFileState`, `FormInputImageState` |
| Counter | `QuantityStepperControl(label, value, onValueChange)` | an `Int` |
| Button with a spinner | `FormInputButton` | none |

`FormInputField(state, ...)` chooses the right composable for any state class when a form is built from a list of states.

## Examples

```kotlin
var name by remember { mutableStateOf(FormInputTextFieldState(id = "name", type = FormInputType.TEXT, value = "", label = "Full name", isMandatory = true)) }
FormInputTextField(state = name, onValueChange = { name = it.copy(hasError = it.value.isBlank(), error = "Required") })

var price by remember { mutableStateOf(FormInputPriceState(id = "price", currency = "TSH", currencies = listOf("TSH", "USD"), label = "Price")) }
FormInputPriceField(state = price, onValueChange = { price = it }, groupThousands = true)   // price.amount is the plain number

var pw by remember { mutableStateOf(FormInputPasswordState(id = "pw", label = "Password")) }
var confirm by remember { mutableStateOf(FormInputPasswordState(id = "pw2", label = "Confirm password")) }
FormInputPasswordField(state = pw, onValueChange = { pw = it }, onValidityChange = { canSubmit = it })
FormInputPasswordField(state = confirm, onValueChange = { confirm = it }, confirmWith = pw.value, rules = emptyList(), showStrength = false)

var region by remember { mutableStateOf(FormInputDropDownState(id = "r", label = "Region", options = regions.map { DropDownOptionModel(it.code, it.name) })) }
FormInputDropDownField(state = region, onSelected = { region = it })

var date by remember { mutableStateOf(FormInputDateTimePickerState(id = "d", labelRes = null, placeholderRes = null, type = FormInputType.DATE_PICKER, label = "Date")) }
FormInputDatePickerField(state = date, onValueChange = { date = it })   // date.value is "yyyy-MM-dd"
```

## Style, shape and colours

- `style = FormInputFieldStyle.OUTLINED` (default) or `FILLED` (tinted box with an underline, the old Compose look). Every field takes a `shape`; most take `colors`.
- Set it once for a whole form: `FormInputTheme(FormInputDefaults(style = FILLED, shape = formInputShape())) { ... }`. A `style` or `shape`
  passed to a field wins over the default. A null default leaves the field as it was.
- The old classic look is `style = FILLED` + `Modifier.formInputModifier()` + `shape = formInputShape()` (square bottom corners).
  Inside a screen that already pads its content, use `formInputModifier(padding = PaddingValues(0.dp))`.
- The stepper and the file and image uploads take `style`, `shape` and `colors` too, so they match the fields.

## Your own texts or language

The texts the library draws itself (OK, Cancel, "Show password", the password checklist, "Browse files", ...) can be replaced with `FormInputStrings`.
Fields are nullable; a null keeps the built-in English or Swahili text. Provide it once through `FormInputTheme`:

```kotlin
val myStrings = FormInputStrings(ok = "D'accord", showPassword = "Afficher", passwordRuleMinLength = { "Au moins $it caractères" })   // build once
FormInputTheme(FormInputDefaults(strings = myStrings)) { /* form */ }
```

Values may come from the app's own resources (`stringResource(Res.string.my_ok)`). An app string with the same name as a library string does
**not** override it (Compose Multiplatform keeps each module's resources separate). Field names are not part of this: give each field its
own plain `label`.

## Gotchas

- `FormInputDateTimePickerState` needs `labelRes` and `placeholderRes` passed (use `null` with a plain `label`). Same for `FormInputFileState` and `FormInputImageState`.
- Dates are stored as `yyyy-MM-dd`, `HH:mm`, `yyyy-MM-dd HH:mm` and handled in UTC. `minDateMillis` / `maxDateMillis` are UTC midnight millis.
- `FormInputTextField` filters input by `type` (NUMBER keeps digits and one dot, PHONE digits and `+`), `maxChar` and `autoCapitalize`. For the cursor use the `TextFieldValue` overload.
- Supporting text on the dropdown and the pickers is drawn **below** the field (an error replaces it). A dropdown's runtime `error` is shown as written.
- The search field keeps a compact pill unless you pass `style`; styled, it shows only its placeholder (no floating label).
- A custom `visualTransformation` on `FormInputTextField` turns off its built-in password toggle. `FormInputPasswordField` already handles masking and the toggle.
- Build `FormInputStrings` / `FormInputDefaults` once (a top-level value or `remember`); a lambda created inline each recomposition makes the whole form recompose.
- On iOS, picking a file is not supported yet: the upload fields show, and picking reports an error through the state.
- Do not invent parameters. If unsure, check [reference.md](reference.md) or the source in `forminput-compose/src/commonMain/kotlin/com/omarshehe/forminput/compose/ui/`.

## Verifying a change

```
./gradlew :forminput-compose:jvmTest                       # desktop UI tests and pure logic tests
./gradlew :forminput-compose:compileAndroidMain :forminput-compose:compileKotlinIosArm64 :forminput-compose:compileKotlinIosSimulatorArm64
./gradlew :app:installDebug                               # the demo on a device
```
