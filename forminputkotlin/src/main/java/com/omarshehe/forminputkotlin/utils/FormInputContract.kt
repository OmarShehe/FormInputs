package com.omarshehe.forminputkotlin.utils

interface FormInputContract {
    interface View
    interface Presenter {
        fun isValidEmail(email: CharSequence): Boolean

        fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean

        fun isValidUrl(url:CharSequence) :Boolean
    }
}