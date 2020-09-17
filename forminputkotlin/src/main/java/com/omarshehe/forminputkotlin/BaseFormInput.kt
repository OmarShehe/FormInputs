package com.omarshehe.forminputkotlin

import android.content.Context
import android.os.Parcelable
import android.text.InputType
import android.text.Spanned
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.text.HtmlCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.omarshehe.forminputkotlin.utils.*

open class BaseFormInput : RelativeLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs,defStyleAttr)


    fun TextView.showInputError( validIcon: AppCompatImageView, showValidIcon: Boolean, stringError: String, visible: Int): Boolean  {
        text = stringError
        visibility = visible
        return if (visible == View.VISIBLE) {
            validIcon.showDoneIcon(false)
            true
        } else {
            validIcon.showDoneIcon( showValidIcon)
            false
        }
    }

    /**
     * Set label text, add red star if required
     */
    fun TextView.setLabel(label: String,isMandatory: Boolean):String {
        text = if (label.isNotBlank()) {
            val mandatory= if(isMandatory) "*" else ""
            String.format(context.getString(R.string.label),label,mandatory).toHtml()
        } else {
            val mandatory= if(isMandatory) "*" else ""
           String.format(context.getString(R.string.label),"",mandatory).toHtml()
        }
        return label
    }

    /**
     * Set Input type
     */
    fun EditText.setInputTypes(mInputType: Int) {
        when (mInputType) {
            INPUT_TYPE_TEXT -> inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            INPUT_TYPE_PHONE -> inputType = InputType.TYPE_CLASS_PHONE
            INPUT_TYPE_NUMBER ->  inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            INPUT_TYPE_EMAIL -> inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            INPUT_TYPE_URL -> inputType= InputType.TYPE_TEXT_VARIATION_URI
        }
    }



    /**
     * Save Instance State of the view
     * */
    public override fun onSaveInstanceState(): Parcelable? {
        return SavedState(super.onSaveInstanceState()).apply {
            childrenStates = saveChildViewStates()
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                state.childrenStates?.let { restoreChildViewStates(it) }
            }
            else -> super.onRestoreInstanceState(state)
        }
    }

    private fun ViewGroup.saveChildViewStates(): SparseArray<Parcelable> {
        val childViewStates = SparseArray<Parcelable>()
        children.forEach { child -> child.saveHierarchyState(childViewStates) }
        return childViewStates
    }

    private fun ViewGroup.restoreChildViewStates(childViewStates: SparseArray<Parcelable>) {
        children.forEach { child -> child.restoreHierarchyState(childViewStates) }
    }

    override
    fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }
    override
    fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }
}


