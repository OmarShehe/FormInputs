package com.omarshehe.forminputkotlin

import java.util.ArrayList

interface FormInputLayoutContract {
    interface View
    interface Presenter {
        val timeSlots: ArrayList<String>

        fun pad(input: Int): String

        fun isValidEmail(email: CharSequence): Boolean

        fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean
    }
}