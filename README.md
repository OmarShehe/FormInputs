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

# Sample Usage!

Spinner
```
 <com.omarshehe.forminputkotlin.FormInputSpinner
            android:id="@+id/gender"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:customer_hint="Select gender"
            app:customer_array="@array/array_gender"
            app:customer_isMandatory="true"
            app:customer_label="Gender"
            app:customer_value="" />
```

Auto Complete
```
<com.omarshehe.forminputjava.FormInputLayout
            android:id="@+id/company"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:inputType="textAutoCorrect"
            app:customer_array="@array/array_company"
            app:customer_component="autoComplete"
            app:customer_inputType="text"
            app:customer_hint="Enter your company"
            app:customer_isMandatory="true"
            app:customer_label="Company" />

```


Text
```
<com.omarshehe.forminputkotlin.FormInputText
            android:id="@+id/fullName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:inputType="textAutoCorrect"
            app:customer_hint="Your full name"
            app:customer_inputType="text"
            app:customer_isMandatory="true"
            app:customer_label="Full Name"
            app:customer_value="" />

```

Phone Numer
```
 <com.omarshehe.forminputkotlin.FormInputText
             android:id="@+id/phoneNumber"
             android:layout_width="match_parent"
             android:layout_height="wrap_content"
             android:layout_marginTop="16dp"
             app:customer_hint="Your phone number"
             app:customer_inputType="phoneNumber"
             app:customer_isMandatory="true"
             app:customer_label="Phone Number"
             app:customer_value=""/>
```


Number
```
  <com.omarshehe.forminputkotlin.FormInputText
             android:id="@+id/ID"
             android:layout_width="match_parent"
             android:layout_height="wrap_content"
             android:layout_marginTop="16dp"
             app:customer_hint="Your ID number"
             app:customer_inputType="number"
             app:customer_isMandatory="false"
             app:customer_label="ID Number"
             app:customer_value=""/>
```

Email
```
         <com.omarshehe.forminputkotlin.FormInputText
                    android:id="@+id/email"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="16dp"
                    android:inputType="textAutoCorrect"
                    app:customer_hint="Your email address"
                    app:customer_inputType="email"
                    app:customer_isMandatory="true"
                    app:customer_label="Email"
                    app:customer_value=""/>
```


Mault line
```
        <com.omarshehe.forminputkotlin.FormInputMultiline
                    android:id="@+id/about"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="16dp"
                    app:customer_height="250"
                    app:customer_hint="About you"
                    app:customer_isMandatory="true"
                    app:customer_label="About you"
                    app:customer_value=""
                    app:customer_maxLength="500"/>
```


Password
```
        <com.omarshehe.forminputkotlin.FormInputPassword
                android:id="@+id/password"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp"
                android:inputType="textPassword"
                app:customer_hint="Your password"
                app:customer_isMandatory="true"
                app:customer_label="Password"
                app:customer_showPassStrength="true"
                app:customer_value="" />
```


Button
```
        <com.omarshehe.forminputkotlin.FormInputButton
                   android:id="@+id/btnSubmit"
                   android:layout_width="200dp"
                   android:layout_height="wrap_content"
                   android:layout_gravity="center"
                   android:layout_marginTop="16dp"
                    app:customer_background="@drawable/btn_click"
                    app:customer_progressColor="@color/colorPink"
                    app:customer_showProgress="false"
                    app:customer_textColor="@color/white"
                    app:customer_value="@string/Submit"
                    app:customer_valueOnLoad="Please, wait.." />

```
