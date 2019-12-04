package com.omarshehe.forminputkotlin.utils

interface FormInputContract {
    interface Presenter {
        fun isValidEmail(email: CharSequence): Boolean

        fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean

        fun isValidUrl(url:CharSequence) :Boolean
    }
}