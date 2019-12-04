package com.omarshehe.forminputkotlin

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.button.MaterialButton
import com.omarshehe.forminputkotlin.utils.DrawableSpan
import com.omarshehe.forminputkotlin.utils.Utils


class FormInputButton : MaterialButton {
    private var mValue : String = ""
    private var mValueOnLoad : String = ""
    private var isShowProgress : Boolean = true
    private var mProgressColor: Int =R.color.white

    private lateinit var drawableSpan: DrawableSpan
    private lateinit var spannableString: SpannableString
    private lateinit var progressDrawable : CircularProgressDrawable
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
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout, 0, 0)
        mValueOnLoad = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_valueOnLoad),resources.getString(R.string.pleaseWait))
        isShowProgress = a.getBoolean(R.styleable.FormInputLayout_form_showProgress, true)
        mProgressColor = a.getResourceId(R.styleable.FormInputLayout_form_progressColor, R.color.white)

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
                invalidate()
            }
            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
            }
        }

        mValue=text.toString()
        setValueOnLoad(mValueOnLoad)
        a.recycle()

    }



    /**
     * Set components
     */
    fun setValue(value: String) :FormInputButton {
        mValue = value
        text = value
        return this
    }
    fun setValueOnLoad(value: String) :FormInputButton {
        mValueOnLoad = value
        spannableString = SpannableString(mValueOnLoad).apply {
            setSpan(drawableSpan,  length- 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return this
    }

    fun showProgressOnClick(show: Boolean) : FormInputButton {
        isShowProgress=show
        return this
    }

    private fun setProgressColor(color: Int) : FormInputButton {
        mProgressColor=color
        return this
    }


   fun showLoading(visibility: Boolean) {
        if (visibility) {
            text = spannableString
            if(isShowProgress){
                progressDrawable.callback = callback
                progressDrawable.start()
            }
            isEnabled = false
        } else {
           text = mValue
            isEnabled = true
        }
    }
}