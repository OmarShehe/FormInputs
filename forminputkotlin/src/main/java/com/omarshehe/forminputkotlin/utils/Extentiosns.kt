package com.omarshehe.forminputkotlin.utils

import android.text.Spanned
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.omarshehe.forminputkotlin.R




fun String?.ifNullSetThis(default: String):String{
    return this ?: default
}
fun String.toHtml() : Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}