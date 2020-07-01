package com.omarshehe.forminputkotlin.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.omarshehe.forminputkotlin.R

/**
 * Created by omars on 10/1/2019.
 * Author omars
 */
object Utils {
    fun setLabel(txtView : TextView, label: String,isMandatory: Boolean) :String {
        if (label != "") {
            val mandatory= if(isMandatory) "*" else ""
            txtView.text = HtmlCompat.fromHtml(String.format(txtView.context.getString(R.string.label),label,mandatory), HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            val mandatory= if(isMandatory) "*" else ""
            txtView.text = HtmlCompat.fromHtml(String.format(txtView.context.getString(R.string.label),"",mandatory), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        return label
    }

    fun setViewVisibility(view: View, shouldShow: Boolean) :Boolean{
        view.visibility = if (shouldShow) VISIBLE else GONE
        return shouldShow
    }


    fun showInputError(txtView : TextView,viewNoError: AppCompatImageView,showNoErrorIcon: Boolean, stringError: String, visible: Int): Array<Any>  {
        txtView.text = stringError
        txtView.visibility = visible
        val intError = if (visible == VISIBLE) {
            showDoneIcon(viewNoError.context,viewNoError,false)
            1
        } else {
            showDoneIcon(viewNoError.context,viewNoError,showNoErrorIcon)
            0
        }

        return arrayOf(stringError, intError)
    }

    fun hideKeyboard(context: Context) {
        val activity: Activity = context as Activity
        val view: View? = activity.currentFocus
        (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view?.windowToken, 0)

    }


    private fun showDoneIcon(context: Context,view: AppCompatImageView,shouldShow: Boolean) {
        if(view.isVisible!=shouldShow){
            setViewVisibility(view, shouldShow)
            val animCheckIcon: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(context, R.drawable.check_anim)

            if (animCheckIcon == view.drawable) return
            view.setImageDrawable(animCheckIcon)
            animCheckIcon?.start()
        }
        setViewVisibility(view, shouldShow)


    }

}