package com.omarshehe.forminputkotlin.utils

import com.omarshehe.forminputkotlin.R

class  PasswordStrength {
    enum class PassLevel(resId: Int, color: Int) {
        Weak(0, R.color.colorOnError),Fair(2, R.color.colorOrange), Good(3, R.color.colorBlue), Strong(4, R.color.colorGreen);
        private var mResId: Int = resId
        private var mColor: Int = color

        fun getValue(): Int {
            return mResId
        }

        fun getColor(): Int {
            return mColor
        }

    }
    fun calculateStrength(maxLength:Int,input: String): ArrayList<Any> {
        var result =0
        var upperCasePresent = false
        var specialPresent = false
        var numberPresent = false
        var lengthPresent = false


        for (element in input) {
            when {
                Character.isUpperCase(element) -> upperCasePresent = true
                !Character.isLetterOrDigit(element) && !Character.isWhitespace(element) -> specialPresent = true
                Character.isDigit(element) -> numberPresent = true
                input.length >= maxLength -> lengthPresent = true
            }
        }

        if (upperCasePresent) ++result
        if (specialPresent) ++result
        if (numberPresent) ++result
        if (lengthPresent) ++result

        val level= when (result) {
            0 -> PassLevel.Weak
            1 -> PassLevel.Weak
            2 -> PassLevel.Fair
            3 -> PassLevel.Good
            else -> PassLevel.Strong
        }
        val resultArray=ArrayList<Any>()
        resultArray.add(level)
        resultArray.add(result)
        resultArray.add(upperCasePresent)
        resultArray.add(specialPresent)
        resultArray.add(numberPresent)
        resultArray.add(lengthPresent)
        return resultArray

    }

}