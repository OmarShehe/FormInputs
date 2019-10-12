# Form Input

![alt text](https://raw.githubusercontent.com/OmarShehe/FormInputs/master/forminputs.gif)



[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-FormInputs-green.svg?style=flat )]( https://android-arsenal.com/details/1/7888 )



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
	        implementation 'com.github.OmarShehe.FormInputs:forminputkotlin:1.0.1'
	}
```



## Using Snapshots

If you want to use the features that have not been packaged in an official release yet, you can use a `master-SNAPSHOT` release of the current development version

```
dependencies {
     implementation 'com.github.OmarShehe.FormInputs:forminputkotlin:master-SNAPSHOT'
}
```



# Sample Usage!

Spinner
```
<com.omarshehe.forminputkotlin.FormInputSpinner
            android:id="@+id/gender"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:form_hint="Select gender"
            app:form_array="@array/array_gender"
            app:form_isMandatory="true"
            app:form_label="Gender"
            app:form_showValidIcon="false"/>
```

Auto Complete
```
<com.omarshehe.forminputkotlin.FormInputAutoComplete
            android:id="@+id/country"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_array="@array/array_country"
            app:form_height="40dp"
            app:form_hint="Your country"
            app:form_inputType="text"
            app:form_isMandatory="true"
            app:form_label="Country" />

```


Text
```
<com.omarshehe.forminputkotlin.FormInputText
            android:id="@+id/fullName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_height="40dp"
            app:form_hint="Your full name"
            app:form_inputType="text"
            app:form_isMandatory="true"
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
            app:form_isMandatory="true"
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
            app:form_isMandatory="true"
            app:form_label="Phone Number"/>
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
            android:inputType="textAutoCorrect"
            app:form_hint="Your email address"
            app:form_inputType="email"
            app:form_isMandatory="true"
            app:form_label="Email"/>
```


Mault line
```
<com.omarshehe.forminputkotlin.FormInputMultiline
            android:id="@+id/about"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_height="100dp"
            app:form_hint="About you"
            app:form_isMandatory="true"
            app:form_label="About you"
            app:form_maxLength="500"/>
```


Password
```
<com.omarshehe.forminputkotlin.FormInputPassword
            android:id="@+id/password"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            app:form_hint="Your password"
            app:form_isMandatory="true"
            app:form_label="Password"
            app:form_showPassStrength="true" />
```


Button
```
<com.omarshehe.forminputkotlin.FormInputButton
            android:id="@+id/btnSubmit"
            android:layout_width="200dp"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_marginTop="16dp"
            app:form_background="@color/colorPrimary"
            app:form_cornerRadius="100"
            app:form_height="55dp"
            app:form_progressColor="@color/colorPink"
            app:form_showProgress="true"
            app:form_textColor="@color/white"
            app:form_value="@string/Submit"
            app:form_valueOnLoad="Please, wait.." />

```
