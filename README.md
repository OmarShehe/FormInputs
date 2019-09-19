# Form Input

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
	        implementation 'com.github.OmarShehe:FormInput:master-SNAPSHOT'
	}
```

# Sample Usage!

Spinner
```
<com.technovations.forminputs.FormInputLayout
            android:id="@+id/gender"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:customer_component="spinner"
            app:customer_hint="Select gender"
            app:customer_array="@array/array_gender"
            app:customer_isMandatory="true"
            app:customer_label="Gender" />
```

Auto Complete
```
<com.technovations.forminputs.FormInputLayout
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
 <com.technovations.forminputs.FormInputLayout
            android:id="@+id/fullName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:inputType="textAutoCorrect"
            app:customer_component="inputBox"
            app:customer_hint="Your full name"
            app:customer_inputType="text"
            app:customer_isMandatory="true"
            app:customer_label="Full Name" />

```

Phone Numer
```
 <com.technovations.forminputs.FormInputLayout
            android:id="@+id/phoneNumber"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:inputType="textAutoCorrect"
            app:customer_component="inputBox"
            app:customer_hint="Your phone number"
            app:customer_inputType="phoneNumber"
            app:customer_isMandatory="true"
            app:customer_label="Phone Number" />
```


Number
```
 <com.technovations.forminputs.FormInputLayout
            android:id="@+id/ID"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:inputType="textAutoCorrect"
            app:customer_component="inputBox"
            app:customer_hint="Your ID number"
            app:customer_inputType="number"
            app:customer_isMandatory="false"
            app:customer_label="ID Number" />
```

Email
```
        <com.technovations.forminputs.FormInputLayout
            android:id="@+id/email"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:inputType="textAutoCorrect"
            app:customer_component="inputBox"
            app:customer_hint="Your email address"
            app:customer_inputType="email"
            app:customer_isMandatory="true"
            app:customer_label="Email" />
```


Mault line
```
        <com.technovations.forminputs.FormInputLayout
            android:id="@+id/about"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:inputType="text"
            app:customer_component="inputBox"
            app:customer_height="250"
            app:customer_hint="About you"
            app:customer_isMandatory="true"
            app:customer_isMultiLine="true"
            app:customer_label="About you"
            app:customer_maxLength="500"
            app:customer_maxLines="20" />
```


Password
```
        <com.technovations.forminputs.FormInputLayout
            android:id="@+id/password"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:inputType="textPassword"
            app:customer_component="password"
            app:customer_hint="Your password"
            app:customer_inputType="text"
            app:customer_isMandatory="true"
            app:customer_label="Password"
            app:customer_showPassStrength="true" />
```


Button
```
        <com.technovations.forminputs.FormInputLayout
            android:id="@+id/btnSubmit"
            android:layout_width="200dp"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_marginTop="16dp"
            app:customer_component="button" />
```
