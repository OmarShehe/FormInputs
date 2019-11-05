package com.omarshehe.forminputkotlin.utils

import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import android.view.View

class SavedState : View.BaseSavedState {
    var childrenStates: SparseArray<Parcelable>? = null
    constructor(superState: Parcelable?) : super(superState)
    constructor(source: Parcel) : super(source) {
        childrenStates = source.readSparseArray<Parcelable>(javaClass.classLoader)
    }
    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeSparseArray(childrenStates as SparseArray<*>)
    }
    companion object { @JvmField
        internal val CREATOR = object : Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel): SavedState? = SavedState(source)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}