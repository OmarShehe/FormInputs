package com.omarshehe.forminputkotlin


import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.*
import android.text.method.MovementMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.omarshehe.forminputkotlin.utils.FormInputContract
import com.omarshehe.forminputkotlin.utils.FormInputPresenterImpl
import com.omarshehe.forminputkotlin.utils.Utils.hideKeyboard


/**
 * Created by omars on 10/16/2019.
 * Author omars
 */
class FormInputMaterialText : TextInputEditText, TextWatcher {

    private lateinit var mPresenter: FormInputContract.Presenter

    val INPUTTYPE_TEXT = 1
    val INPUTTYPE_PHONE = 2
    val INPUTTYPE_NUMBER = 3
    val INPUTTYPE_EMAIL = 4
    val INPUTTYPE_PASSWORD = 5

    private var bottomTextSize: Int = 0
    private var inputError:Int = 1
    private var mErrorMessage :String = ""
    private lateinit var mClearIcon: Drawable
    private  var mNoErrorIcon:Drawable? =null
    private var mTextInputLayout: TextInputLayout? =null
    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0
    private var tempTextHelper:String =""
    private var defaultTextHelperColor:Int =R.color.colorGrey
    private var mListener : OnClickListener? =null
    private var isMandatory: Boolean = false
    private var mInputType:Int = 1
    private var isShowValidIcon= true
    private var viewToConfirm :FormInputMaterialText? = null
    private var isConfirmText:Boolean=false
    private var isShowClearButton:Boolean=true
    private var isShowLabel:Boolean =true

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
        mPresenter = FormInputPresenterImpl()
        mNoErrorIcon=ContextCompat.getDrawable(context,R.drawable.check_green)
        mClearIcon= ContextCompat.getDrawable(context,R.drawable.ic_close)!!
        mClearIcon.callback=this

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout,styleAttr,0)
        minHeight=resources.getDimension(R.dimen.input_box_height).toInt()
        bottomTextSize = a.getDimensionPixelSize(R.styleable.FormInputLayout_form_bottomTextSize, resources.getDimensionPixelSize(R.dimen.bottom_text_size))
        setMandatory(a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, false))
        isShowValidIcon  = a.getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true)
        mInputType = a.getInt(R.styleable.FormInputLayout_form_inputType, 1)
        isConfirmText= a.getBoolean(R.styleable.FormInputLayout_form_confirm, false)
        isShowClearButton=a.getBoolean(R.styleable.FormInputLayout_form_showClearButton, true)
        isShowLabel=a.getBoolean(R.styleable.FormInputLayout_form_showLabel, true)
        a.recycle()

        setViewInputType(mInputType)
        if(mTextInputLayout!=null){
            tempTextHelper=mTextInputLayout!!.helperText.toString()
            defaultTextHelperColor=mTextInputLayout!!.helperTextCurrentTextColor
        }
        mErrorMessage=formatString()
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

    /**
     * Set components
     */
    fun setTextInputLayout(textInputLayout: TextInputLayout){
        mTextInputLayout=textInputLayout
        tempTextHelper=mTextInputLayout?.helperText.toString()
        mTextInputLayout?.isHelperTextEnabled=isShowLabel
        mErrorMessage=formatString()
    }

    fun setOnViewClickListener(listener: OnClickListener):FormInputMaterialText{
        mListener=listener
        initClickListener()
        return this
    }

    fun setMandatory(mandatory: Boolean) :FormInputMaterialText{
        isMandatory=mandatory
        val stringMandatory= if(isMandatory) "*" else ""
        mTextInputLayout?.helperText = HtmlCompat.fromHtml(String.format(context.getString(R.string.label),tempTextHelper,stringMandatory), HtmlCompat.FROM_HTML_MODE_LEGACY)
        mTextInputLayout?.setHelperTextColor(ContextCompat.getColorStateList(context,defaultTextHelperColor))

        return this
    }

    fun setConfirmConfirmText(isConfirm: Boolean): FormInputMaterialText{
        isConfirmText= isConfirm
        return this
    }


    fun setViewToConfirm(view:FormInputMaterialText):FormInputMaterialText{
        viewToConfirm=view
        return this
    }

    fun setViewInputType(type: Int) : FormInputMaterialText  {
        mInputType = type
        when (mInputType) {
            INPUTTYPE_TEXT -> {
                inputType = InputType.TYPE_CLASS_TEXT
                inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            }
            INPUTTYPE_PHONE -> inputType = InputType.TYPE_CLASS_PHONE
            INPUTTYPE_NUMBER ->  inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            INPUTTYPE_EMAIL -> inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            INPUTTYPE_PASSWORD -> {
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                transformationMethod = PasswordTransformationMethod()
            }
        }

        return this
    }

    fun setValue(value: String) : FormInputMaterialText {
        setText(value)
        return this
    }

    fun setIsShowLabel(show:Boolean): FormInputMaterialText {
        isShowLabel=show
        return this
    }

    fun showValidIcon(showIcon: Boolean) : FormInputMaterialText {
        isShowValidIcon=showIcon
        return this
    }

    /**
     * Get components
     */
    fun getValue(): String {
        return text.toString()
    }


    /**
     * Listener on text change
     * */
    override fun afterTextChanged(s: Editable?) {
    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text=s.toString()
        if (hasFocus()) {
            showIcon(text.isNotEmpty())
        }
        inputBoxOnTextChange(text)
    }



    private fun inputBoxOnTextChange(value: String) {

        if(isConfirmText){
            if(value.isNotEmpty() && viewToConfirm?.text.toString()==value){
                verifyInputError("", View.GONE)
            }else{
                verifyInputError(formatString(resources.getString(R.string.doNotMatch)), View.VISIBLE)
            }
        }else if (value.isEmpty()) {
            if (isMandatory) {
                verifyInputError(formatString(), View.VISIBLE)
            } else {
                verifyInputError("", View.GONE)
            }
        }else {
            verifyInputError("", View.GONE)
            if (mInputType == INPUTTYPE_EMAIL) {
                if (mPresenter.isValidEmail(value)) {
                    setTextColor(ContextCompat.getColor(context,R.color.black))
                    verifyInputError("", View.GONE)
                } else {
                    setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    verifyInputError(resources.getString(R.string.inValidEmail), View.VISIBLE)
                }
            }

            if (mInputType == INPUTTYPE_PHONE) {
                if (mPresenter.isValidPhoneNumber(value)) {
                    setTextColor(ContextCompat.getColor(context,R.color.black))
                    verifyInputError("", View.GONE)
                } else {
                    setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    verifyInputError(resources.getString(R.string.inValidPhoneNumber), View.VISIBLE)
                }
            }
        }
    }

    private fun verifyInputError(error: String, visible: Int) {
        if(error.isNotEmpty()){
            mTextInputLayout?.isHelperTextEnabled=isShowLabel
            mTextInputLayout?.helperText = error
            mTextInputLayout?.setHelperTextColor(ContextCompat.getColorStateList(context,R.color.colorRed))
            inputError=1
            mErrorMessage=error
        }else{
            inputError=0
            mErrorMessage=""
            setMandatory(isMandatory)
            mTextInputLayout?.isHelperTextEnabled=isShowLabel
        }

    }

    fun isError(parentView: View?): Boolean {
        return if (inputError == 1) {
            verifyInputError(mErrorMessage, VISIBLE)
            if (parentView != null) {
                hideKeyboard(parentView.context as Activity)
                parentView.scrollTo(0, this.top)
                requestFocus()
            }
            true
        } else {
            verifyInputError("", View.GONE)
            false
        }
    }


    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        showIcon(focused && !TextUtils.isEmpty(text.toString()))
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isClearIconClicked(event)) {
            text = null
            event.action = MotionEvent.ACTION_CANCEL
            showIcon(false)
            return false
        }
        return super.onTouchEvent(event)
    }

    private fun showIcon(show: Boolean,icon:Drawable=mClearIcon) {
        if (isShowClearButton && show) {
            setCompoundDrawablesWithIntrinsicBounds (null, null, icon, null)
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


    private fun formatString(string:String=resources.getString(R.string.cantBeEmpty),label:String=tempTextHelper):String{
        return String.format(string ,label)
    }
}