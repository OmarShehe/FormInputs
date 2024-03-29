package com.omarshehe.forminputkotlin

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.core.content.withStyledAttributes
import com.omarshehe.forminputkotlin.databinding.FormInputPinBinding
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import com.omarshehe.forminputkotlin.utils.*

class FormInputPin: BaseFormInput,TextWatcher  {
    private lateinit var mPresenter: FormInputContract.Presenter
    private lateinit var binding: FormInputPinBinding

    private var inputError:Boolean = true
    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mErrorMessage :String = ""
    private var isMandatory: Boolean = true
    private var mInputType:Int = 3
    private var showValidIcon= true
    private var viewToConfirm :FormInputPin? = null
    private var mPinValue : String = ""
    private var mValidPin : String = ""
    private var pinViewList= emptyList<EditText>()

    private var mTextChangeListener : OnTextChangeListener? =null

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
        binding=FormInputPinBinding.inflate(LayoutInflater.from(context),this)
        mPresenter = FormInputPresenterImpl()
        pinViewList= listOf(binding.txtPinOne,binding.txtPinTwo,binding.txtPinThree,binding.txtPinFour)
        orientation= VERTICAL
        context.withStyledAttributes(attrs, R.styleable.FormInputLayout, styleAttr, 0) {
            setTextColor(getResourceId(R.styleable.FormInputLayout_form_textColor,R.color.black))
            setHintTextColor(getResourceId(R.styleable.FormInputLayout_form_textColorHint,R.color.hint_text_color))
            setLabelTextColor(getResourceId(R.styleable.FormInputLayout_form_textColorLabel,R.color.black))
            setMandatory( getBoolean(R.styleable.FormInputLayout_form_isMandatory, false))
            setLabel( getString(R.styleable.FormInputLayout_form_label).orEmpty())
            setHint( getString(R.styleable.FormInputLayout_form_hint).orEmpty())
            setInputViewHeight(getDimension(R.styleable.FormInputLayout_form_height,resources.getDimension( R.dimen.formInputInput_box_height)).toInt())
            setBackground(getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square))

            showValidIcon( getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true))
            setInputType( getInt(R.styleable.FormInputLayout_form_inputType, 3))
            setLabelVisibility(getBoolean(R.styleable.FormInputLayout_form_showLabel, true))

            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)
        }
        initEvents()
    }

    private fun initEvents(){
        binding.txtPinOne.addTextChangedListener(this)
        binding.txtPinTwo.addTextChangedListener(this)
        binding.txtPinThree.addTextChangedListener(this)
        binding.txtPinFour.addTextChangedListener(this)
    }


    /**
     * Set components
     */

    fun setLabel(text:String): FormInputPin{
        mLabel=binding.tvLabel.setLabel(text,isMandatory)
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputPin {
        isMandatory =mandatory
        mandatory.isNotTrue{ inputError=false }
        mLabel=binding.tvLabel.setLabel(mLabel,isMandatory)
        return this
    }
    fun setLabelVisibility(show:Boolean): FormInputPin {
        binding.tvLabel.visibleIf(show)
        return this
    }

    fun setHint(hint: String) :FormInputPin {
        pinViewList.forEach{
            it.hint= hint
        }
        return this
    }

    /**
     * Set pin values
     */
    fun setValues(vararg values: String) :FormInputPin {
        pinViewList.forEachIndexed{index,view->
            view.setText(values[index])
        }
        return this
    }

    fun setInputViewHeight(mHeight: Int) : FormInputPin {
        pinViewList.forEach {
            it.height=mHeight
        }
        return this
    }

    fun setBackground(background: Int) : FormInputPin  {
        pinViewList.forEach{
            it.setBackgroundResource(background)
        }
        return this
    }


    fun setViewToConfirm(view:FormInputPin):FormInputPin{
        viewToConfirm=view
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String){
        pinViewList.forEach{
            it.textColor(R.color.colorOnError)
        }
        verifyInputError(errorMessage, VISIBLE)
    }

    fun showValidIcon(showIcon: Boolean) : FormInputPin {
        showValidIcon=showIcon
        return this
    }


    fun setInputType(inputType: Int) : FormInputPin  {
        mInputType = inputType

        when (mInputType) {
            INPUT_TYPE_NUMBER ->  {
                pinViewList.forEach{
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                }
            }
            else -> {
                pinViewList.forEach{
                    it.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                }
            }
        }

        return this
    }

    fun setOnTextChangeListener(listener: OnTextChangeListener):FormInputPin{
        mTextChangeListener=listener
        return this
    }

    fun setTextColor(color:Int):FormInputPin{
        mTextColor=color
        pinViewList.forEach{
            it.textColor(mTextColor)
        }
        return this
    }

    fun setHintTextColor(@ColorRes color: Int):FormInputPin{
        pinViewList.forEach{
            it.hintTextColor(color)
        }
        return this
    }

    fun setLabelTextColor(@ColorRes color: Int):FormInputPin{
        binding.tvLabel.textColor(color)
        return this
    }

    private fun setRedTextColor(){
        pinViewList.forEach{
            it.textColor(R.color.colorOnError)
        }
    }

    /**
     * For save Instance State of the view in programmatically access
     */
    fun setID(id:Int):FormInputPin{
        this.id=id
        return this
    }


    /**
     * Get components
     */
    fun getValue(): String {
        return mPresenter.appendPin(binding.txtPinOne.text.toString(), binding.txtPinTwo.text.toString(), binding.txtPinThree.text.toString(), binding.txtPinFour.text.toString())
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
                parentView?.scrollTo(0, binding.tvError.top)
                binding.txtPinOne.requestFocus()
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
    override fun onTextChanged(value: CharSequence, i: Int, i1: Int, i2: Int) { performOnTextChange(value.toString()) }

    private fun performOnTextChange(value: String) {
        if(value.isNotEmpty()){
            val editText = (context as Activity).currentFocus as EditText?
            editText?.focusSearch(View.FOCUS_RIGHT)?.requestFocus()
        }
        mPinValue=mPresenter.appendPin(binding.txtPinOne.text.toString(), binding.txtPinTwo.text.toString(), binding.txtPinThree.text.toString(), binding.txtPinFour.text.toString())
        if(viewToConfirm.isNotNull()){
            if(pinViewIsNotEmpty() && viewToConfirm?.getValue()==mPinValue){
                setTextColor(mTextColor)
                verifyInputError("", View.GONE)
            }else{
                setRedTextColor()
                verifyInputError(resources.getString(R.string.doNotMatch,mLabel), View.VISIBLE)
            }
        }else {
            if (pinViewIsNotEmpty()) {
                if(mValidPin.isEmpty()){
                    setTextColor(mTextColor)
                    verifyInputError("", View.GONE)
                }else{
                    if(mPinValue==mValidPin){
                        setTextColor(mTextColor)
                        verifyInputError("", View.GONE)
                    }else{
                        setRedTextColor()
                        verifyInputError(resources.getString(R.string.inValid,mLabel), View.VISIBLE)
                    }
                }
            } else {
                setRedTextColor()
                verifyInputError(resources.getString(R.string.isRequired,mLabel), View.VISIBLE)
            }
        }
        mTextChangeListener?.onTextChange(value)
    }

    private fun pinViewIsNotEmpty(): Boolean {
        pinViewList.forEach{
            if(it.text.isNullOrEmpty()){
                return false
            }
        }
        return true
    }
}