package com.omarshehe.forminputs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.android.material.textfield.TextInputLayout
import com.omarshehe.forminputkotlin.FormInputMaterialText
import kotlinx.android.synthetic.main.activity_material_view.*

class MaterialView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_view)

        txtFullName.setTextInputLayout(fullNameView)
        txtEmail.setTextInputLayout(emailView)
        txtPhoneNumber.setTextInputLayout(phoneNumberView)
        btnSubmit.setOnClickListener {
            if(!txtFullName.isError(mainView) && !txtEmail.isError(mainView) && !txtPhoneNumber.isError(mainView)){
                btnSubmit.showLoading(true)
                Handler().postDelayed({
                    btnSubmit.showLoading(false)
                }, 1000)
            }
        }

        txtFullName.setOnViewClickListener(object : FormInputMaterialText.OnClickListener {
            override fun onClick() {
            }
        })


    }
}
