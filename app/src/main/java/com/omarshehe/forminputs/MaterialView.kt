package com.omarshehe.forminputs

import android.os.Bundle
import android.os.Handler
import com.omarshehe.forminputkotlin.interfaces.ItemSelectedListener
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import kotlinx.android.synthetic.main.activity_material_view.*

class MaterialView : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_view)

        spGender.setTextInputLayout(gender)
        txtFullName.apply {
            setTextInputLayout(fullNameView)
            setOnTextChangeListener(onTextChangeListener)
        }
        txtEmail.apply {
            setTextInputLayout(emailView)
            setOnTextChangeListener(onTextChangeListener)
        }
        txtPhoneNumber.apply {
            setTextInputLayout(phoneNumberView)
            setOnTextChangeListener(onTextChangeListener)
        }
        txtId.apply {
            setTextInputLayout(IdView)
            setOnTextChangeListener(onTextChangeListener)
        }
        txtAbout.apply {
            setTextInputLayout(about)
            setOnTextChangeListener(onTextChangeListener)
        }

        btnSubmit.isEnabled=isAllFieldAreValid()

        btnSubmit.setOnClickListener {
            if(isAllFieldAreValid(true)){
                btnSubmit.showLoading(true)
                Handler(mainLooper).postDelayed({
                    btnSubmit.showLoading(false)
                }, 1000)
            }
        }

        spGender.setOnSpinnerItemSelected(object :ItemSelectedListener{
            override fun onItemSelected(item: String) {  btnSubmit.isEnabled=isAllFieldAreValid() }
        })
    }



    private val onTextChangeListener=object : OnTextChangeListener {
        override fun onTextChange(value: String) {
            btnSubmit.isEnabled=isAllFieldAreValid()
        }
    }


    /**
     * Check errors
     */
    private fun isAllFieldAreValid(showError:Boolean=false):Boolean{
        return spGender.noError(mainView,showError)
                && txtFullName.noError(mainView,showError)
                && txtEmail.noError(mainView,showError)
                && txtPhoneNumber.noError(mainView,showError)
                && txtId.noError(mainView,showError)
                && txtAbout.noError(mainView,showError)
    }
}
