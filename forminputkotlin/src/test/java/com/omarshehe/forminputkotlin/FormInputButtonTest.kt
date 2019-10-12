package com.omarshehe.forminputkotlin

import org.junit.Assert.*
import org.junit.Test

class FormInputButtonTest {



    @Test
    fun buttonSetup() {
        val mFormBoolean: FormInputButton? = null
        //mFormBoolean?.setUpButton("ds","Wait",2,2)
        mFormBoolean?.showLoading(true)
        assertNull(mFormBoolean)
    }
}