package com.omarshehe.forminputkotlin

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.TextWatcher
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.omarshehe.forminputkotlin.utils.FormInputContract
import com.omarshehe.forminputkotlin.utils.FormInputPresenterImpl
import com.omarshehe.forminputkotlin.utils.Utils
import com.omarshehe.forminputkotlin.utils.Utils.hideKeyboard
import kotlinx.android.synthetic.main.form_input_text.view.*


class FormInputText : BaseFormInput, TextWatcher  {
    private lateinit var mPresenter: FormInputContract.Presenter

    val INPUTTYPE_TEXT = 1
    val INPUTTYPE_PHONE = 2
    val INPUTTYPE_NUMBER = 3
    val INPUTTYPE_EMAIL = 4

    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mHint: String = ""
    private var mValue : String = ""
    private var mHeight : Int = 100
    private var mErrorMessage :String = ""
    private var inputError:Int = 1
    private var isMandatory: Boolean = true
    private var mInputType:Int = 1
    private var isShowValidIcon= true
    private var viewToConfirm :FormInputText? = null
    private var isShowLabel:Boolean =true

    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0

    private var mListener : OnClickListener? =null

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
        if(context!=null){
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout,styleAttr,0)
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

            setIcons()

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

    private fun setIcons(){
        iconCancel.setImageResource(R.drawable.ic_close_grey)
        imgNoError.setImageResource(R.drawable.check_green)
    }

    fun setLabel(text:String): FormInputText{
        mLabel=Utils.setLabel(tvLabel,text,isMandatory)
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputText {
        isMandatory =mandatory
        if(!mandatory){ inputError=0 }
        mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
        return this
    }

    fun setLabelVisibility(show:Boolean): FormInputText {
        isShowLabel=Utils.setViewVisibility(tvLabel,show)
        return this
    }

    fun setHint(hint: String) :FormInputText {
        mHint=hint
        txtInputBox.hint = mHint
        return this
    }

    fun setValue(value: String) :FormInputText {
        mValue = value
        txtInputBox.setText(value)
        return this
    }

    fun setHeight(height: Int) : FormInputText {
        mHeight=height
        val lp = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        txtInputBox.layoutParams=lp
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


    fun showValidIcon(showIcon: Boolean) : FormInputText {
        isShowValidIcon=showIcon
        return this
    }

    fun setInputType(inputType: Int) : FormInputText  {
        mInputType = inputType

        when (mInputType) {
            INPUTTYPE_TEXT -> {
                txtInputBox.inputType = InputType.TYPE_CLASS_TEXT
                txtInputBox.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            }
            INPUTTYPE_PHONE -> txtInputBox.inputType = InputType.TYPE_CLASS_PHONE
            INPUTTYPE_NUMBER ->  txtInputBox.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            INPUTTYPE_EMAIL -> txtInputBox.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        return this
    }

    fun setOnViewClickListener(listener: OnClickListener):FormInputText{
        mListener=listener
        initClickListener()
        return this
    }

    fun setTextColor(color:Int):FormInputText{
        mTextColor=color
        txtInputBox.setTextColor(ContextCompat.getColor(context,mTextColor))
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
                hideKeyboard(context)
                parentView.scrollTo(0, tvError.top)
            }
            txtInputBox.requestFocus()
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
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        inputBoxOnTextChange(s.toString())
    }
    private fun inputBoxOnTextChange(value: String) {
        mValue=value
        iconCancel.visibility = if (mValue.isNotEmpty()) VISIBLE else GONE

        if(viewToConfirm!=null){
            if(mValue.isNotEmpty() && viewToConfirm?.getValue()==mValue){
                setTextColor(mTextColor)
                verifyInputError("", View.GONE)
            }else{
                txtInputBox.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                verifyInputError(String.format(resources.getString(R.string.doNotMatch),mLabel), View.VISIBLE)
            }
        }else if (mValue.isEmpty()) {
            if (isMandatory) {
                verifyInputError(String.format(resources.getString(R.string.cantBeEmpty), mLabel), View.VISIBLE)
            } else {
                verifyInputError("", View.GONE)
            }
        }else {
            verifyInputError("", View.GONE)

            if (mInputType == INPUTTYPE_EMAIL) {
                if (mPresenter.isValidEmail(mValue)) {
                    setTextColor(mTextColor)
                    verifyInputError("", View.GONE)
                } else {
                    txtInputBox.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    verifyInputError(resources.getString(R.string.inValidEmail), View.VISIBLE)
                }
            }

            if (mInputType == INPUTTYPE_PHONE) {
                if (mPresenter.isValidPhoneNumber(mValue)) {
                    setTextColor(mTextColor)
                    verifyInputError("", View.GONE)
                } else {
                    txtInputBox.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    verifyInputError(resources.getString(R.string.inValidPhoneNumber), View.VISIBLE)
                }
            }
        }
    }


    interface OnClickListener{
        fun onClick()
    }
}