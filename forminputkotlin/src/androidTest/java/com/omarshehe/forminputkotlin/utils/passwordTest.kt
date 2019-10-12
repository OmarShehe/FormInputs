package com.omarshehe.forminputkotlin.utils

import android.util.Log
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test

import org.junit.Assert.*
import android.util.Log.WARN
import org.junit.Rule
import java.io.PrintStream


/**
 * Created by omars on 10/10/2019.
 * Author omars
 */
class passwordTest {

    @Test
    fun m(){
        val pass=PasswordStrength().calculateStrength(8,"!Oomas")
        assertEquals("Check Character :",1, pass[0].toString())

    }
}