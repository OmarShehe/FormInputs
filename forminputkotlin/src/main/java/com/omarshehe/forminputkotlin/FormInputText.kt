package com.omarshehe.forminputkotlin

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.TextWatcher
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import com.omarshehe.forminputkotlin.interfaces.ViewOnClickListener
import com.omarshehe.forminputkotlin.utils.*
import com.omarshehe.forminputkotlin.utils.Utils.hideKeyboard
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
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs,defStyleAttr) {
        this.attrs = attrs
        styleAttr=defStyleAttr
        initView()
    }

    private fun initView(){
        LayoutInflater.from(context).inflate(R.layout.form_input_text, this, true)
        mPresenter = FormInputPresenterImpl()
        /**
         * Get Attributes
         */
       context?.let{
            val a = it.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout,styleAttr,0)
            setTextColor( a.getResourceId(R.styleable.FormInputLayout_form_textColor,R.color.black))
            setMandatory( a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, true))
            setLabel(a.getString(R.styleable.FormInputLayout_form_label).orEmpty())
            setHint(a.getString(R.styleable.FormInputLayout_form_hint).orEmpty())
            setValue(a.getString(R.styleable.FormInputLayout_form_value).orEmpty())
            setHeight(a.getDimension(R.styleable.FormInputLayout_form_height,resources.getDimension( R.dimen.formInputInput_box_height)).toInt())
            setBackground(a.getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square))

            showValidIcon(a.getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true))
            setInputType( a.getInt(R.styleable.FormInputLayout_form_inputType, 1))
            setLabelVisibility(a.getBoolean(R.styleable.FormInputLayout_form_showLabel, true))

            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)
            txtInputBox.addTextChangedListener(this)
            iconCancel.setOnClickListener { txtInputBox.setText("") }
            a.recycle()
        }
    }


    private fun initClickListener(){
            txtInputBox.movementMethod=object :MovementMethod{
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

            txtInputBox.setOnClickListener{
                    mListener?.onClick()
            }
    }
    /**
     * Set components
     */

    fun setLabel(text:String): FormInputText{
        mLabel=tvLabel.setLabel(text,isMandatory)
        return this
    }

    /**
     * set red star in the label for mandatory view.
     * if view not mandatory set [inputError] false
     */
    fun setMandatory(mandatory: Boolean) : FormInputText {
        isMandatory =mandatory
        if(!mandatory){ inputError=false }
        mLabel=tvLabel.setLabel(mLabel,isMandatory)
        return this
    }

    fun setLabelVisibility(show:Boolean): FormInputText {
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

    fun setHeight(height: Int) : FormInputText {
        txtInputBox.height=height
        return this
    }

    fun setBackground(background: Int) : FormInputText  {
        layInputBox.setBackgroundResource(background)
        return this
    }


    fun setViewToConfirm(view:FormInputText):FormInputText{
        viewToConfirm=view
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String){
        txtInputBox.textColor(R.color.colorRed)
        verifyInputError(errorMessage, VISIBLE)
    }

    fun showValidIcon(showIcon: Boolean) : FormInputText {
        showValidIcon=showIcon
        return this
    }

    fun setInputType(inputType: Int) : FormInputText  {
        mInputType = inputType

        when (mInputType) {
            INPUT_TYPE_TEXT -> {
                txtInputBox.inputType = InputType.TYPE_CLASS_TEXT
                txtInputBox.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            }
            INPUT_TYPE_PHONE -> txtInputBox.inputType = InputType.TYPE_CLASS_PHONE
            INPUT_TYPE_NUMBER ->  txtInputBox.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            INPUT_TYPE_EMAIL -> txtInputBox.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            INPUT_TYPE_URL -> txtInputBox.inputType=InputType.TYPE_TEXT_VARIATION_URI
        }

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

    fun setTextColor(color:Int):FormInputText{
        mTextColor=color
        txtInputBox.textColor(mTextColor)
        return this
    }

    /**
     * For save Instance State of the view in programmatically access
     */
    fun setID(id:Int):FormInputText{
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
        inputError=tvError.showInputError(validIcon,checkIfShouldShowValidIcon(), stringError, visible)
    }

    private fun checkIfShouldShowValidIcon():Boolean{
        return if(getValue().isBlank()){
             false
        }else{
            showValidIcon
        }
    }


    fun isError(parentView: View?=null): Boolean {
        return if (inputError) {
            verifyInputError(mErrorMessage, VISIBLE)
            hideKeyboard(context)
            parentView?.scrollTo(0, tvError.top)
            txtInputBox.requestFocus()
            true
        } else {
            verifyInputError("", GONE)
            false
        }
    }



    /**
     * Listener on text change
     * */
    override fun afterTextChanged(s: Editable?) {
    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        inputBoxOnTextChange(s.toString())
    }
    private fun inputBoxOnTextChange(mValue: String) {
        mTextChangeListener?.onTextChange(mValue)
        iconCancel.visibleIf(mValue.isNotEmpty())

        /**
         *  Compare this view textValue to [viewToConfirm] textValue if [viewToConfirm] is not equal to null
         */
        if(viewToConfirm.isNotNull()){
            if(mValue.isNotEmpty() && viewToConfirm?.getValue() == mValue){
                setTextColor(mTextColor)
                verifyInputError("", GONE)
            }else{
                txtInputBox.textColor(R.color.colorRed)
                verifyInputError(String.format(resources.getString(R.string.doNotMatch),mLabel), VISIBLE)
            }

            /**
             *  If the [mValue] is empty, show input error only if [isMandatory] is true
             */
        }else if (mValue.isEmpty()) {
            if (isMandatory) {
                verifyInputError(String.format(resources.getString(R.string.cantBeEmpty), mLabel), VISIBLE)
            } else {
                verifyInputError("", GONE)
            }

            /**
             * The [mValue] is not empty, remove error.
             * Validate view based on [mInputType]
             */
        }else {
            verifyInputError("", GONE)

            when(mInputType){
                INPUT_TYPE_NUMBER->{
                    if(mPresenter.isValidNumber(mValue) ){
                        setTextColor(mTextColor)
                        verifyInputError("", GONE)
                    }else{
                        txtInputBox.textColor(R.color.colorRed)
                        verifyInputError(String.format(resources.getString(R.string.isInvalid), mLabel), VISIBLE)
                    }
                }

                INPUT_TYPE_EMAIL->{
                    if (mPresenter.isValidEmail(mValue)) {
                        setTextColor(mTextColor)
                        verifyInputError("", View.GONE)
                    } else {
                        txtInputBox.textColor(R.color.colorRed)
                        verifyInputError(resources.getString(R.string.inValidEmail), View.VISIBLE)
                    }
                }

                INPUT_TYPE_PHONE-> {
                    if (mPresenter.isValidPhoneNumber(mValue)) {
                        setTextColor(mTextColor)
                        verifyInputError("", View.GONE)
                    } else {
                        txtInputBox.textColor(R.color.colorRed)
                        verifyInputError(resources.getString(R.string.inValidPhoneNumber), View.VISIBLE)
                    }
                }

                INPUT_TYPE_URL->{
                    if (mPresenter.isValidUrl(mValue)) {
                        setTextColor(mTextColor)
                        verifyInputError("", View.GONE)
                    } else {
                        txtInputBox.textColor(R.color.colorRed)
                        verifyInputError(resources.getString(R.string.invalidUrl), View.VISIBLE)
                    }
                }
            }
        }
    }
}