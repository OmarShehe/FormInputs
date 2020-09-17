package com.omarshehe.forminputkotlin.interfaces

interface ViewOnClickListener {
    fun onClick()
}
interface OnTextChangeListener {
    fun onTextChange(value: String)
}

interface SpinnerSelectionListener {
    fun onSpinnerItemSelected(item: String)
}

interface ItemSelectedListener {
    fun onItemSelected(item: String)
}