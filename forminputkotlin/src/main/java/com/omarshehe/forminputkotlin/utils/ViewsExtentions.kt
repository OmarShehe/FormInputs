package com.omarshehe.forminputkotlin.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.omarshehe.forminputkotlin.R

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

infix fun View.visibleIf(boolean: Boolean) {
    visibility = if (boolean) View.VISIBLE else View.GONE
}

fun View?.isNotNull(): Boolean {
    return this!=null
}

fun View.getDrawable(@DrawableRes drawableId:Int): Drawable? {
    return VectorDrawableCompat.create(resources, drawableId, null)
}
fun View.getDimension(@DimenRes dimension: Int):Int {
    return resources.getDimension(dimension).toInt()
}
fun View?.hideKeyboard() {
    this?.context?.getSystemService(Context.INPUT_METHOD_SERVICE)?.apply {
        val imm=this as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

fun EditText.textColor(@ColorRes color: Int) {
    setTextColor(ContextCompat.getColor(context,color))
}
fun EditText.hintTextColor(@ColorRes color: Int){
    setHintTextColor(ContextCompat.getColor(context,color))
}

fun TextView.textColor(color: Int) {
    setTextColor(ContextCompat.getColor(context,color))
}

fun AppCompatImageView.changeIconState(state: Boolean) {
    val animFromDoneToClose: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(context, R.drawable.arrow_down_to_up)
    val animFromCloseToDone: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(context, R.drawable.arrow_up_to_down)
    val animation = if (state) animFromCloseToDone else animFromDoneToClose
    if (animation == this.drawable) return
    this.setImageDrawable(animation)
    animation?.start()
}


fun AppCompatImageView.showDoneIcon(visibility: Boolean) {
    if(this.isVisible!=visibility){
        this.visibleIf(visibility)
        val animCheckIcon: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(context, R.drawable.check_anim)
        if (animCheckIcon == this.drawable) return
        this.setImageDrawable(animCheckIcon)
        animCheckIcon?.start()
    }
    this.visibleIf(visibility)
}