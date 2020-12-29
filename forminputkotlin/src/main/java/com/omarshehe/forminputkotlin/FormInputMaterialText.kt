package com.omarshehe.forminputkotlin


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.*
import android.text.method.MovementMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import com.omarshehe.forminputkotlin.interfaces.ViewOnClickListener
import com.omarshehe.forminputkotlin.utils.*
import kotlinx.android.synthetic.main.form_input_text.view.*


/**
 * Created by omars on 10/16/2019.
 * Author omars
 */
class FormInputMaterialText : TextInputEditText, TextWatcher {
    private lateinit var mPresenter: FormInputContract.Presenter

    private var inputError:Boolean = true
    private var mErrorMessage :String = ""
    private var isMandatory: Boolean = true
    private var mInputType:Int = 1
    private var viewToConfirm : FormInputMaterialText? = null
    private var showClearButton:Boolean=true
    private var showLabel:Boolean =true

    private var mTextInputLayout: TextInputLayout? =null
    private var tempTextHelper:String =""
    private var defaultTextHelperColor:Int =R.color.colorGrey
    private var mClearIcon: Drawable?=getDrawable(R.drawable.ic_close)

    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0

    private var mListener : ViewOnClickListener? =null
    private var mTextChangeListener : OnTextChangeListener? =null

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


