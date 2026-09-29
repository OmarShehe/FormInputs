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
`yyyy-MM-dd HH:mm` and handled in UTC, so a date does not shift with the device time zone.

Coil is used only by the image upload field and the image viewer; loading images from a URL needs a Coil network module in your app
(for example `coil-network-ktor3`). The iOS target is compile-checked only; it has not been run on a device.

Breaking changes from the unreleased 2.0.0: the API now follows FleetIQ's form inputs (`state` parameter, `StringResource` labels,
outlined fields), and the old self-validating text field, `FormInputResultState` and password-strength helpers are gone. JitPack builds
on Linux, so it can publish the Android and JVM artifacts only; the iOS artifacts need a macOS build. Until it is on a public
repository, run `./gradlew :forminput-compose:publishToMavenLocal`.
