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
Compose Multiplatform 1.12 and Kotlin 2.4. Strings ship in English and Swahili.

```
commonMain.dependencies { implementation("com.github.OmarShehe:forminput-compose:2.1.0") }
```

| Input | Android | Desktop | iOS |
|---|---|---|---|
| `FormInputTextField` (text, number, phone, email, password, URL), `FormInputSearchField`, `FormInputImmutableTextField` | yes | yes | yes |
| `FormInputDropDownField` (search, free text, `enabled`, `supportingText`) | yes | yes | yes |
| `FormInputDatePickerField`, `FormInputTimePickerField`, `FormInputDateTimePickerField`, `FormInputColorPickerField` | yes | yes | yes |
| `FormInputButton`, `QuantityStepperControl` | yes | yes | yes |
| `FormInputUploadDocument`, `FormInputUploadMultiDocument`, `FormInputUploadImage`, `ZoomableImageViewer` | yes | yes | shows, but picking a file reports "not supported" |

`FormInputField(state, ...)` picks the right input for a state class. Labels are `StringResource`s (`labelRes`); the text field and the
dropdown also accept plain runtime strings (`label`, `placeholder`, `error`). Dates are stored as `yyyy-MM-dd`, `HH:mm` and
`yyyy-MM-dd HH:mm` and handled in UTC, so a stored date does not shift with the device time zone. An empty picker starts on the
device's current day, and a picker whose value arrives late opens on that value.

