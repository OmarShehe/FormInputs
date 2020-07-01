package com.omarshehe.forminputkotlin.utils

import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.omarshehe.forminputkotlin.R

fun AppCompatImageView.changeIconState(state: Boolean) {
    val animFromDoneToClose: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(context, R.drawable.arrow_down_to_up)
    val animFromCloseToDone: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(context, R.drawable.arrow_downtoup)
    val animation = if (state) animFromCloseToDone else animFromDoneToClose
    if (animation == this.drawable) return
    this.setImageDrawable(animation)
    animation?.start()
}

fun View.showOrHide(visible: Boolean) {
    visibility=if (visible) View.VISIBLE else View.GONE
}
fun View?.isNotNull(): Boolean {
    return this!=null
}
fun View.gone(){
    visibility=View.GONE
}
fun View.visible(){
    visibility=View.VISIBLE
}

fun EditText.textColor(color: Int) {
    setTextColor(ContextCompat.getColor(context,color))
}

fun String?.ifNullSetThis(default: String):String{
    return this ?: default
}
