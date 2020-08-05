package com.omarshehe.forminputkotlin.utils

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
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

fun EditText.textColor(color: Int) {
    setTextColor(ContextCompat.getColor(context,color))
}

fun AppCompatImageView.changeIconState(state: Boolean) {
    val animFromDoneToClose: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(context, R.drawable.arrow_down_to_up)
    val animFromCloseToDone: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(context, R.drawable.arrow_downtoup)
    val animation = if (state) animFromCloseToDone else animFromDoneToClose
    if (animation == this.drawable) return
    this.setImageDrawable(animation)
    animation?.start()
}


fun AppCompatImageView.showDoneIcon(visibility: Boolean) {
    if(this.isVisible!=visibility){
        Utils.setViewVisibility(this, visibility)
        val animCheckIcon: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(context, R.drawable.check_anim)

        if (animCheckIcon == this.drawable) return
        this.setImageDrawable(animCheckIcon)
        animCheckIcon?.start()
    }
    this.visibleIf(visibility)
}

fun setLabel(txtView : TextView, label: String, isMandatory: Boolean) :String {
    if (label != "") {
        val mandatory= if(isMandatory) "*" else ""
        txtView.text = HtmlCompat.fromHtml(String.format(txtView.context.getString(R.string.label),label,mandatory), HtmlCompat.FROM_HTML_MODE_LEGACY)
    } else {
        val mandatory= if(isMandatory) "*" else ""
        txtView.text = HtmlCompat.fromHtml(String.format(txtView.context.getString(R.string.label),"",mandatory), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
    return label
}