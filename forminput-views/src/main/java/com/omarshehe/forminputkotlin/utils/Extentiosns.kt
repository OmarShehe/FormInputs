package com.omarshehe.forminputkotlin.utils

import android.text.Spanned
import androidx.core.text.HtmlCompat


fun Any?.isNotNull(): Boolean = this!=null


fun String?.ifNullSetThis(default: String):String{
    return this ?: default
}
fun String.toHtml() : Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

inline fun Boolean.isTrue(action: () -> Unit): Boolean {
    if(this){
        action()
    }
    return this
}

inline fun Boolean.isNotTrue(action: () -> Unit) {
    if(!this){
        action()
    }
}

fun Int?.isGreaterThan(number: Int): Boolean {
    return if(this.isNotNull()){
        this!! > number
    }else{
        false
    }
}
