package com.omarshehe.forminputs

import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.omarshehe.forminputkotlin.*
import com.omarshehe.forminputkotlin.utils.Density
import kotlinx.android.synthetic.main.activity_programmatical.*

class Programmatically : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatical)





       val getGenderArray = resources.getStringArray(R.array.array_gender)
        val spGender = FormInputSpinner(this)
            .setID(1)
            .setLabel("Gender")
            .setHint("Select gender")
            .setMandatory(true)
            .setAdapter(listOf(*getGenderArray))
            .showValidIcon(true)

        val countryList = resources.getStringArray(R.array.array_country)
        val country = FormInputAutoComplete(this)
            .setID(2)
            .setLabel("Country")
            .setHint("Select your country")
            .setMandatory(true)
            .setAdapter(ArrayList(listOf(*countryList)))
            .showValidIcon(true)
        country.setPadding(0,50,0,0)

        val fullName = FormInputText(applicationContext)
        fullName.setInputType(fullName.INPUTTYPE_TEXT)
            .setID(3)
            .setHint("Your full name")
            .setLabel("Full Name")
            .setMandatory(true)
        fullName.setPadding(0,50,0,0)

        val currencyArray = resources.getStringArray(R.array.array_currency)
        val price = FormInputSpinnerInputBox(this)
        price.setInputType(fullName.INPUTTYPE_NUMBER)
            .setID(4)
            .setHint("Your price")
            .setLabel("Price")
            .setSpinner(listOf(*currencyArray))
            .setMandatory(true)

        price.setPadding(0,50,0,0)



        val phoneNumber = FormInputText(applicationContext)
        phoneNumber.setInputType(phoneNumber.INPUTTYPE_PHONE)
            .setID(5)
            .setHint("Your phone number")
            .setLabel("Phone Number")
            .setMandatory(true)
        phoneNumber.setPadding(0,50,0,0)

        val idNumber = FormInputText(applicationContext)
        idNumber.setInputType(idNumber.INPUTTYPE_NUMBER)
            .setID(6)
            .setHint("Your ID number")
            .setLabel("ID Number")
            .setMandatory(true)
        idNumber.setPadding(0,50,0,0)

        val about = FormInputMultiline(applicationContext)
            .setID(7)
            .setHint("About you")
            .setLabel("About you")
            .setMandatory(true)
            .setHeight(100)
            .setMaxLength(500)
        about.setPadding(0,50,0,0)

        val email = FormInputText(applicationContext)
        email.setInputType(email.INPUTTYPE_EMAIL)
            .setID(8)
            .setHint("Your email address")
            .setLabel("Email")
            .setMandatory(true)
        email.setPadding(0,50,0,0)

        val password = FormInputPassword(this)
            .setID(9)
            .setHint("Your password")
            .setLabel("Password")
            .setMandatory(true)
            .showPassStrength(true)
        password.setPadding(0,50,0,0)

        val param=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,Density.dp2px(resources,60f))
        param.gravity=Gravity.CENTER
        val btnSubmit= FormInputButton(this)
        btnSubmit.setValue("Send")
        btnSubmit.showProgressOnClick(true)
        btnSubmit.cornerRadius=Density.dp2px(resources,60f)
        btnSubmit.layoutParams=param





        mainView.addView(spGender)
        mainView.addView(country)
        mainView.addView(fullName)
        mainView.addView(price)
        mainView.addView(phoneNumber)
        mainView.addView(idNumber)
        mainView.addView(about)
        mainView.addView(email)
        mainView.addView(password)
        mainView.addView(btnSubmit)


        btnSubmit.setOnClickListener{
            btnSubmit.showLoading(true)
            Handler().postDelayed({
                Toast.makeText(applicationContext,"Submit",Toast.LENGTH_LONG).show()
                btnSubmit.showLoading(false)
            },1000)

        }

    }
}
