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
import androidx.core.content.ContextCompat
import com.omarshehe.forminputkotlin.utils.FormInputContract
import com.omarshehe.forminputkotlin.utils.FormInputPresenterImpl
import com.omarshehe.forminputkotlin.utils.Utils
import kotlinx.android.synthetic.main.form_input_pin.view.*

class FormInputPin:  BaseFormInput,TextWatcher  {
    private lateinit var mPresenter: FormInputContract.Presenter
    val INPUTTYPE_TEXT = 1
    val INPUTTYPE_NUMBER = 3




    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mHint: String = ""
    private var mPinValue : String = ""
    private var mValidPin : String = ""
    private var mErrorMessage :String = ""
    private var mBackground: Int =R.drawable.bg_txt_square
    private var inputError:Int = 1
    private var isMandatory: Boolean = false
    private var mInputType:Int = 3
    private var isShowValidIcon= true
    private var viewToConfirm :FormInputPin? = null
    private var isShowLabel:Boolean =true
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
            mLabel = a.getString(R.styleable.FormInputLayout_form_label).orEmpty()
            mHint = a.getString(R.styleable.FormInputLayout_form_hint).orEmpty()
            setValidPin(a.getString(R.styleable.FormInputLayout_form_value).orEmpty())
            mBackground = a.getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square)
            setMandatory( a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, false))
            isShowValidIcon  = a.getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true)
            setInputType( a.getInt(R.styleable.FormInputLayout_form_inputType, 3))
            setLabelVisibility(a.getBoolean(R.styleable.FormInputLayout_form_showLabel, true))

            setIcons()
            mLabel= Utils.setLabel(tvLabel,mLabel,isMandatory)

            setHint(mHint)

            setBackground(mBackground)
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
    private fun setIcons(){
        imgNoError.setImageResource(R.drawable.check_green)
    }

    fun setLabel(text:String): FormInputPin{
        mLabel=Utils.setLabel(tvLabel,text,isMandatory)
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputPin {
        isMandatory =mandatory
        if(!mandatory){ inputError=0 }
        mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
        return this
    }
    fun setLabelVisibility(show:Boolean): FormInputPin {
        isShowLabel=Utils.setViewVisibility(tvLabel,show)
        return this
    }

    fun setHint(hint: String) :FormInputPin {
        for(view in pinViewList){
            view.hint= hint
        }
        return this
    }

    fun setValidPin(value: String) :FormInputPin {
        mValidPin = value
        return this
    }


    fun setBackground(background: Int) : FormInputPin  {
        for(view in pinViewList){
            view.setBackgroundResource(background)
        }
        return this
    }


    fun setViewToConfirm(view:FormInputPin):FormInputPin{
        viewToConfirm=view
        return this
    }


    fun showValidIcon(showIcon: Boolean) : FormInputPin {
        isShowValidIcon=showIcon
        return this
    }


    fun setInputType(inputType: Int) : FormInputPin  {
        mInputType = inputType

        when (mInputType) {
            INPUTTYPE_TEXT -> {
                for(view in pinViewList){
                    view.inputType = InputType.TYPE_CLASS_TEXT
                    view.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                }

            }
            INPUTTYPE_NUMBER ->  {
                for(view in pinViewList){
                    view.inputType = InputType.TYPE_CLASS_NUMBER
                }
            }

        }

        return this
    }





    fun setTextColor(color:Int,updateValue:Boolean=true):FormInputPin{
        if(updateValue){
            mTextColor=color
        }
        for(view in pinViewList){
            view.setTextColor(ContextCompat.getColor(context,mTextColor))
        }
        return this
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
    private fun verifyInputError(error: String, visible: Int){
        val errorResult=Utils.showInputError(tvError,imgNoError,checkIfShouldShowValidIcon(), error, visible)
        mErrorMessage=errorResult[0].toString()
        inputError=errorResult[1].toString().toInt()
    }

    private fun checkIfShouldShowValidIcon():Boolean{
        return if(getValue().isBlank()){
            false
        }else{
            isShowValidIcon
        }
    }


    fun isError(parentView: View?): Boolean {
        return if (inputError == 1) {
            verifyInputError(mErrorMessage, VISIBLE)
            if (parentView != null) {
                Utils.hideKeyboard(context)
                parentView.scrollTo(0, tvError.top)
            }
            txtPinOne.requestFocus()
            true
        } else {
            verifyInputError("", View.GONE)
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
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        val editText = (context as Activity).currentFocus as EditText?
        if (editText != null && editText.length() > 0) {
            editText.focusSearch(View.FOCUS_RIGHT)?.requestFocus()
        }
        mPinValue=mPresenter.appendPin(txtPinOne.text.toString(), txtPinTwo.text.toString(), txtPinThree.text.toString(), txtPinFour.text.toString())
        if(viewToConfirm!=null){
            if(pinViewIsNotEmpty() && viewToConfirm?.getValue()==mPinValue){
                setTextColor(mTextColor)
                verifyInputError("", View.GONE)
            }else{
                setTextColor(ContextCompat.getColor(context,R.color.colorRed),false)
                verifyInputError(String.format(resources.getString(R.string.doNotMatch),mLabel), View.VISIBLE)
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
                        setTextColor(ContextCompat.getColor(context,R.color.colorRed),false)
                        verifyInputError(String.format(resources.getString(R.string.inValid),mLabel), View.VISIBLE)
                    }
                }
            } else {
                setTextColor(ContextCompat.getColor(context,R.color.colorRed),false)
                verifyInputError(String.format(resources.getString(R.string.isRequired),mLabel), View.VISIBLE)
            }
        }


    }

    private fun pinViewIsNotEmpty(): Boolean {
        for(view in pinViewList){
            if(view.text.isNullOrEmpty()){
                return false
            }
        }
        return true
    }
}