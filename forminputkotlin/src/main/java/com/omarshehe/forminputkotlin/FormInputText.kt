package com.omarshehe.forminputkotlin

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.withStyledAttributes
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import com.omarshehe.forminputkotlin.interfaces.ViewOnClickListener
import com.omarshehe.forminputkotlin.utils.*
import kotlinx.android.synthetic.main.form_input_text.view.*


class FormInputText : BaseFormInput, TextWatcher  {
    private lateinit var mPresenter: FormInputContract.Presenter

    private var inputError:Boolean = true
    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mErrorMessage :String = ""
    private var isMandatory: Boolean = true
    private var mInputType:Int = 1
    private var showValidIcon= true
    private var viewToConfirm :FormInputText? = null

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
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.attrs = attrs
        styleAttr = defStyleAttr
        initView()
    }

    private fun initView(){
        inflate(context, R.layout.form_input_text, this)
        mPresenter = FormInputPresenterImpl()
        orientation= VERTICAL
        context.withStyledAttributes(attrs, R.styleable.FormInputLayout, styleAttr, 0) {
            setTextColor(getResourceId(R.styleable.FormInputLayout_form_textColor, R.color.black))
            setHintTextColor(getResourceId(R.styleable.FormInputLayout_form_textColorHint, R.color.hint_text_color))
            setLabelTextColor(getResourceId(R.styleable.FormInputLayout_form_textColorLabel, R.color.black))
            setMandatory(getBoolean(R.styleable.FormInputLayout_form_isMandatory, true))
            setLabel(getString(R.styleable.FormInputLayout_form_label).orEmpty())
            setHint(getString(R.styleable.FormInputLayout_form_hint).orEmpty())
            setValue(getString(R.styleable.FormInputLayout_form_value).orEmpty())
            setInputViewHeight(getDimension(R.styleable.FormInputLayout_form_height, resources.getDimension(R.dimen.formInputInput_box_height)).toInt())
            setBackground(getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square))

            showValidIcon(getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true))
            setInputType(getInt(R.styleable.FormInputLayout_form_inputType, 1))
            setLabelVisibility(getBoolean(R.styleable.FormInputLayout_form_showLabel, true))

            mErrorMessage = String.format(resources.getString(R.string.cantBeEmpty), mLabel)
        }
        iconCancel.setOnClickListener { txtInputBox.setText("") }
        txtInputBox.addTextChangedListener(this)
    }


    private fun initClickListener(){
        object :MovementMethod{
            override fun onTouchEvent(widget: TextView?, text: Spannable?, event: MotionEvent?): Boolean { return false }

            override fun canSelectArbitrarily(): Boolean { return false }

            override fun onKeyDown(widget: TextView?, text: Spannable?, keyCode: Int, event: KeyEvent?): Boolean { return false }

            override fun onKeyUp(widget: TextView?, text: Spannable?, keyCode: Int, event: KeyEvent?): Boolean { return false }

            override fun onGenericMotionEvent(widget: TextView?, text: Spannable?, event: MotionEvent?): Boolean { return false }

            override fun onTakeFocus(widget: TextView?, text: Spannable?, direction: Int) { mListener?.onClick() }

            override fun initialize(widget: TextView?, text: Spannable?) {}

            override fun onKeyOther(view: TextView?, text: Spannable?, event: KeyEvent?): Boolean { return false }

            override fun onTrackballEvent(widget: TextView?, text: Spannable?, event: MotionEvent?): Boolean { return false }

        }.also { txtInputBox.movementMethod = it }

        txtInputBox.setOnClickListener{
            mListener?.onClick()
        }
    }
    /**
     * Set components
     */

    fun setLabel(text: String): FormInputText{
        mLabel=tvLabel.setLabel(text, isMandatory)
        return this
    }

    /**
     * set red star in the label for mandatory view.
     * if view not mandatory set [inputError] false
     */
    fun setMandatory(mandatory: Boolean) : FormInputText {
        isMandatory =mandatory
        mandatory.isNotTrue{ inputError=false }
        mLabel=tvLabel.setLabel(mLabel, isMandatory)
        return this
    }

    fun setLabelVisibility(show: Boolean): FormInputText {
        tvLabel.visibleIf(show)
        return this
    }

    fun setHint(hint: String) :FormInputText {
        txtInputBox.hint = hint
        return this
    }

    fun setValue(value: String) :FormInputText {
        txtInputBox.setText(value)
        return this
    }

    fun setInputViewHeight(height: Int) : FormInputText {
        txtInputBox.height=height
        return this
    }

    fun setBackground(background: Int) : FormInputText  {
        layInputBox.setBackgroundResource(background)
        return this
    }


    fun setViewToConfirm(view: FormInputText):FormInputText{
        viewToConfirm=view
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String){
        txtInputBox.textColor(R.color.colorOnError)
        verifyInputError(errorMessage, VISIBLE)
    }

    fun showValidIcon(showIcon: Boolean) : FormInputText {
        showValidIcon=showIcon
        return this
    }

    fun setInputType(inputType: Int) : FormInputText  {
        mInputType = inputType
        txtInputBox.setInputTypes(mInputType)
        return this
    }

    fun setOnViewClickListener(listener: ViewOnClickListener):FormInputText{
        mListener=listener
        initClickListener()
        return this
    }

    fun setOnTextChangeListener(listener: OnTextChangeListener):FormInputText{
        mTextChangeListener=listener
        return this
    }

    fun setTextColor(color: Int):FormInputText{
        mTextColor=color
        txtInputBox.textColor(mTextColor)
        return this
    }

    fun setHintTextColor(@ColorRes color: Int):FormInputText{
        txtInputBox.hintTextColor(color)
        return this
    }

    fun setLabelTextColor(@ColorRes color: Int):FormInputText{
        tvLabel.textColor(color)
        return this
    }

    /**
     * For save Instance State of the view in programmatically access
     */
    fun setID(id: Int):FormInputText{
        this.id=id
        return this
    }


    /**
     * Get components
     */
    fun getValue(): String {
        return txtInputBox.text.toString()
    }

    fun getInputBox() : EditText{
        return txtInputBox
    }


    /**
     * Errors
     */
    private fun verifyInputError(stringError: String, visible: Int){
        mErrorMessage=stringError
        inputError=tvError.showInputError(validIcon, checkIfShouldShowValidIcon(), stringError, visible)
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
                parentView?.scrollTo(0, tvError.top)
                txtInputBox.requestFocus()
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
    override fun onTextChanged(value: CharSequence?, start: Int, before: Int, count: Int) { performOnTextChange(value.toString()) }

    private fun performOnTextChange(value: String) {
        iconCancel.visibleIf(value.isNotEmpty())

        /**
         *  Compare this view textValue to [viewToConfirm] textValue if [viewToConfirm] is not equal to null
         */
        if(viewToConfirm.isNotNull()){
            if(value.isNotEmpty() && viewToConfirm?.getValue() == value){
                setTextColor(mTextColor)
                verifyInputError("", GONE)
            }else{
                txtInputBox.textColor(R.color.colorOnError)
                verifyInputError(String.format(resources.getString(R.string.doNotMatch), mLabel), VISIBLE)
            }

            /**
             *  If the [value] is empty, show input error only if [isMandatory] is true
             */
        }else if (value.isEmpty()) {
            if (isMandatory) {
                verifyInputError(String.format(resources.getString(R.string.cantBeEmpty), mLabel), VISIBLE)
            } else {
                verifyInputError("", GONE)
            }

            /**
             * The [value] is not empty, remove error.
             * Validate view based on [mInputType]
             */
        }else {
            verifyInputError("", GONE)

            when(mInputType){
                INPUT_TYPE_NUMBER -> {
                    if (mPresenter.isValidNumber(value)) {
                        setTextColor(mTextColor)
                        verifyInputError("", GONE)
                    } else {
                        txtInputBox.textColor(R.color.colorOnError)
                        verifyInputError(String.format(resources.getString(R.string.isInvalid), mLabel), VISIBLE)
                    }
                }

                INPUT_TYPE_EMAIL -> {
                    if (mPresenter.isValidEmail(value)) {
                        setTextColor(mTextColor)
                        verifyInputError("", View.GONE)
                    } else {
                        txtInputBox.textColor(R.color.colorOnError)
                        verifyInputError(resources.getString(R.string.inValidEmail), View.VISIBLE)
                    }
                }

                INPUT_TYPE_PHONE -> {
                    if (mPresenter.isValidPhoneNumber(value)) {
                        setTextColor(mTextColor)
                        verifyInputError("", View.GONE)
                    } else {
                        txtInputBox.textColor(R.color.colorOnError)
                        verifyInputError(resources.getString(R.string.inValidPhoneNumber), View.VISIBLE)
                    }
                }

                INPUT_TYPE_URL -> {
                    if (mPresenter.isValidUrl(value)) {
                        setTextColor(mTextColor)
                        verifyInputError("", View.GONE)
                    } else {
                        txtInputBox.textColor(R.color.colorOnError)
                        verifyInputError(resources.getString(R.string.invalidUrl), View.VISIBLE)
                    }
                }
            }
        }
        mTextChangeListener?.onTextChange(value)
    }
}