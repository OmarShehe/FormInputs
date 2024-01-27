package com.omarshehe.forminputs

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.omarshehe.forminputkotlin.interfaces.ItemSelectedListener
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import com.omarshehe.forminputs.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {


            // set view to confirm the value
            confirmPassword.setViewToConfirm(password)
            confirmEmail.setViewToConfirm(email)
            confirmPin.setViewToConfirm(pin)

            pin.setValues("1", "2", "3", "4")

            //startActivity(Intent(this, MaterialView::class.java))
            btnSubmit.setOnClickListener {
                if (areAllFieldsValid()) {
                    btnSubmit.showLoading(true)
                    Handler(mainLooper).postDelayed({
                        btnSubmit.showLoading(false)
                        startActivity(Intent(this@MainActivity, Programmatically::class.java))
                    }, 1000)
                }
            }

            gender.setOnSpinnerItemSelected(object : ItemSelectedListener {
                override fun onItemSelected(item: String) {
                    Toast.makeText(baseContext, item, Toast.LENGTH_LONG).show()
                }
            })

            fullName.setOnTextChangeListener(object : OnTextChangeListener {
                override fun onTextChange(value: String) {
                    Toast.makeText(baseContext, value, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    /**
     * Check errors
     */
    private fun areAllFieldsValid(): Boolean = with(binding) {
        return gender.noError(mainView) && country.noError(mainView) && txtUrl.noError(mainView) && fullName.noError(
            mainView
        ) && price.noError(mainView) && phoneNumber.noError(mainView) && ID.noError(
            mainView
        ) && about.noError(mainView) && email.noError(mainView) && confirmEmail.noError(mainView) && password.noError(
            mainView
        ) && confirmPassword.noError(mainView) && pin.noError(mainView) && confirmPin.noError(
            mainView
        )
    }
}
