package com.omarshehe.forminputkotlin.utils

import android.graphics.Color

class  PasswordStrength {

enum class PassLevel(resId: Int, color: Int) {
    Weak(0, Color.RED), Medium(1, Color.argb(255, 220, 185, 0)), Strong(2, Color.parseColor("#28A645"));
    private var mResId: Int = resId
    private var mColor: Int = color

    fun getValue(): Int {
        return mResId
    }

    fun getColor(): Int {
        return mColor
    }

}
    private var REQUIRED_LENGTH = 6
    private var MAXIMUM_LENGTH = 6
    private var REQUIRE_SPECIAL_CHARACTERS = true
    private var REQUIRE_DIGITS = true
    private var REQUIRE_LOWER_CASE = true
    private var REQUIRE_UPPER_CASE = true







    fun calculateStrength(password: String): PassLevel {
        var currentScore = 0
        var sawUpper = false
        var sawLower = false
        var sawDigit = false
        var sawSpecial = false

        for (element in password) {

            if (!sawSpecial && !Character.isLetterOrDigit(element)) {
                currentScore += 1
                sawSpecial = true
            } else {
                if (!sawDigit && Character.isDigit(element)) {
                    currentScore += 1
                    sawDigit = true
                } else {
                    if (!sawUpper || !sawLower) {
                        if (Character.isUpperCase(element))
                            sawUpper = true
                        else
                            sawLower = true
                        if (sawUpper && sawLower)
                            currentScore += 1
                    }
                }
            }
        }

        currentScore = if (password.length > REQUIRED_LENGTH) {
            if (REQUIRE_SPECIAL_CHARACTERS && !sawSpecial || REQUIRE_UPPER_CASE && !sawUpper || REQUIRE_LOWER_CASE && !sawLower || REQUIRE_DIGITS && !sawDigit) {
                1
            } else {
                2
            }
        } else {
            0
        }

        return when (currentScore) {
            0 -> PassLevel.Weak
            1 -> PassLevel.Medium
            else -> PassLevel.Strong
        }


    }

}