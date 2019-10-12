package com.omarshehe.forminputkotlin

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.omarshehe.forminputkotlin.utils.DrawableSpan
import com.omarshehe.forminputkotlin.utils.SavedState
import com.omarshehe.forminputkotlin.utils.Utils
import kotlinx.android.synthetic.main.form_input_button.view.*


class FormInputButton:RelativeLayout{
    private var mValue : String = ""
    private var mValueOnLoad : String = ""
    private var mHeight : Int = 500
    private var isShowProgress : Boolean = true
    private var mBackground: Int =R.color.colorGrey
    private var mTextColor: Int =R.color.white
    private var mProgressColor: Int =R.color.white
    private var mCornerRadius: Int =5

    private lateinit var drawableSpan: DrawableSpan
    private lateinit var spannableString: SpannableString
    private lateinit var progressDrawable :CircularProgressDrawable
    private lateinit var callback: Drawable.Callback

    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0

    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.attrs=attrs
        initView()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs,defStyleAttr) {
        this.attrs = attrs
        styleAttr=defStyleAttr
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.form_input_button, this, true)
        if (context != null) {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout, 0, 0)
            mValue = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_value))
            mValueOnLoad = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_valueOnLoad))
            mHeight = a.getDimension(R.styleable.FormInputLayout_form_height, resources.getDimension( R.dimen.input_box_height)).toInt()
            isShowProgress = a.getBoolean(R.styleable.FormInputLayout_form_showProgress, true)
            mBackground = a.getResourceId(R.styleable.FormInputLayout_form_background, R.color.colorGrey)
            mTextColor = a.getResourceId(R.styleable.FormInputLayout_form_textColor, R.color.white)
            mProgressColor = a.getResourceId(R.styleable.FormInputLayout_form_progressColor, R.color.white)
            mCornerRadius = a.getInteger(R.styleable.FormInputLayout_form_cornerRadius, 5)


            progressDrawable = CircularProgressDrawable(context).apply {
                setStyle(CircularProgressDrawable.LARGE)
                setColorSchemeColors(Color.WHITE)
                val size = (centerRadius + strokeWidth).toInt() * 2
                setBounds(0, 0, size, size)
            }

            drawableSpan = DrawableSpan(progressDrawable, 40, 20, true)

            callback = object : Drawable.Callback {
                override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                }

                override fun invalidateDrawable(who: Drawable) {
                    btnNoImage.invalidate()
                }

                override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
                }
            }

            if(mValueOnLoad.isEmpty()){
                mValueOnLoad=resources.getString(R.string.pleaseWait)
            }

            btnNoImage.text = mValue
            setCornerRadius(mCornerRadius)
            setValueOnLoad(mValueOnLoad)
            height()
            setBackground(mBackground)
            setTextColor(mTextColor)
            a.recycle()
        }
    }



    /**
     * Set components
     */
    fun setValue(value: String) :FormInputButton {
        mValue = value
        btnNoImage.text = value
        return this
    }
    fun setValueOnLoad(value: String) :FormInputButton {
        mValueOnLoad = value
        spannableString = SpannableString(mValueOnLoad).apply {
            setSpan(drawableSpan,  length- 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return this
    }

    private fun height(){
        btnNoImage.layoutParams=LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight)
    }

    fun setHeight(height: Int) : FormInputButton {
        mHeight = (height * Resources.getSystem().displayMetrics.density).toInt()
        btnNoImage.layoutParams=LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight)
        return this
    }

    fun showProgressOnClick(show: Boolean) : FormInputButton {
        isShowProgress=show
        return this
    }

    fun setBackground(backgroundColor: Int) : FormInputButton {
        btnNoImage.backgroundTintList=ContextCompat.getColorStateList(context,backgroundColor)
        return this
    }
    fun setTextColor(color: Int) : FormInputButton {
        btnNoImage.setTextColor(ContextCompat.getColor(context,color))
        return this
    }

    private fun setProgressColor(color: Int) : FormInputButton {
        mProgressColor=color
        return this
    }

    fun setCornerRadius(radius:Int): FormInputButton {
        mCornerRadius=radius
        btnNoImage.cornerRadius=mCornerRadius
        return this
    }


    fun getButton(): Button {
        return btnNoImage
    }

   fun showLoading(visibility: Boolean) {
        if (visibility) {
            btnNoImage.text = spannableString
            if(isShowProgress){
                progressDrawable.callback = callback
                progressDrawable.start()
            }
            btnNoImage.isEnabled = false
        } else {
            btnNoImage.text = mValue
            btnNoImage.isEnabled = true
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