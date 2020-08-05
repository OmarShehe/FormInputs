package com.omarshehe.forminputkotlin.utils

import android.text.TextUtils
import android.util.Patterns
import android.webkit.URLUtil
import java.util.*

class FormInputPresenterImpl : FormInputContract.Presenter {




    override fun isValidEmail(email: CharSequence): Boolean {
        return (!TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        return if (phoneNumber.isNotBlank()) {
            if (phoneNumber.length == 10 || phoneNumber.length == 13) {
                Patterns.PHONE.matcher(phoneNumber).matches()
            } else {
                false
            }
        } else {
            false
        }
    }


    override fun isValidUrl(input: CharSequence): Boolean {
        return if (input.isNotBlank()) {
            URLUtil.isValidUrl(input.toString()) && Patterns.WEB_URL.matcher(input).matches()
        } else {
            false
        }
    }

    override fun appendPin(first: String, second: String, third: String, fourth: String): String {
        return StringBuilder().append(first).append(second).append(third).append(fourth).toString()
    }
}