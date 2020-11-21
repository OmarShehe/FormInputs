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
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import com.omarshehe.forminputkotlin.utils.*
import kotlinx.android.synthetic.main.form_input_pin.view.*

class FormInputPin:  BaseFormInput,TextWatcher  {
    private lateinit var mPresenter: FormInputContract.Presenter

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
        LayoutInflater.from(context).inflate(R.layout.form_input_pin, this, true)
        mPresenter = FormInputPresenterImpl()

        pinViewList= listOf(txtPinOne,txtPinTwo,txtPinThree,txtPinFour)
        /**
         * Get Attributes
         */
        if(context!=null){
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout,styleAttr,0)
            setTextColor( a.getResourceId(R.styleable.FormInputLayout_form_textColor,R.color.black))
            setHintTextColor(a.getResourceId(R.styleable.FormInputLayout_form_textColorHint,R.color.hint_text_color))
            setLabelTextColor(a.getResourceId(R.styleable.FormInputLayout_form_textColorLabel,R.color.black))
            setMandatory( a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, false))
            setLabel( a.getString(R.styleable.FormInputLayout_form_label).orEmpty())
            setHint( a.getString(R.styleable.FormInputLayout_form_hint).orEmpty())
            height = a.getDimension(R.styleable.FormInputLayout_form_height,resources.getDimension( R.dimen.formInputInput_box_height)).toInt()
            setBackground(a.getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square))

            showValidIcon( a.getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true))
            setInputType( a.getInt(R.styleable.FormInputLayout_form_inputType, 3))
            setLabelVisibility(a.getBoolean(R.styleable.FormInputLayout_form_showLabel, true))

            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)
            initEvents()
            a.recycle()
        }



    }
    
    private fun initEvents(){
        txtPinOne.addTextChangedListener(this)
        txtPinTwo.addTextChangedListener(this)
        txtPinThree.addTextChangedListener(this)
        txtPinFour.addTextChangedListener(this)
    }


    /**
     * Set components
     */

    fun setLabel(text:String): FormInputPin{
        mLabel=tvLabel.setLabel(text,isMandatory)
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputPin {
        isMandatory =mandatory
        mandatory.isNotTrue{ inputError=false }
        mLabel=tvLabel.setLabel(mLabel,isMandatory)
        return this
    }
    fun setLabelVisibility(show:Boolean): FormInputPin {
        tvLabel.visibleIf(show)
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

    fun setHeight(height: Int) : FormInputPin {
        viewPins.layoutParams= LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
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
            it.textColor(R.color.colorRed)
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
        tvLabel.textColor(color)
        return this
    }

    private fun setRedTextColor(){
        pinViewList.forEach{
            it.textColor(R.color.colorRed)
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
        return mPresenter.appendPin(txtPinOne.text.toString(), txtPinTwo.text.toString(), txtPinThree.text.toString(), txtPinFour.text.toString())
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

    fun noError(parentView: View?=null):Boolean{
        inputError.isTrue {
            verifyInputError(mErrorMessage, VISIBLE)
            parentView.hideKeyboard()
            parentView?.scrollTo(0, tvError.top)
            txtPinOne.requestFocus()
        }.isNotTrue {
            verifyInputError("", View.GONE)
        }
        return !inputError
    }


    /**
     * Listener on text change
     * */
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
    override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
        inputBoxOnTextChange(s.toString())
    }

    private fun inputBoxOnTextChange(value:String) {
        if(value.isNotEmpty()){
            val editText = (context as Activity).currentFocus as EditText?
            editText?.focusSearch(View.FOCUS_RIGHT)?.requestFocus()
        }
        mPinValue=mPresenter.appendPin(txtPinOne.text.toString(), txtPinTwo.text.toString(), txtPinThree.text.toString(), txtPinFour.text.toString())
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