package com.omarshehe.forminputkotlin.utils

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by omars on 10/10/2019.
 * Author omars
 */
class passwordTest {

    @Test
    fun m() {
        val pass = PasswordStrength().calculateStrength(8, "!Oomas")
        assertEquals("Check Character :", 1, pass[0].toString())
    }
}