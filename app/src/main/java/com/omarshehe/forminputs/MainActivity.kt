package com.omarshehe.forminputs

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.omarshehe.forminputkotlin.FormInputSpinner
import com.omarshehe.forminputkotlin.interfaces.SpinnerSelectionListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // set view to confirm the value
        confirmPassword.setViewToConfirm(password)
        confirmEmail.setViewToConfirm(email)
        confirmPin.setViewToConfirm(pin)

        //startActivity(Intent(this, MaterialView::class.java))
        btnSubmit.setOnClickListener {
            if (!gender.isError(mainView) &&
                !country.isError(mainView) &&
                !txtUrl.isError(mainView) &&
                !fullName.isError(mainView) &&
                !price.isError(mainView)  &&
                !phoneNumber.isError(mainView)  &&
                !ID.isError(mainView)  &&
                !about.isError(mainView) &&
                !email.isError(mainView) &&
                !confirmEmail.isError(mainView) &&
                !password.isError(mainView) &&
                !confirmPassword.isError(mainView) &&
                !pin.isError(mainView) &&
                !confirmPin.isError(mainView)) {

                btnSubmit.showLoading(true)
                Handler().postDelayed({
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

    }
}
