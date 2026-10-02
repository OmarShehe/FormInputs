package com.omarshehe.forminputkotlin.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView


class AutoCompleteView : AppCompatAutoCompleteTextView{
    private var mShowAlways: Boolean = false
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor( context: Context, attrs: AttributeSet?, defStyleAttr: Int ) : super(context, attrs,defStyleAttr)
    fun setShowAlways (showAlways: Boolean){
        mShowAlways=showAlways
    }

   override fun enoughToFilter(): Boolean {
        return mShowAlways || super.enoughToFilter()

    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        showDropDownIfFocused()
    }

    private fun showDropDownIfFocused() {
       if (enoughToFilter() && isFocused && windowVisibility == View.VISIBLE) {
            showDropDown()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        showDropDownIfFocused()
    }

    override fun setOnCreateContextMenuListener(l: OnCreateContextMenuListener?) {
        super.setOnCreateContextMenuListener(l)

    }


}