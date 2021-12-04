package com.omarshehe.forminputs

import android.os.Bundle
import android.os.Handler
import com.omarshehe.forminputkotlin.interfaces.ItemSelectedListener
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import kotlinx.android.synthetic.main.activity_material_view.*

class MaterialView: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_view)

        spGender.setTextInputLayout(gender)
        fullNameView.setOnTextChangeListener(onTextChangeListener)
        emailView.setOnTextChangeListener(onTextChangeListener)
        phoneNumberView.setOnTextChangeListener(onTextChangeListener)
        IdView.setOnTextChangeListener(onTextChangeListener)
        about.setOnTextChangeListener(onTextChangeListener)

        btnSubmit.isEnabled = areAllFieldsValid()
        btnSubmit.setOnClickListener {
            if (areAllFieldsValid(true)) {
                btnSubmit.showLoading(true)
                Handler(mainLooper).postDelayed({
                    btnSubmit.showLoading(false)
                }, 1000)
            }
        }

        spGender.setOnSpinnerItemSelected(object: ItemSelectedListener {
            override fun onItemSelected(item: String) {
                btnSubmit.isEnabled = areAllFieldsValid()
            }
        })
    }

    private val onTextChangeListener = object: OnTextChangeListener {
        override fun onTextChange(value: String) {
            btnSubmit.isEnabled = areAllFieldsValid()
        }
    }

    /**
     * Check errors
     */
    private fun areAllFieldsValid(showError: Boolean = false): Boolean {
        return spGender.noError(mainView, showError) && fullNameView.noError(mainView, showError) && emailView.noError(mainView, showError) && phoneNumberView.noError(
            mainView, showError) && IdView.noError(
            mainView, showError) && about.noError(mainView, showError)
    }
}
