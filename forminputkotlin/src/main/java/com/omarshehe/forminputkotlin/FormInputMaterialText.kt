package com.omarshehe.forminputkotlin


import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.*
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


/**
 * Created by omars on 10/16/2019.
 * Author omars
 */
class FormInputMaterialText : TextInputEditText, TextWatcher {

    private var bottomTextSize: Int = 0
    private var inputError:Int = 1
    private var mErrorMessage :String = ""
    private lateinit var mClearIcon: Drawable
    private var mTextInputLayout: TextInputLayout? =null
    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0
    private var tempTextHelper:String =""
    private var mListener : OnClickListener? =null
    private var isMandatory: Boolean = false
    private var isShowValidIcon= true

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
    var erroView:Int=0

    private fun initView(){

        mClearIcon= ContextCompat.getDrawable(context,R.drawable.ic_close)!!
        mClearIcon.callback=this

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout,styleAttr,0)
        minHeight=resources.getDimension(R.dimen.input_box_height).toInt()
        bottomTextSize = a.getDimensionPixelSize(R.styleable.FormInputLayout_form_bottomTextSize, resources.getDimensionPixelSize(R.dimen.bottom_text_size))
        isMandatory = a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, false)
        isShowValidIcon  = a.getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true)
        a.recycle()

        if(mTextInputLayout!=null){
            tempTextHelper=mTextInputLayout?.helperText.toString()
        }

        setMandatory(true)
    }

    private fun initClickListener(){
        this.movementMethod=object : MovementMethod {
            override fun onTouchEvent(widget: TextView?, text: Spannable?, event: MotionEvent?): Boolean {
                return false
            }

            override fun canSelectArbitrarily(): Boolean {
                return false
            }

            override fun onKeyDown(widget: TextView?, text: Spannable?, keyCode: Int, event: KeyEvent?): Boolean {
                return false
            }

            override fun onKeyUp(widget: TextView?, text: Spannable?, keyCode: Int, event: KeyEvent?): Boolean {
                return false
            }

            override fun onGenericMotionEvent(widget: TextView?, text: Spannable?, event: MotionEvent?): Boolean {
                return false
            }

            override fun onTakeFocus(widget: TextView?, text: Spannable?, direction: Int) {
                mListener?.onClick()
            }

            override fun initialize(widget: TextView?, text: Spannable?) {
            }

            override fun onKeyOther(view: TextView?, text: Spannable?, event: KeyEvent?): Boolean {
                return false
            }

            override fun onTrackballEvent(widget: TextView?, text: Spannable?, event: MotionEvent?): Boolean {
                return false
            }

        }

        this.setOnClickListener{
            mListener?.onClick()
        }
    }

    fun setTextInputLayout(textInputLayout: TextInputLayout){
        mTextInputLayout=textInputLayout
        tempTextHelper=mTextInputLayout?.helperText.toString()
        setMandatory(true)
    }



    fun setOnViewClickListener(listener: OnClickListener):FormInputMaterialText{
        mListener=listener
        initClickListener()
        return this
    }

    fun setMandatory(isMandatory: Boolean) :FormInputMaterialText{
        if (mTextInputLayout != null) {
            val mandatory= if(isMandatory) "*" else ""
            mTextInputLayout?.helperText = HtmlCompat.fromHtml(String.format(context.getString(R.string.label),tempTextHelper,mandatory), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        return this
    }

    fun showValidIcon(showIcon: Boolean) : FormInputMaterialText {
        isShowValidIcon=showIcon
        return this
    }



    /**
     * Listener on text change
     * */
    override fun afterTextChanged(s: Editable?) {
    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (hasFocus()) {
            showClearIcon(!TextUtils.isEmpty(s))
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        showClearIcon(focused && !TextUtils.isEmpty(text.toString()))
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isClearIconClicked(event)) {
            text = null
            event.action = MotionEvent.ACTION_CANCEL
            showClearIcon(false)
            return false
        }
        return super.onTouchEvent(event)
    }

    private fun showClearIcon(show: Boolean) {
        if (show) {
            setCompoundDrawablesWithIntrinsicBounds (null, null, mClearIcon, null)
        } else {
            setCompoundDrawables(null, null, null, null)
        }


    }

    private fun isClearIconClicked(event: MotionEvent): Boolean {
        val touchPointX = event.x.toInt()
        val widthOfView = width
        return touchPointX >= widthOfView - compoundPaddingRight
    }






    interface OnClickListener{
        fun onClick()
    }
}