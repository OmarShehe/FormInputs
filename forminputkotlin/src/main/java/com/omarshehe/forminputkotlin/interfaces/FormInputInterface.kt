package com.omarshehe.forminputkotlin.interfaces

interface ViewOnClickListener {
    fun onClick()
}
interface OnTextChangeListener {
    fun onTextChange(value: String)
}

interface ItemSelectedListener {
    fun onItemSelected(item: String)
}