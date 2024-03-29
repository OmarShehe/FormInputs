package com.omarshehe.forminputkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.withStyledAttributes
import com.omarshehe.forminputkotlin.databinding.FormInputMultilineBinding
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import com.omarshehe.forminputkotlin.utils.*

class FormInputMultiline  :BaseFormInput, TextWatcher {
    private lateinit var binding:FormInputMultilineBinding
    private var inputError:Boolean = true
    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mErrorMessage :String = ""
    private var isMandatory: Boolean = true
    private var mMaxLength:Int = 0
    private var showValidIcon= true

    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0

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
        binding= FormInputMultilineBinding.inflate(LayoutInflater.from(context),this)
        orientation= VERTICAL
        context.withStyledAttributes(attrs, R.styleable.FormInputLayout, styleAttr, 0) {
            setTextColor(getResourceId(R.styleable.FormInputLayout_form_textColor,R.color.black))
            setHintTextColor(getResourceId(R.styleable.FormInputLayout_form_textColorHint,R.color.hint_text_color))
            setLabelTextColor(getResourceId(R.styleable.FormInputLayout_form_textColorLabel,R.color.black))
            setMandatory( getBoolean(R.styleable.FormInputLayout_form_isMandatory, true))
            setLabel(getString(R.styleable.FormInputLayout_form_label).orEmpty())
            setHint(getString(R.styleable.FormInputLayout_form_hint).orEmpty())
            setValue(getString(R.styleable.FormInputLayout_form_value).orEmpty())
            setInputViewHeight(getDimension(R.styleable.FormInputLayout_form_height,resources.getDimension( R.dimen.formInputInput_box_height)).toInt())
            setBackground(getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square))

            showValidIcon(getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true))
            setLabelVisibility(getBoolean(R.styleable.FormInputLayout_form_showLabel, true))

            setMaxLines(getInt(R.styleable.FormInputLayout_form_maxLines, 5))
            setMaxLength( getInt(R.styleable.FormInputLayout_form_maxLength, 300))

            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)
        }
        setScroll()
        binding.txtMultiline.addTextChangedListener(this)
    }

    /**
     * Set components
     */
    fun setLabel(text:String): FormInputMultiline{
        mLabel=binding.tvLabel.setLabel(text,isMandatory)
        return this
    }

    /**
     * set red star in the label for mandatory view.
     * if view not mandatory set [inputError] false
     */
    fun setMandatory(mandatory: Boolean) : FormInputMultiline {
        isMandatory =mandatory
        mandatory.isNotTrue{ inputError=false }
        mLabel=binding.tvLabel.setLabel(mLabel,isMandatory)
        return this
    }

    fun setLabelVisibility(show:Boolean): FormInputMultiline {
        binding.tvLabel.visibleIf(show)
        return this
    }


    fun setHint(hint: String) : FormInputMultiline {
        binding.txtMultiline.hint = hint
        return this
    }

    fun setValue(value: String) : FormInputMultiline{
        binding.txtMultiline.setText(value)
        return this
    }

    fun setInputViewHeight(height: Int) : FormInputMultiline {
        binding.txtMultiline.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, height)
        return this
    }

    fun setMaxLength(maxLength: Int) : FormInputMultiline{
        mMaxLength = maxLength
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(mMaxLength)
        binding.txtMultiline.filters = filterArray
        countRemainInput(getValue())
        return this
    }



    @SuppressLint("ClickableViewAccessibility", "RtlHardcoded")
    private fun setScroll() {
        binding.txtMultiline.apply {
            isSingleLine = false
            gravity = Gravity.LEFT or Gravity.TOP
            setPadding(getDimension(R.dimen.space_normal), getDimension(R.dimen.space_normal), getDimension(R.dimen.space_normal), getDimension(R.dimen.space_tiny))
            scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
            isVerticalScrollBarEnabled = true
            overScrollMode = 0
        }
        binding.txtMultiline.setOnTouchListener { view, event ->
            if(binding.txtMultiline.isFocused){
                view.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
    }

    fun setMaxLines(maxLines: Int) : FormInputMultiline{
        binding.txtMultiline.maxLines = maxLines
        return this
    }

    fun setBackground(@DrawableRes background: Int) : FormInputMultiline{
        binding.layInputBox.setBackgroundResource(background)
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String){
        binding.txtMultiline.textColor(R.color.colorOnError)
        verifyInputError(errorMessage, VISIBLE)
    }

    fun showValidIcon(showIcon: Boolean) : FormInputMultiline {
        showValidIcon=showIcon
        return this
    }

    fun setOnTextChangeListener(listener: OnTextChangeListener):FormInputMultiline{
        mTextChangeListener=listener
        return this
    }

    fun setTextColor(color:Int):FormInputMultiline{
        mTextColor=color
        binding.txtMultiline.textColor(mTextColor)
        return this
    }

    fun setHintTextColor(@ColorRes color: Int):FormInputMultiline{
        binding.txtMultiline.hintTextColor(color)
        return this
    }

    fun setLabelTextColor(@ColorRes color: Int):FormInputMultiline{
        binding.tvLabel.textColor(color)
        binding.txtLengthDesc.textColor(color)
        return this
    }

    /**
     * For save Instance State of the view in programmatically access
     */
    fun setID(id:Int):FormInputMultiline{
        this.id=id
        return this
    }


    /**
     * Get components
     */
    fun getValue(): String {
        return binding.txtMultiline.text.toString()
    }

    fun getInputBox() : EditText{
        return binding.txtMultiline
    }

    /**
     * Errors
     */
    private fun verifyInputError(stringError: String, visible: Int){
        mErrorMessage=stringError
        inputError=binding.tvError.showInputError(binding.validIcon,checkIfShouldShowValidIcon(), stringError, visible)
    }

    private fun checkIfShouldShowValidIcon():Boolean{
        return if(getValue().isBlank()){
            false
        }else{
            showValidIcon
        }
    }

    /**
     * Check if there is an error.
     * if there any
     * * return true,
     * * hide softKeyboard
     * * scroll top to the view
     * * put view on focus
     * * show error message
     * else return false
     * set [showError] to false if you want to get only the return value
     */
    fun noError(parentView: View? = null, showError:Boolean=true):Boolean{
        inputError.isTrue {
            showError.isTrue {
                verifyInputError(mErrorMessage, VISIBLE)
                parentView.hideKeyboard()
                parentView?.scrollTo(0, binding.txtMultiline.top)
                binding.txtMultiline.requestFocus()
            }
        }.isNotTrue {
            verifyInputError("", View.GONE)
        }
        return !inputError
    }


    /**
     * Listener on text change
     * */
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(value: CharSequence?, start: Int, before: Int, count: Int) { performOnTextChange(value.toString())}

    private fun performOnTextChange(value: String) {
        if (value.isEmpty()) {
            if (isMandatory) {
                verifyInputError(resources.getString(R.string.cantBeEmpty, mLabel), View.VISIBLE)
            } else {
                verifyInputError("", View.GONE)
            }
        } else {
            verifyInputError("", View.GONE)
        }
        countRemainInput(value)
        mTextChangeListener?.onTextChange(value)
    }

    /**
     * Display remain characters to [txtLengthDesc]
     */
    private fun countRemainInput(value: String){
        val rem = mMaxLength - value.length
        binding.txtLengthDesc.text = resources.getString(R.string.remainCharacters,rem,mMaxLength)
    }
}