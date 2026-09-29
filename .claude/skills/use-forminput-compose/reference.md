# forminput-compose API cheat sheet

Packages: composables `com.omarshehe.forminput.compose.ui`, states `...ui.model`, helpers `...ui.utils`. Check the source before relying on a
parameter that is not listed here (`forminput-compose/src/commonMain/kotlin/com/omarshehe/forminput/compose/ui/`).

## Shared state fields (interface `FormInputState`)

`id`, `labelRes`, `placeholderRes`, `errorRes` (StringResource?), `label`, `placeholder`, `error` (plain strings, win over the resources),
`type: FormInputType`, `isMandatory`, `hasError`, `icon: ImageVector?`, `isVisible`.
`FormInputType`: TEXT, PHONE, NUMBER, EMAIL, PASSWORD, URL, DROP_DOWN, DATE_PICKER, TIME_PICKER, DATE_TIME_PICKER, FILE_UPLOAD, IMAGE_PICKER.

## States

| State | Extra fields |
|---|---|
| `FormInputTextFieldState(id, type, value, ...)` | `showMaxChar`, `isSingleLine`, `minLines`, `maxLines`, `maxChar`, `autoCapitalize`, `subtitleRes`, `prefix`, `suffix` (plain) and `prefixRes`, `suffixRes`, `onHeaderActionClick`, `headerActionLabelRes`, `headerActionIcon` |
| `FormInputDropDownState(id, ...)` | `value: DropDownOptionModel`, `options: List<DropDownOptionModel>`, `isSearchEnable`, `allowFreeText`; `DropDownOptionModel(id, text, textRes)` |
| `FormInputDateTimePickerState(id, labelRes, placeholderRes, type, ...)` | `value` (string), `isManualEditable`, `minDateMillis`, `maxDateMillis` (UTC millis) |
| `FormInputPriceState(id, currency, ...)` | `amount` (plain number string), `currencies`, `maxDecimals` (2), `maxIntegerDigits` (12) |
| `FormInputPasswordState(id, ...)` | `value` |
| `FormInputFileState(id, labelRes, placeholderRes, ...)` | `value: FileUploadValue?`, `allowedExtensions`, `progress`, `statusRes` |
| `FormInputMultiFileState(id, ...)` | `values: List<FileUploadValue>`, `allowedExtensions` |
| `FormInputImageState(id, labelRes, placeholderRes, ...)` | `values: List<ImageUploadValue?>` (null is an empty slot), `allowedExtensions` |

## Composables (main parameters; all also take `modifier`)

- **`FormInputTextField(state, onValueChange: (State) -> Unit)`** and an overload `(state, value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit)`.
  Options: `textModifier`, `colors`, `shape`, `contentPadding`, `style`, `enabled`, `readOnly`, `textStyle`, `keyboardOptions`, `keyboardActions`,
  `visualTransformation`, `leadingIcon`, `trailingIcon`.
- **`FormInputPriceField(state, onValueChange)`**: text-field options plus `currencyPlacement` (START/END), `groupThousands`, `currencyLabel: (String) -> String`,
  `currencyContent`, `currencyTextStyle`, `menuShape`, `menuContainerColor`.
- **`FormInputPasswordField(state, onValueChange)`**: text-field options plus `rules` (null = default four; `emptyList()` = none), `rulesVisibility`
  (ALWAYS, WHEN_FOCUSED, WHEN_NOT_EMPTY, NEVER), `rulesTitle`, `ruleContent`, `showStrength`, `strengthLabels`, `passwordColors`, `strengthOf`,
  `strengthContent`, `confirmWith`, `mismatchMessage`, `maskCharacter`, `allowReveal`, `initiallyRevealed`, `revealIcon`, `hideIcon`, `imeAction`,
  `leadingIcon`, `onValidityChange`. Helpers: `PasswordRule(text, isMet)`, `PasswordRules.minLength/maxLength/upperCase/lowerCase/digit/special/noWhitespace`,
  `passwordStrength(value, rules)`, `defaultPasswordRules(minLength)`.
- **`FormInputDropDownField(state, onSelected)`**: `colors`, `shape`, `style`, `enabled`, `supportingText`, `fieldModifier`, `textStyle`, `leadingIcon`,
  `menuShape`, `menuContainerColor`, `maxMenuHeight`, `itemContent`.
- **Pickers `FormInputDatePickerField / TimePickerField / DateTimePickerField(state, onValueChange)`**: `shape`, `style`, `colors`, `enabled`,
  `supportingText`, `fieldModifier`, `dialogShape`, `dialogContainerColor`.
- **`FormInputColorPickerField(label: StringResource | String, value, onColorSelected)`**: `shape`, `style`, `colors`, `enabled`, `hasError`, `error`,
  `supportingText`, `fieldModifier`, `dialogShape`, `dialogContainerColor`.
- **`FormInputSearchField(state, onValueChange: (String) -> Unit)`** and a `TextFieldValue` overload: `style` (null = pill), `shape`, `colors`, `enabled`, `onSearch`.
- **`FormInputImmutableTextField(label, text)`**: `imageVector`, `isError`, `outlined`, `shape`, `onIconClick`, `style`.
- **Uploads `FormInputUploadDocument(state, onValueChange)`, `FormInputUploadMultiDocument(state, onValueChange)`,
  `FormInputUploadImage(state, onValueChange, ...)`**: `shape`, `style`, `colors`. `FormInputUploadImage` also has `onDeleteImage`, `unboundedAdd`, `maxItems`,
  `showPrimaryBadge`, `captureRequester`, `onImageClick`.
- **`QuantityStepperControl(label, value, onValueChange)`**: `description`, `icon`, `minValue`, `maxValue`, `shape`, `style`, `colors`.
- **`FormInputButton(onClick)`**: `text` or `textRes`, `style: FormInputButtonStyle` (FILLED default), `icon`, `enabled`, `isLoading`, `contentPadding`, `shape`, `color`, `contentColor`.

## Theming and helpers

- `FormInputTheme(defaults: FormInputDefaults) { content }`; `FormInputDefaults(style: FormInputFieldStyle?, shape: Shape?, strings: FormInputStrings)`.
- `FormInputStrings(...)`: `ok`, `cancel`, `next`, `back`, `delete`, `selectDate`, `selectTime`, `selectDateTime`, `selectCurrency`, `colorHexLabel`, `searchHint`,
  `clearSearch`, `uploadedDocuments`, `clickToUpload`, `browseFiles`, `unknownFile`, `primaryBadge`, `showPassword`, `hidePassword`, `passwordRulesTitle`,
  `passwordRuleUpperCase`, `passwordRuleSpecial`, `passwordRuleDigit`, `passwordRuleMinLength: ((Int) -> String)?`, `passwordMismatch`, `strengthWeak/Medium/Strong/VeryStrong`,
  `ruleMet`, `ruleNotMet`, `filePickerUnsupported`, `selectFile`. A library `StringResource` passed as a label follows these overrides too.
- `formInputShape()`, `Modifier.formInputModifier(padding)`.
- Pure helpers with tests: `sanitizeAmount`, `ThousandsSeparatorTransformation`, `TextFieldValue.withCleanedText`, and the date helpers in `utils/DateExtensions.kt`.
