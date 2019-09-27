package com.omarshehe.forminputkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Parcelable
import android.transition.*
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.omarshehe.forminputkotlin.utils.SavedState
import kotlinx.android.synthetic.main.form_input_button.view.*


class FormInputButton:RelativeLayout{

    private val TAG :String ="FormInputButtonA"
    private var mValue : String = ""
    private var mValueOnLoad : String = ""
    private var isShowProgress : Boolean = false
    private var mBackground: Int =R.drawable.bg_btn_click
    private var mTextColor: Int =R.color.white
    private var mProgressColor: Int =R.color.colorRed


    constructor(context: Context?) : super(context)
    @SuppressLint("Recycle", "CustomViewStyleable")
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        val mInFlater= LayoutInflater.from(context).inflate(R.layout.form_input_button,this,true)
        if(context!=null){
            val a=context.obtainStyledAttributes(attrs,R.styleable.FormInputLayout)
            mValue=a.getString(R.styleable.FormInputLayout_customer_value) as String
            mValueOnLoad=a.getString(R.styleable.FormInputLayout_customer_valueOnLoad) as String
            isShowProgress=a.getBoolean(R.styleable.FormInputLayout_customer_showProgress, false)
            mBackground=a.getResourceId(R.styleable.FormInputLayout_customer_background,R.drawable.bg_btn_click)
            mTextColor=a.getResourceId(R.styleable.FormInputLayout_customer_textColor,R.color.white)
            mProgressColor=a.getResourceId(R.styleable.FormInputLayout_customer_progressColor,R.color.white)

            setUpButton(mValue,mValueOnLoad,mTextColor,mProgressColor)
        }


    }

    fun getButton(): Button {
        return btnNoImage
    }

    fun setUpButton(value: String,valueOnLoad : String, textColor: Int , progressColor : Int) {
        btnNoImage.text=value
        mValueOnLoad=valueOnLoad
        showLoading(isShowProgress)
        setBgBackground(mBackground)
        setTextColor(textColor)
        setProgressColor(progressColor)
    }


    fun showLoading(visibility: Boolean) {
        if (visibility) {
            btnProgressBar.visibility= VISIBLE
            btnNoImage.text=mValueOnLoad
            btnNoImage.isEnabled = false
        } else {
            btnProgressBar.visibility= GONE
            btnNoImage.text = mValue
            btnNoImage.isEnabled = true
        }
    }


    private fun setBgBackground(background: Int) {
        btnNoImage.setBackgroundResource(background)
    }

    private fun setProgressColor(color: Int) {
        btnProgressBar.indeterminateDrawable.setColorFilter(resources.getColor(color), android.graphics.PorterDuff.Mode.SRC_ATOP)
    }

    private fun setTextColor(color: Int) {
        btnNoImage.setTextColor(resources.getColor(color))
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun animateLoading(){
        TransitionManager.beginDelayedTransition(lytButton,Slide())

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