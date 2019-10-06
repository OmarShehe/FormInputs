package com.omarshehe.forminputkotlin

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.view.children
import com.omarshehe.forminputkotlin.utils.PasswordStrength
import com.omarshehe.forminputkotlin.utils.SavedState
import com.omarshehe.forminputkotlin.utils.Utils
import kotlinx.android.synthetic.main.form_input_password.view.*

class FormInputPassword : RelativeLayout, TextWatcher {
    var TAG : String ="FormInputPasswordA"
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
            mLabel = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_customer_label))
            mHint = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_customer_hint))
            mValue= Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_customer_value))
            mHeight = a.getDimension(R.styleable.FormInputLayout_customer_height,resources.getDimension( R.dimen.input_box_height)).toInt()
            isMandatory = a.getBoolean(R.styleable.FormInputLayout_customer_isMandatory, false)
            mBackground = a.getResourceId(R.styleable.FormInputLayout_customer_background, R.drawable.bg_txt_square)
            isShowPassStrength = a.getBoolean(R.styleable.FormInputLayout_customer_showPassStrength, true)
            isShowValidIcon  = a.getBoolean(R.styleable.FormInputLayout_customer_showValidIcon, true)

            setIcons()
            mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
            setHint(mHint)
            setValue(mValue)
            height = mHeight
            showPassStrength(isShowPassStrength)
            setBackground(mBackground)
            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)
            if(isShowPassStrength){ txtPassword.addTextChangedListener(this) }
            a.recycle()


        }
    }

    private fun setIcons(){
        Utils.setViewVisibility(imgNoError,false)
        imgNoError.setImageResource(R.drawable.check_green)
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
        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height)
        txtPassword.layoutParams=lp
        return this
    }

    fun showPassStrength(isShowStrength: Boolean): FormInputPassword{
        isShowPassStrength= Utils.setViewVisibility(layPassStrength,isShowStrength)
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
                return
            }
            password.length < 8 -> verifyInputError(String.format(resources.getString(R.string.hintPassword), mLabel), View.VISIBLE)
            else -> verifyInputError("", View.GONE)
        }

        val str = PasswordStrength().calculateStrength(password)

        tvPassStrength.text = str.toString()
        tvPassStrength.setTextColor(str.getColor())

       // pgPassStrength.progressDrawable.setColorFilter(str.getColor(), PorterDuff.Mode.SRC_ATOP)

        when {
            str.toString() == "Weak" -> {

                passView.updateProgress(1,str.getColor())
            }
            str.toString() == "Medium" ->{

                passView.updateProgress(3,str.getColor())
            }
            else ->{
                passView.updateProgress(4,str.getColor())
            }
        }
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

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(isShowPassStrength){
            updatePasswordStrengthView(s.toString())
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