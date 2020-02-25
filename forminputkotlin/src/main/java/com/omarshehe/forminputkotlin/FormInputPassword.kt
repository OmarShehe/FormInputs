package com.omarshehe.forminputkotlin

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.omarshehe.forminputkotlin.utils.PasswordStrength
import com.omarshehe.forminputkotlin.utils.SavedState
import com.omarshehe.forminputkotlin.utils.Utils
import kotlinx.android.synthetic.main.form_input_password.view.*
import kotlin.properties.Delegates

class FormInputPassword : RelativeLayout, TextWatcher {
    var TAG : String ="FormInputPasswordA"
    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mHint: String = ""
    private var mValue : String = ""
    private var mHeight : Int = 100
    private var mErrorMessage :String = ""
    private var mBackground: Int =R.drawable.bg_txt_square
    private var inputError:Int = 1
    private var isMandatory: Boolean = false
    private var isShowPassStrength: Boolean =false
    private var isShowValidIcon= true
    private var mPassLength=8
    private var confirmPassword :FormInputPassword? = null
    private var isConfirmPassword:Boolean=false
    private var isShowLabel:Boolean =true




    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0

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
        LayoutInflater.from(context).inflate(R.layout.form_input_password,this,true)

        /**
         * Get Attributes
         */
        if(context!=null){
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout,0,0)
            mTextColor = a.getResourceId(R.styleable.FormInputLayout_form_textColor,R.color.black)
            mLabel = a.getString(R.styleable.FormInputLayout_form_label).orEmpty()
            mHint = a.getString(R.styleable.FormInputLayout_form_hint).orEmpty()
            mValue= a.getString(R.styleable.FormInputLayout_form_value).orEmpty()
            mHeight = a.getDimension(R.styleable.FormInputLayout_form_height,resources.getDimension( R.dimen.input_box_height)).toInt()
            isMandatory = a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, false)
            mBackground = a.getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square)
            isShowPassStrength = a.getBoolean(R.styleable.FormInputLayout_form_showPassStrength, true)
            isShowValidIcon  = a.getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true)
            isConfirmPassword= a.getBoolean(R.styleable.FormInputLayout_form_confirm, false)
            setLabelVisibility(a.getBoolean(R.styleable.FormInputLayout_form_showLabel, true))


            setIcons()
            mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
            setHint(mHint)
            setValue(mValue)
            height = mHeight
            isConfirm(isConfirmPassword)
            showPassStrength(isShowPassStrength)
            setBackground(mBackground)
            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)
            txtPassword.addTextChangedListener(this)

            setPassLength(mPassLength)
            a.recycle()


        }
    }

    fun setPassLength(passLength:Int) {
        mPassLength=passLength
        tvHintLong.text=resources.getString(R.string.passHintLong,mPassLength)

    }

    private fun setIcons(){
        Utils.setViewVisibility(imgNoError,false)
        imgNoError.setImageResource(R.drawable.check_anim)
    }

    fun setLabel(text:String): FormInputPassword{
        mLabel=Utils.setLabel(tvLabel,text,isMandatory)
        return this
    }


    fun setMandatory(mandatory: Boolean) : FormInputPassword {
        isMandatory =mandatory
        mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
        return this
    }
    fun setLabelVisibility(show:Boolean): FormInputPassword {
        isShowLabel=Utils.setViewVisibility(tvLabel,show)
        return this
    }


    fun setHint(hint: String) :FormInputPassword{
        txtPassword.hint = hint
        return this
    }

    fun setValue(value: String) : FormInputPassword {
        mValue = value
        txtPassword.setText(value)
        return this
    }

    fun setHeight(height: Int) : FormInputPassword {
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
        passView.layoutParams=lp
        return this
    }

    fun showPassStrength(isShowStrength: Boolean): FormInputPassword{
        if(isConfirmPassword) {isShowPassStrength=false}
        isShowPassStrength= Utils.setViewVisibility(layPassStrength,isShowStrength)
        return this
    }

    fun isConfirm(isConfirm: Boolean): FormInputPassword{
        isConfirmPassword= isConfirm
        if(isConfirmPassword) {isShowPassStrength=false}
        return this
    }
    

    fun setViewToConfirm(passwordView:FormInputPassword):FormInputPassword{
        confirmPassword=passwordView
        return this
    }
    fun setBackground(background: Int) : FormInputPassword{
        passView.setBackgroundResource(background)
        return this
    }

    fun showValidIcon(showIcon: Boolean) : FormInputPassword {
        isShowValidIcon=showIcon
        return this
    }

    fun setTextColor(color:Int):FormInputPassword{
        mTextColor=color
        txtPassword.setTextColor(ContextCompat.getColor(context,mTextColor))
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
        return txtPassword.text.toString()
    }

    fun getInputBox(): EditText {
        return txtPassword
    }



    private fun updatePasswordStrengthView(password: String) {
        when {
            password.isEmpty() -> {
                verifyInputError(String.format(resources.getString(R.string.cantBeEmpty), mLabel), View.VISIBLE)
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
        val animFromDoneToClose: AnimatedVectorDrawableCompat? =
            AnimatedVectorDrawableCompat.create(context, R.drawable.ic_done_to_close)
        val animFromCloseToDone: AnimatedVectorDrawableCompat? =
            AnimatedVectorDrawableCompat.create(context, R.drawable.ic_close_to_done)
        val animation = if (state) animFromCloseToDone else animFromDoneToClose
        if (animation == view.drawable) return
        view.setImageDrawable(animation)
        animation?.start()
    }





    /**
     * Errors
     */
    private fun verifyInputError(error: String, visible: Int){
        val errorResult=Utils.showInputError(tvError,imgNoError,isShowValidIcon, error, visible)
        mErrorMessage=errorResult[0].toString()
        inputError=errorResult[1].toString().toInt()
    }

    fun isError(parentView: View?): Boolean {
        return if (inputError == 1) {
            verifyInputError(mErrorMessage, VISIBLE)
            if (parentView != null) {
                Utils.hideKeyboard(context)
                parentView.scrollTo(0, tvError.top)
                txtPassword.requestFocus()
            }
            true
        } else {
            verifyInputError("", View.GONE)
            false
        }
    }


    /**
     * Listener on text change
     */
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        val value=text.toString()
        if(isShowPassStrength) {
            updatePasswordStrengthView(value)
        }else if(isConfirmPassword){
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


    /**
     * Save Instance State of the view
     * */
    public override fun onSaveInstanceState(): Parcelable? {
        return SavedState(super.onSaveInstanceState()).apply {
            childrenStates = saveChildViewStates()
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                state.childrenStates?.let { restoreChildViewStates(it) }
            }
            else -> super.onRestoreInstanceState(state)
        }
    }

    private fun ViewGroup.saveChildViewStates(): SparseArray<Parcelable> {
        val childViewStates = SparseArray<Parcelable>()
        children.forEach { child -> child.saveHierarchyState(childViewStates) }
        return childViewStates
    }

    private fun ViewGroup.restoreChildViewStates(childViewStates: SparseArray<Parcelable>) {
        children.forEach { child -> child.restoreHierarchyState(childViewStates) }
    }

    override
    fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }
    override
    fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }


}