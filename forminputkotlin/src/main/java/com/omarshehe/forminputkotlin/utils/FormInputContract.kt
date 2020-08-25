package com.omarshehe.forminputkotlin.utils

interface FormInputContract {
    interface Presenter {
        /**
         * Validate email address
         */
        fun isValidEmail(email: CharSequence): Boolean

        /**
         * Validate phone number
         */
        fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean

        /**
         * Validate url
         */
        fun isValidUrl(url:CharSequence) :Boolean

        /**
         * For [FormInputPin], append the pin in one string
         */
        fun appendPin(first: String, second: String, third: String, fourth: String): String

        fun isValidNumber(number: String): Boolean
    }
}