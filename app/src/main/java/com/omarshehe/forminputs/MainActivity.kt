package com.omarshehe.forminputs

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import com.omarshehe.forminputkotlin.interfaces.SpinnerSelectionListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set view to confirm the value
        confirmPassword.setViewToConfirm(password)
        confirmEmail.setViewToConfirm(email)
        confirmPin.setViewToConfirm(pin)

        pin.setValues("1","2","3","4")

        //startActivity(Intent(this, MaterialView::class.java))
        btnSubmit.setOnClickListener {
            if (gender.noError(mainView) &&
                country.noError(mainView) &&
                txtUrl.noError(mainView) &&
                fullName.noError(mainView) &&
                price.noError(mainView)  &&
                phoneNumber.noError(mainView)  &&
                ID.noError(mainView)  &&
                about.noError(mainView) &&
                email.noError(mainView) &&
                confirmEmail.noError(mainView) &&
                password.noError(mainView) &&
                confirmPassword.noError(mainView) &&
                pin.noError(mainView) &&
                confirmPin.noError(mainView)) {

                btnSubmit.showLoading(true)
                Handler(mainLooper).postDelayed({
                    btnSubmit.showLoading(false)
                    startActivity(Intent(this, Programmatically::class.java))
                }, 1000)
            }
        }

        gender.setOnSpinnerItemSelected(object :SpinnerSelectionListener{
            override fun onSpinnerItemSelected(item: String) {
                Toast.makeText(baseContext,item,Toast.LENGTH_LONG).show()
            }
        })

        fullName.setOnTextChangeListener(object :OnTextChangeListener{
            override fun onTextChange(value: String) {
                Toast.makeText(baseContext,value,Toast.LENGTH_LONG).show()
            }

        })

    }
}
