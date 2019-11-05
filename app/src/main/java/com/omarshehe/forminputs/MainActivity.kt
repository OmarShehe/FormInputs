package com.omarshehe.forminputs

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.omarshehe.forminputkotlin.FormInputMaterialText
import com.omarshehe.forminputkotlin.FormInputSpinner
import com.omarshehe.forminputkotlin.FormInputText
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),FormInputSpinner.SpinnerSelectionListener {
    override fun onSpinnerItemSelected(item: String) {
        Toast.makeText(baseContext,item,Toast.LENGTH_LONG).show()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        confirmPassword.setConfirmPassword(password)
        txtMaterialText.setTextInputLayout(vi as TextInputLayout)

        btnSubmit.getButton().setOnClickListener {
            run {
                if (!gender.isError(mainView) && !country.isError((mainView)) && !fullName.isError(mainView) && !price.isError(mainView)  && !phoneNumber.isError(mainView)  && !about.isError(mainView) && !email.isError(mainView) && !password.isError(mainView)) {
                    btnSubmit.showLoading(true)
                    Handler().postDelayed({
                        btnSubmit.showLoading(false)
                        startActivity(Intent(this, Programmatically::class.java))
                    }, 1000)
                }
            }
        }

        fullName.setOnViewClickListener(object : FormInputText.OnClickListener{
            override fun onClick() {
            }
        })

        txtMaterialText.setOnViewClickListener(object : FormInputMaterialText.OnClickListener {
            override fun onClick() {
            }
        })
        gender.setOnSpinnerItemSelected(object :FormInputSpinner.SpinnerSelectionListener{
            override fun onSpinnerItemSelected(item: String) {
            }

        })
    }


}
