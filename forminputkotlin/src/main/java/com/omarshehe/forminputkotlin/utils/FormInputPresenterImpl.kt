package com.omarshehe.forminputkotlin.utils

import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import java.util.*

class FormInputPresenterImpl(view: FormInputContract.View?) : FormInputContract.Presenter {


    var mView: FormInputContract.View? = view

    override fun isValidEmail(email: CharSequence): Boolean {
        return (!TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        return if (phoneNumber != "") {
            if (phoneNumber.length == 10 || phoneNumber.length == 13) {
                Patterns.PHONE.matcher(phoneNumber).matches()
            } else {
                false
            }
        } else {
            false
        }
    }


    override fun isValidUrl(url: CharSequence): Boolean {
        return if (url != "") {
            val p = Patterns.WEB_URL
            val m = p.matcher(url.toString().toLowerCase(Locale(toString())))
            m.matches()
        } else {
            false
        }
    }


}