    private fun initView(){
        mPresenter = FormInputPresenterImpl()
        mClearIcon?.callback=this

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout,styleAttr,0)
        setViewInputType(a.getInt(R.styleable.FormInputLayout_form_inputType, 1))
        setShowClearButton(a.getBoolean(R.styleable.FormInputLayout_form_showClearButton, true))
        setShowLabel(a.getBoolean(R.styleable.FormInputLayout_form_showLabel, true))
        setMandatory(a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, true))
        a.recycle()

        initTextInputLayout()
        mErrorMessage=formatString()
    }

    /**
     * Initialize mTextInputLayout, get default label and text color
     */
    private fun initTextInputLayout(){
        mTextInputLayout?.let{
            tempTextHelper=it.helperText.toString()
            defaultTextHelperColor=it.helperTextCurrentTextColor
        }
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
        initTextInputLayout()
        mTextInputLayout=textInputLayout
        tempTextHelper=mTextInputLayout?.helperText.toString()
        mErrorMessage=formatString()
        setMandatory(isMandatory)
    }



    fun setOnViewClickListener(listener: ViewOnClickListener): FormInputMaterialText {
        mListener=listener
        initClickListener()
        return this
    }

    fun setOnTextChangeListener(listener: OnTextChangeListener):FormInputMaterialText{
        mTextChangeListener=listener
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputMaterialText {
        isMandatory=mandatory
        if(!mandatory){ inputError=false }
        val stringMandatory= if(isMandatory) "*" else ""
        mTextInputLayout?.helperText = context.getString(R.string.label,tempTextHelper,stringMandatory).toHtml()
        mTextInputLayout?.setHelperTextColor(ContextCompat.getColorStateList(context,defaultTextHelperColor))
        mTextInputLayout?.isHelperTextEnabled=showLabel
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String){
        textColor(R.color.colorRed)
        verifyInputError(errorMessage)
    }

    fun setViewToConfirm(view: FormInputMaterialText): FormInputMaterialText {
        viewToConfirm=view
        return this
    }

    fun setViewInputType(type: Int) : FormInputMaterialText {
        mInputType = type
        when (mInputType) {
            INPUT_TYPE_TEXT -> inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            INPUT_TYPE_PHONE -> inputType = InputType.TYPE_CLASS_PHONE
            INPUT_TYPE_NUMBER ->  inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            INPUT_TYPE_EMAIL -> inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            INPUT_TYPE_PASSWORD -> {
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                transformationMethod = PasswordTransformationMethod()
            }
            INPUT_TYPE_MULTILINE ->{
                setScroll()
                inputType=InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }
        }

        return this
    }


    fun setValue(value: String) : FormInputMaterialText {
        setText(value)
        return this
    }

    fun setShowLabel(show:Boolean): FormInputMaterialText {
        showLabel=show
        return this
    }

    fun setShowClearButton(show:Boolean): FormInputMaterialText {
        showClearButton=show
        return this
    }


    @SuppressLint("ClickableViewAccessibility", "RtlHardcoded")
    private fun setScroll() {
        isSingleLine = false
        gravity = Gravity.LEFT or Gravity.TOP
        scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        isVerticalScrollBarEnabled = true
        overScrollMode = 0
        setOnTouchListener { v, event ->
            if(isFocused){
                v.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
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
    override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
        val text=char.toString()
        if (hasFocus()) {
            showIcon(text.isNotEmpty())
        }
        mTextChangeListener?.onTextChange(text)
        inputBoxOnTextChange(text)
    }



    private fun inputBoxOnTextChange(value: String) {
        if(viewToConfirm!=null){
            if(value.isNotEmpty() && viewToConfirm?.text.toString()==value){
                verifyInputError("")
            }else{
                verifyInputError(formatString(resources.getString(R.string.doNotMatch)))
            }
        }else if (value.isEmpty()) {
            if (isMandatory) {
                verifyInputError(formatString())
            } else {
                verifyInputError("")
            }
        }else {
            verifyInputError("")
            if (mInputType == INPUT_TYPE_EMAIL) {
                if (mPresenter.isValidEmail(value)) {
                    setTextColor(ContextCompat.getColor(context,R.color.black))
                    verifyInputError("")
                } else {
                    setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    verifyInputError(resources.getString(R.string.inValidEmail))
                }
            }

            if (mInputType == INPUT_TYPE_PHONE) {
                if (mPresenter.isValidPhoneNumber(value)) {
                    setTextColor(ContextCompat.getColor(context,R.color.black))
                    verifyInputError("")
                } else {
                    setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    verifyInputError(resources.getString(R.string.inValidPhoneNumber))
                }
            }
        }
    }

    private fun verifyInputError(error: String) {
        if(error.isNotEmpty()){
            mTextInputLayout?.isHelperTextEnabled=showLabel
            mTextInputLayout?.helperText = error
            mTextInputLayout?.setHelperTextColor(ContextCompat.getColorStateList(context,R.color.colorRed))
            mTextInputLayout?.error=error
            mTextInputLayout?.errorIconDrawable=null
            inputError=true
            mErrorMessage=error
        }else{
            inputError=false
            mErrorMessage=""
            setMandatory(isMandatory)
        }

    }

    /**
     * Check if there is an error.
     * if there any
     * * * return true,
     * * * hide softKeyboard
     * * * scroll top to the view
     * * * put view on focus
     * * * show error message
     * else return false
     */
    fun noError(parentView: View?=null):Boolean{
        inputError.isTrue {
            verifyInputError(mErrorMessage)
            parentView.hideKeyboard()
            parentView?.scrollTo(0, this.top)
            requestFocus()
        }.isNotTrue {
            verifyInputError("")
        }
        return !inputError
    }


    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        showIcon(focused && !TextUtils.isEmpty(text.toString()))
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isClearIconClicked(event) && getValue().isNotBlank()) {
            text = null
            event.action = MotionEvent.ACTION_CANCEL
            showIcon(false)
            return false
        }
        return super.onTouchEvent(event)
    }

    private fun showIcon(show: Boolean,icon:Drawable?=mClearIcon) {
        if ( showClearButton && show) {
            setCompoundDrawablesWithIntrinsicBounds (null, null, icon, null)
        } else {
            setCompoundDrawables(null, null, null, null)
        }
    }

    /**
     * Determine if the clearIcon been clicked.
     * measure the clicked space by [width] of the view minus by the [compoundPaddingRight].
     * Return true if clicked space is greater than measured space. Else return  false
     */
    private fun isClearIconClicked(event: MotionEvent): Boolean {
        val touchPointX = event.x.toInt()
        val widthOfView = width
        return if(compoundDrawables[2].isNotNull()){
            touchPointX >= widthOfView - compoundPaddingRight
        }else {
            false
        }
    }

    private fun formatString(string:String=resources.getString(R.string.cantBeEmpty),label:String=tempTextHelper):String{
        return String.format(string ,label)
    }
}