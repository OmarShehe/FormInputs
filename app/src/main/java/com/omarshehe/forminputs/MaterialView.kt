package com.omarshehe.forminputs

import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_material_view.*

class MaterialView : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_view)

        spGender.setTextInputLayout(gender)
        txtFullName.setTextInputLayout(fullNameView)
        txtEmail.setTextInputLayout(emailView)
        txtPhoneNumber.setTextInputLayout(phoneNumberView)
        txtAbout.setTextInputLayout(about)
        btnSubmit.setOnClickListener {
            if(txtFullName.noError(mainView) && txtEmail.noError(mainView) && txtPhoneNumber.noError(mainView)){
                btnSubmit.showLoading(true)
                Handler(mainLooper).postDelayed({
                    btnSubmit.showLoading(false)
                }, 1000)
            }
        }
    }
}
