# Form Input

![alt text](https://raw.githubusercontent.com/OmarShehe/FormInputs/master/forminputs.gif)


[![](https://jitpack.io/v/OmarShehe/FormInputs.svg)](https://jitpack.io/#OmarShehe/FormInputs)    [![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-FormInputs-green.svg?style=flat )]( https://android-arsenal.com/details/1/7888 ) 
[![](https://jitci.com/gh/OmarShehe/FormInputs/svg)](https://jitci.com/gh/OmarShehe/FormInputs)




Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```	
Add the dependency
```
dependencies {
	        implementation 'com.github.OmarShehe:FormInputs:1.0.5'
	}
```




# Sample Usage!

Spinner
```
<com.omarshehe.forminputkotlin.FormInputSpinner
            android:id="@+id/gender"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:form_array="@array/array_gender"
            app:form_hint="Select gender"
            app:form_label="Gender"
            app:form_showLabel="false"
            app:form_textColor="@color/colorGrey" />
```

Auto Complete
```
<com.omarshehe.forminputkotlin.FormInputAutoComplete
            android:id="@+id/country"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_array="@array/array_country"
            app:form_height="@dimen/formInputInput_box_height"
            app:form_hint="Your country"
            app:form_inputType="text"
            app:form_label="Country" />

```


Text
```
<com.omarshehe.forminputkotlin.FormInputText
            android:id="@+id/fullName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_hint="Your full name"
            app:form_inputType="text"
            app:form_label="Full Name" />

```

Text
```
<com.omarshehe.forminputkotlin.FormInputSpinnerInputBox
            android:id="@+id/price"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_array="@array/array_currency"
            app:form_hint="Enter Price"
            app:form_inputType="number"
            app:form_label="Price" />
```


Phone number
```
<com.omarshehe.forminputkotlin.FormInputText
            android:id="@+id/phoneNumber"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_hint="Your phone number"
            app:form_inputType="phoneNumber"
            app:form_isMandatory="false"
            app:form_label="Phone Number" />
```


Number
```
<com.omarshehe.forminputkotlin.FormInputText
            android:id="@+id/ID"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_hint="Your ID number"
            app:form_inputType="number"
            app:form_isMandatory="false"
            app:form_label="ID Number"/>
```

Email
```
<com.omarshehe.forminputkotlin.FormInputText
            android:id="@+id/email"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_hint="Your email address"
            app:form_inputType="email"
            app:form_label="Email"/>
```


Mault line
```
<com.omarshehe.forminputkotlin.FormInputMultiline
            android:id="@+id/about"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_height="130dp"
            app:form_hint="About you"
            app:form_label="About you"
            app:form_maxLength="500" />

```


Password
```
<com.omarshehe.forminputkotlin.FormInputPassword
            android:id="@+id/password"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_hint="Your password"
            app:form_label="Password"
            app:form_showPassStrength="true" />
```


Pin
```
<com.omarshehe.forminputkotlin.FormInputPin
            android:id="@+id/confirmPin"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_label="Pin"
            app:form_hint="0"
            app:form_inputType="number"/>
```


Button
```
<com.omarshehe.forminputkotlin.FormInputButton
            android:id="@+id/btnSubmit"
            android:layout_width="wrap_content"
            android:layout_height="55dp"
            android:layout_gravity="center"
            android:layout_marginTop="16dp"
            android:text="@string/Submit"
            android:textAllCaps="false"
            android:textColor="@color/white"
            app:backgroundTint="@color/colorPrimary"
            app:cornerRadius="35dp"
            app:form_progressColor="@color/colorPink"
            app:form_showProgress="true"
            app:form_valueOnLoad="Please, wait.." />

```

# Compose Multiplatform (forminput-compose 2.1.0)

`forminput-compose` is a Kotlin Multiplatform library for Android, desktop JVM and iOS (arm64, simulator arm64), built on
Compose Multiplatform 1.12 and Kotlin 2.4. Its own texts ship in English and Swahili, and an app can replace them (see
[Your own texts or language](#your-own-texts-or-language)).

```kotlin
commonMain.dependencies { implementation("io.github.omarshehe:forminput:2.1.0") }
```

## Inputs

| Input | Android | Desktop | iOS |
|---|---|---|---|
| `FormInputTextField` (text, number, phone, email, password, URL), `FormInputSearchField`, `FormInputImmutableTextField` (read-only) | yes | yes | yes |
| `FormInputPriceField` (amount and currency), `FormInputPasswordField` (checklist and strength) | yes | yes | yes |
| `FormInputDropDownField` (search, free text) | yes | yes | yes |
| `FormInputDatePickerField`, `FormInputTimePickerField`, `FormInputDateTimePickerField`, `FormInputColorPickerField` | yes | yes | yes |
| `FormInputButton`, `QuantityStepperControl` | yes | yes | yes |
| `FormInputUploadDocument`, `FormInputUploadMultiDocument`, `FormInputUploadImage`, `ZoomableImageViewer` | yes | yes | shows, but picking a file reports "not supported" |

`FormInputField(state, ...)` picks the right input for a state class. The iOS target is compile-checked only; it has not been run on a device.

## Labels, values and dates

- A field's name is the state's `labelRes` (a `StringResource`) or a plain runtime string: `label`, `placeholder` and `error`, on the text,
  price, password, dropdown, picker and upload states. The plain strings win when both are set.
- Dates are stored as `yyyy-MM-dd`, `HH:mm` and `yyyy-MM-dd HH:mm` and handled in UTC, so a stored date does not shift with the device time
  zone. An empty picker starts on the device's current day, and a picker whose value arrives late opens on that value.
- `FormInputTextField` also has an overload that takes a hoisted `TextFieldValue` (`FormInputTextField(state, value, onValueChange)`), so you
  control the text, cursor and selection; input is still filtered by the state's type, `maxChar` and `autoCapitalize`. `FormInputSearchField`
  has the same overload.
- `FormInputTextFieldState` takes plain-string `prefix` and `suffix` (shown dimmed). Material draws them once the field is focused or has text.

## Style, shape and colours

Every field takes a `shape`. These take `style = FormInputFieldStyle.OUTLINED` (the default) or `FILLED` (a tinted box with an underline,
the old Compose look): the text, price and password fields, the dropdown, the four pickers, the read-only field, the search field, the stepper and
the file and image uploads. The stepper and uploads share one container with the fields, so they match them in either style.

| Option | Where |
|---|---|
| `colors` (`TextFieldColors`) | text, price and password fields, dropdown, the four pickers, search field (once it has a `style`), stepper, uploads |
| `enabled` | text, price, password, dropdown, the four pickers, search |
| `supportingText` | dropdown and the four pickers; drawn **below** the field, and an error replaces it |
| `fieldModifier` | dropdown and the four pickers (the modifier for the field itself, `modifier` is for the whole input) |
| `dialogShape`, `dialogContainerColor` | the date, time, date-time and colour pickers (the colour one for its popup) |

- **Whole form at once:** `FormInputTheme(FormInputDefaults(style = FILLED, shape = formInputShape())) { ... }` sets `style` and `shape` for every input
  inside, including the stepper and uploads. A `style` or `shape` passed to an input wins, and a null default leaves that input as it was.
- **The old classic look:** `style = FILLED`, `Modifier.formInputModifier()` as the modifier and `formInputShape()` as the shape (rounded top
  corners, square bottom corners). `formInputModifier(padding = ...)` sets the spacing around a field; pass `PaddingValues(0.dp)` inside a
  screen that already pads its content.
- **Read-only field:** without `style` it keeps the older `outlined` flag. **Search field:** keeps its compact pill by default; pass `style` to
  draw it like the other fields (it then shows only its placeholder, no floating label).
- **Spacing:** the dropdown, the four pickers and the read-only field draw Material's outlined or filled decoration around a plain text box, the same
  way `FormInputTextField` does, so every field is the same height and the same distance from its neighbours, and a tap or ripple lines up with the
  drawn box at any font size.

## More options per input

- **`FormInputTextField`:** `enabled`, `readOnly`, `textStyle`, `keyboardOptions`, `keyboardActions`, `visualTransformation`, `contentPadding`,
  and custom `leadingIcon` / `trailingIcon` (a custom trailing icon replaces the password toggle, and so does passing a
  `visualTransformation`).
- **`FormInputDropDownField`:** `textStyle`, `leadingIcon`, `menuShape`, `menuContainerColor`, `maxMenuHeight` and an `itemContent` slot. A runtime
  `error` is shown as written.
- **`FormInputSearchField`:** `enabled` and `onSearch` (the keyboard's search action).
- **`FormInputColorPickerField`:** an overload with a plain-string `label`.

## Price field

```kotlin
var price by remember {
    mutableStateOf(FormInputPriceState(id = "price", currency = "TSH", currencies = listOf("TSH", "USD"), label = "Price"))
}
FormInputPriceField(state = price, onValueChange = { price = it }, groupThousands = true)
```

An amount with a currency dropdown, like the View library's price box. `state.amount` is the plain number: digits and one dot, at most
`maxIntegerDigits` whole digits and `maxDecimals` decimals (a leading dot becomes `0.`, leading zeros are dropped). Options:
`currencyPlacement` (START or END), `groupThousands` (shows 1,234,567 without changing the stored text), `currencyLabel`, `currencyContent`,
`currencyTextStyle`, `menuShape`, `menuContainerColor`, plus the text field's `style`, `shape`, `colors`, `enabled`, `readOnly`, `textStyle` and
`contentPadding`. With one currency the selector is plain text; with several it is a dropdown pill that is fully tappable.

## Password field

```kotlin
var password by remember { mutableStateOf(FormInputPasswordState(id = "pw", label = "Password")) }
var confirm by remember { mutableStateOf(FormInputPasswordState(id = "pw2", label = "Confirm password")) }
FormInputPasswordField(state = password, onValueChange = { password = it }, onValidityChange = { canSubmit = it })
FormInputPasswordField(state = confirm, onValueChange = { confirm = it }, confirmWith = password.value, rules = emptyList(), showStrength = false)
```

A show/hide toggle, a requirement checklist and a strength bar. Everything is configurable:

- **Rules:** `rules` (default: an upper case letter, a special character, a number, 8 characters; `emptyList()` for none). Build your own
  with `PasswordRule("text") { it.length > 3 }` or `PasswordRules.minLength / maxLength / upperCase / lowerCase / digit / special / noWhitespace`.
  `rulesVisibility` is ALWAYS, WHEN_FOCUSED, WHEN_NOT_EMPTY or NEVER; `rulesTitle` replaces the heading; `ruleContent` draws a row yourself.
- **Strength:** `showStrength`, `strengthLabels`, `passwordColors`, `strengthOf` (your own scoring) and `strengthContent` (your own view).
  `passwordStrength(value, rules)` is also public.
- **Match check:** `confirmWith` (the other password) and `mismatchMessage`.
- **Toggle and mask:** `maskCharacter`, `allowReveal`, `initiallyRevealed`, `revealIcon`, `hideIcon`, `leadingIcon`, `imeAction`.
- **Validity:** `onValidityChange` reports true once every rule is met and the match check passes, for enabling a submit button.
- Plus the text field's `style`, `shape`, `colors`, `enabled`, `textStyle` and `contentPadding`.

## Your own texts or language

The texts the library draws itself (OK, Cancel, Next, Back, Delete, the date, time and currency button descriptions, the search hint and clear
button, the upload prompts, the show and hide password buttons, the password checklist and strength words, and so on) can be replaced through
`FormInputStrings`. Every field is null by default and a null field keeps the built-in English or Swahili text, so set only what you need:

```kotlin
FormInputTheme(
    FormInputDefaults(
        strings = FormInputStrings(
            ok = stringResource(Res.string.my_ok),          // from the app's own string resources, so any language works
            showPassword = "Afficher le mot de passe",
            passwordRuleMinLength = { "Au moins $it caractères" },
        ),
    ),
) { /* the form */ }
```

- The app keeps its translations in its own resource files and passes them once; the library needs no extra resource files.
- Build the `FormInputStrings` once (a top-level value or `remember`), not inline on every recomposition, so the form does not recompose needlessly.
- A library string passed as a label, for example `labelRes = Res.string.select_date`, follows the override too.
- An app string with the same name as a library string does **not** replace it, because Compose Multiplatform keeps each module's resources separate.
- Labels, placeholders, errors, prefixes and suffixes are not part of `FormInputStrings`: they are set per input as plain strings.
- The Swahili wording has not been reviewed by a native speaker.

## Demo app, tests and publishing

- The `:app` sample opens on a launcher with two choices: a Compose demo of every input (with a "Classic style" switch and an "Own texts" switch
  that shows `FormInputStrings` in French) and the old View-based sample. Install it with `./gradlew :app:installDebug`.
- Run the library tests with `./gradlew :forminput-compose:jvmTest` (desktop UI tests and the pure logic tests). Compile-check all targets with
  `:forminput-compose:compileAndroidMain`, `compileKotlinIosArm64` and `compileKotlinIosSimulatorArm64`.
- Coil is used only by the image upload field and the image viewer; loading images from a URL needs a Coil network module in your app (for
  example `coil-network-ktor3`).
- Breaking changes from the unreleased 2.0.0: the API now follows FleetIQ's form inputs (`state` parameter, `StringResource` labels, outlined
  fields), and the old self-validating text field, `FormInputResultState` and password-strength helpers are gone.
- JitPack builds on Linux, so it can publish the Android and JVM artifacts only; the iOS artifacts need a macOS build. Until it is on a public
  repository, run `./gradlew :forminput-compose:publishToMavenLocal`.

## For AI coding assistants

`.claude/skills/use-forminput-compose/` holds a skill (`SKILL.md`, plus a `reference.md` API cheat sheet) that teaches an assistant such as Claude Code how
to use this library: which input to pick, the state pattern, styling, form-wide defaults, custom texts and the common mistakes. Copy the folder into
another project's `.claude/skills/` to use it there.
