package com.omarshehe.forminputs

import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.omarshehe.forminputkotlin.*
import com.omarshehe.forminputkotlin.BaseFormInput.Companion.INPUT_TYPE_EMAIL
import com.omarshehe.forminputkotlin.BaseFormInput.Companion.INPUT_TYPE_NUMBER
import com.omarshehe.forminputkotlin.BaseFormInput.Companion.INPUT_TYPE_PHONE
import com.omarshehe.forminputkotlin.BaseFormInput.Companion.INPUT_TYPE_TEXT
import com.omarshehe.forminputkotlin.utils.Density
import kotlinx.android.synthetic.main.activity_programmatical.*

class Programmatically : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatical)





       val getGenderArray = resources.getStringArray(R.array.array_gender)
        val spGender = FormInputSpinner(this).apply {
            setID(1)
            setLabel("Gender")
            setHint("Select gender")
            setMandatory(true)
            setAdapter(listOf(*getGenderArray))
            showValidIcon(true)
        }


        val countryList = resources.getStringArray(R.array.array_country)
        val country = FormInputAutoComplete(this).apply {
            setID(2)
            setLabel("Country")
            setHint("Select your country")
            setMandatory(true)
            setAdapter(ArrayList(listOf(*countryList)))
            showValidIcon(true)
            setPadding(0,50,0,0)
        }


        val fullName = FormInputText(applicationContext)
        fullName.setInputType(INPUT_TYPE_TEXT).apply {
            setID(3)
            setHint("Your full name")
            setLabel("Full Name")
            setMandatory(true)
            setPadding(0,50,0,0)
        }


        val currencyArray = resources.getStringArray(R.array.array_currency)
        val price = FormInputSpinnerInputBox(this).apply {
            setInputType(INPUT_TYPE_NUMBER)
            setID(4)
            setHint("Your price")
            setLabel("Price")
            setSpinner(listOf(*currencyArray))
            setMandatory(true)
            setPadding(0,50,0,0)
        }






        val phoneNumber = FormInputText(applicationContext)
        phoneNumber.setInputType(INPUT_TYPE_PHONE).apply {
            setID(5)
            setHint("Your phone number")
            setLabel("Phone Number")
            setMandatory(true)
            setPadding(0,50,0,0)
        }


        val idNumber = FormInputText(applicationContext)
        idNumber.setInputType(INPUT_TYPE_NUMBER).apply {
            setID(6)
            setHint("Your ID number")
            setLabel("ID Number")
            setMandatory(true)
            setPadding(0,50,0,0)
        }


        val about = FormInputMultiline(applicationContext).apply {
            setID(7)
            setHint("About you")
            setLabel("About you")
            setMandatory(true)
            setHeight(resources.getDimensionPixelSize(R.dimen.formInputInput_box_height))
            setMaxLength(500)
            setPadding(0,50,0,0)
        }


        val email = FormInputText(applicationContext)
        email.setInputType(INPUT_TYPE_EMAIL).apply {
            setID(8)
            setHint("Your email address")
            setLabel("Email")
            setMandatory(true)
           setPadding(0,50,0,0)
        }


        val password = FormInputPassword(this).apply {
            setID(9)
            setHint("Your password")
            setLabel("Password")
            setMandatory(true)
            showPassStrength(true)
            setPadding(0,50,0,0)
        }


        val param=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,Density.dp2px(resources,60f))
        param.gravity=Gravity.CENTER
        val btnSubmit= FormInputButton(this).apply {
            setValue("Send")
            showProgressOnClick(true)
            cornerRadius=Density.dp2px(resources,60f)
            layoutParams=param
        }






        mainView.apply {
            addView(spGender)
            addView(country)
            addView(fullName)
            addView(price)
            addView(phoneNumber)
            addView(idNumber)
            addView(about)
            addView(email)
            addView(password)
            addView(btnSubmit)
        }


        btnSubmit.setOnClickListener{
            btnSubmit.showLoading(true)
            Handler().postDelayed({
                Toast.makeText(applicationContext,"Submit",Toast.LENGTH_LONG).show()
                btnSubmit.showLoading(false)
            },1000)

        }

    }
}
