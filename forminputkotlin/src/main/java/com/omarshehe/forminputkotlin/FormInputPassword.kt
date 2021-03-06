package com.omarshehe.forminputkotlin

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import com.omarshehe.forminputkotlin.utils.*
import kotlinx.android.synthetic.main.form_input_password.view.*
import kotlin.properties.Delegates

class FormInputPassword : BaseFormInput, TextWatcher {
    private var inputError:Boolean = true
    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mErrorMessage :String = ""
    private var isMandatory: Boolean = true
    private var isShowPassStrength: Boolean =true
    private var showValidIcon= true
    private var mPassLength=8
    private var confirmPassword :FormInputPassword? = null

    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0

    private var mTextChangeListener : OnTextChangeListener? =null

    constructor(activity: Activity) : super(activity){
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
        inflate(context, R.layout.form_input_password, this)
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
            showPassStrength(getBoolean(R.styleable.FormInputLayout_form_showPassStrength, true))

            setPassLength(getInt(R.styleable.FormInputLayout_form_passLength,mPassLength))

            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)
        }
        txtInputBox.addTextChangedListener(this)
    }

    fun setPassLength(passLength:Int) {
        mPassLength=passLength
        tvHintLong.text=resources.getString(R.string.passHintLong,mPassLength)

    }

    fun setLabel(text:String): FormInputPassword{
        mLabel=tvLabel.setLabel(text,isMandatory)
        return this
    }


    fun setMandatory(mandatory: Boolean) : FormInputPassword {
        isMandatory =mandatory
        if(!mandatory){ inputError=false }
        mLabel=tvLabel.setLabel(mLabel,isMandatory)
        return this
    }
    fun setLabelVisibility(show:Boolean): FormInputPassword {
        tvLabel.visibleIf(show)
        return this
    }


    fun setHint(hint: String) : FormInputPassword {
        txtInputBox.hint = hint
        return this
    }

    fun setValue(value: String) : FormInputPassword {
        txtInputBox.setText(value)
        return this
    }

    fun setInputViewHeight(height: Int) : FormInputPassword {
        passView.layoutParams=LayoutParams(LayoutParams.MATCH_PARENT, height)
        return this
    }

    fun showPassStrength(isShowStrength: Boolean): FormInputPassword{
        if(confirmPassword.isNotNull()) {isShowPassStrength=false}
        isShowPassStrength= isShowStrength
        layPassStrength.visibleIf(isShowStrength)
        return this
    }


    fun setBackground(background: Int) : FormInputPassword{
        passView.setBackgroundResource(background)
        return this
    }

    fun setViewToConfirm(passwordView:FormInputPassword):FormInputPassword{
        confirmPassword=passwordView
        return this
    }

    fun setOnTextChangeListener(listener: OnTextChangeListener):FormInputPassword{
        mTextChangeListener=listener
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String){
        txtInputBox.textColor(R.color.colorOnError)
        verifyInputError(errorMessage, VISIBLE)
    }

    fun showValidIcon(showIcon: Boolean) : FormInputPassword {
        showValidIcon=showIcon
        return this
    }

    fun setTextColor(color:Int):FormInputPassword{
        mTextColor=color
        txtInputBox.textColor(mTextColor)
        return this
    }

    fun setHintTextColor(@ColorRes color: Int):FormInputPassword{
        txtInputBox.hintTextColor(color)
        return this
    }

    fun setLabelTextColor(@ColorRes color: Int):FormInputPassword{
        tvLabel.textColor(color)
        tvHintTitle.textColor(color)
        tvHintUpperCase.textColor(color)
        tvHintSpecial.textColor(color)
        tvHintNumber.textColor(color)
        tvHintLong.textColor(color)
        return this
    }

    /**
     * For save Instance State of the view in programmatically access
     */
    fun setID(id:Int):FormInputPassword{
        this.id=id
        return this
    }

    /**
     * Get components
     */
    fun getValue(): String {
        return txtInputBox.text.toString()
    }

    fun getInputBox(): EditText {
        return txtInputBox
    }



    private fun updatePasswordStrengthView(password: String) {
        when {
            password.isEmpty() -> {
                verifyInputError(resources.getString(R.string.cantBeEmpty, mLabel), View.VISIBLE)
                passView.updateProgress(0)
                upperCaseIcon=false
                numberIcon=false
                specialIcon=false
                lengthIcon=false
                return
            }
            else -> verifyInputError("", View.GONE)
        }

        if(isShowPassStrength){
            val str  =PasswordStrength().calculateStrength(mPassLength,password)

            val passLevel= str[0] as PasswordStrength.PassLevel
            val result: Int=str[1] as Int
            tvPassStrength.text = passLevel.name
            tvPassStrength.setTextColor(ContextCompat.getColor(context,passLevel.getColor()))
            passView.updateProgress( result,passLevel.getColor())

            upperCaseIcon=str[2] as Boolean
            specialIcon=str[3] as Boolean
            numberIcon=str[4] as Boolean
            lengthIcon=str[5] as Boolean

            if(result==4){
                verifyInputError("", View.GONE)
            }else{
                verifyInputError("Password need to meet the requirements", View.VISIBLE)
            }
        }


    }




    private var upperCaseIcon: Boolean by Delegates.observable(false) { _, old, new ->
        if (new != old)
            changeIcon(iconHintUpperCase!!, new)

    }

    private var numberIcon: Boolean by Delegates.observable(false) { _, old, new ->
        if (new != old)
            changeIcon(iconHintNumber!!, new)

    }

    private var specialIcon: Boolean by Delegates.observable(false) { _, old, new ->
        if (new != old)
            changeIcon(iconHintSpecial!!, new)

    }
    private var lengthIcon: Boolean by Delegates.observable(false) { _, old, new ->
        if (new != old)
            changeIcon(iconHintLong!!, new)

    }



    private fun changeIcon(view: AppCompatImageView, state: Boolean) {
        val animFromDoneToClose: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_done_to_close)
        val animFromCloseToDone: AnimatedVectorDrawableCompat? = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_close_to_done)
        val animation = if (state) animFromCloseToDone else animFromDoneToClose
        if (animation == view.drawable) return
        view.setImageDrawable(animation)
        animation?.start()
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
     */
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(value: CharSequence?, start: Int, before: Int, count: Int) { performOnTextChange(value.toString())}

    private fun performOnTextChange(value: String) {
        if(isShowPassStrength) {
            updatePasswordStrengthView(value)
        }else if(confirmPassword.isNotNull()){
            if(!checkValueNotEmpty(value)){
                if(confirmPassword?.getValue()==value){
                    verifyInputError("", View.GONE)
                }else{
                    verifyInputError(resources.getString(R.string.passwordsDoNotMatch), View.VISIBLE)
                }
            }
        }else{
            checkValueNotEmpty(value)
        }
        mTextChangeListener?.onTextChange(value)
    }

    private fun checkValueNotEmpty(value:String):Boolean{
        return if(value.isEmpty()){
            verifyInputError(String.format(resources.getString(R.string.cantBeEmpty), mLabel), View.VISIBLE)
            true
        }else{
            verifyInputError("", View.GONE)
            false
        }
    }
}