Every input takes a `shape`, including the stepper and the file and image uploads. The text field, the dropdown and the
date, time, date-time and colour pickers also take `style = FormInputFieldStyle.FILLED` (a tinted box with an underline, the old Compose look; the default is `OUTLINED`). For the full
classic look pass `style = FILLED`, `Modifier.formInputModifier()` as the modifier and `formInputShape()` as the shape (rounded top
corners, square bottom corners). The date, time, date-time and colour pickers take the same `style` and `shape`.
`FormInputImmutableTextField` (the read-only field) takes `style` and `shape` too; without `style` it keeps the older `outlined` flag.
`FormInputSearchField` keeps its compact pill look by default; pass `style` (and `shape`, `colors`) to draw it like the other fields, and
use the `TextFieldValue` overload to control the cursor.
To set `style` and `shape` once for a whole form, wrap it in `FormInputTheme(FormInputDefaults(style = FILLED, shape = formInputShape())) { ... }`;
a `style` or `shape` passed to an input still wins, and a null default leaves that input as it was (including the search pill).
The stepper and the file and image uploads take `style` and `colors` too, drawn with the same outline or filled box as the text fields
(without either they keep their own look; `FormInputTheme` applies to them as well). `FormInputTextField` also takes `enabled`, `readOnly`,
`textStyle`, `keyboardOptions`, `keyboardActions`, `visualTransformation` and custom `leadingIcon` / `trailingIcon` (a custom trailing icon
replaces the password toggle). `FormInputDropDownField` takes `textStyle`, `leadingIcon`, `menuShape`, `menuContainerColor`, `maxMenuHeight` and
an `itemContent` slot. `FormInputSearchField` takes `enabled` and `onSearch` (the keyboard's search action). The pickers take `dialogShape` and
`dialogContainerColor`; so does the colour picker for its popup.
The date, time, date-time and colour pickers also take `colors`, `enabled`, `supportingText` and `fieldModifier`; the colour picker has an
overload with a plain-string `label`. The picker, file and image states take plain-string `label`, `placeholder` and `error` like the text field.
`FormInputTextFieldState` takes plain-string `prefix` and `suffix` (shown dimmed; Material draws them once the field is focused or has text).
The styled search field has no floating label, only its placeholder. A runtime `error` on a dropdown is shown as written.
`formInputModifier(padding = ...)` sets the spacing around a field; pass `PaddingValues(0.dp)` inside a screen that already pads its content.
`FormInputTextField` also has an overload that takes a hoisted `TextFieldValue`
(`FormInputTextField(state, value, onValueChange)`), so you control the text, cursor and selection; input is still filtered by the
state's type, `maxChar` and `autoCapitalize`. The `:app` sample opens on a
launcher screen with two choices: a Compose demo of every input (with a "Classic style" switch) and the old View-based sample.

**Price field.** `FormInputPriceField(state = FormInputPriceState(id, currency = "TSH", currencies = listOf("TSH", "USD")), onValueChange)` is an amount with a
currency dropdown, like the View library's price box. The amount keeps digits and one dot, at most `maxIntegerDigits` whole digits and
`maxDecimals` decimals (`state.amount` is the plain number). Options: `currencyPlacement` (START or END), `groupThousands` (shows 1,234,567 without
changing the stored text), `currencyLabel`, `currencyContent`, `currencyTextStyle`, `menuShape`, `menuContainerColor`, plus the text field's `style`,
`shape`, `colors`, `enabled`, `readOnly`, `textStyle` and `contentPadding`. With one currency the selector is plain text.

**Password field.** `FormInputPasswordField(state = FormInputPasswordState(id, label = "Password"), onValueChange)` has a show/hide toggle, a requirement
checklist and a strength bar. Everything is configurable: `rules` (default: upper case, special character, number, 8 characters; `emptyList()` for none;
build your own with `PasswordRule` or `PasswordRules.minLength/upperCase/lowerCase/digit/special/...`), `rulesVisibility` (ALWAYS, WHEN_FOCUSED,
WHEN_NOT_EMPTY, NEVER), `rulesTitle`, `ruleContent` (draw a row yourself), `showStrength`, `strengthLabels`, `passwordColors`, `strengthOf` (your own scoring),
`strengthContent` (your own view), `confirmWith` and `mismatchMessage` (a "must match" field), `maskCharacter`, `allowReveal`, `initiallyRevealed`,
`revealIcon`, `hideIcon`, `leadingIcon`, `imeAction`, and `onValidityChange` (true once every rule is met and the match passes, for enabling a submit
button), plus the text field's `style`, `shape`, `colors`, `enabled`, `textStyle` and `contentPadding`. A `visualTransformation` passed to
`FormInputTextField` turns off its built-in password toggle.

**Your own texts or language.** The texts the library draws itself (OK, Cancel, Next, Back, Delete, the date, time and currency button descriptions, the
search hint and clear button, the upload prompts, the show and hide password buttons, the password checklist and strength words, and so on) can be replaced
through `FormInputStrings`. Every field is null by default and a null field keeps the built-in English or Swahili text, so set only what you need:

```kotlin
FormInputTheme(
    FormInputDefaults(
        strings = FormInputStrings(
            ok = stringResource(Res.string.my_ok),          // from the app's own string resources, so any language works
            showPassword = "Afficher le mot de passe",
            passwordRuleMinLength = { "Au moins $it caracteres" },
        ),
    ),
) { /* the form */ }
```

The app keeps its translations in its own resource files and passes them once; the library needs no extra resource files. A library string passed as a label, for example `labelRes = Res.string.select_date`, follows the override too. An app string
with the same name as a library string does **not** replace it, because Compose Multiplatform keeps each module's resources separate. Labels,
placeholders, errors, prefixes and suffixes are not part of `FormInputStrings`: they are set per input as plain strings.

Coil is used only by the image upload field and the image viewer; loading images from a URL needs a Coil network module in your app
(for example `coil-network-ktor3`). The iOS target is compile-checked only; it has not been run on a device.

Breaking changes from the unreleased 2.0.0: the API now follows FleetIQ's form inputs (`state` parameter, `StringResource` labels,
outlined fields), and the old self-validating text field, `FormInputResultState` and password-strength helpers are gone. JitPack builds
on Linux, so it can publish the Android and JVM artifacts only; the iOS artifacts need a macOS build. Until it is on a public
repository, run `./gradlew :forminput-compose:publishToMavenLocal`.
