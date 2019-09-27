package com.omarshehe.forminputkotlin

import android.annotation.SuppressLint
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
import android.widget.RelativeLayout
import androidx.core.view.children
import com.omarshehe.forminputkotlin.utils.PasswordStrength
import com.omarshehe.forminputkotlin.utils.SavedState
import kotlinx.android.synthetic.main.form_input_password.view.*

class FormInputPassword :RelativeLayout, TextWatcher {

    var TAG : String ="FormInputPasswordA"


    private var mLabel: String = ""
    private var mHint: String = ""
    private var mValue : String = ""
    private var mErrorMessage :String = ""
    private var mBackground: Int =R.drawable.bg_txt_square
    private var inputError:Int = 1
    private var isMandatory: Boolean = false
    private var isShowPassStrength: Boolean =false






    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        LayoutInflater.from(context).inflate(R.layout.form_input_password,this,true)


        /**
         * Get Attributes
         */
        @SuppressLint("CustomViewStyleable", "Recycle")
        if(context!=null){
            val a = context.obtainStyledAttributes(attrs, R.styleable.FormInputLayout)
            mLabel = a.getString(R.styleable.FormInputLayout_customer_label) as String
            mValue = a.getString(R.styleable.FormInputLayout_customer_value) as String
            mHint = a.getString(R.styleable.FormInputLayout_customer_hint) as String
            isMandatory = a.getBoolean(R.styleable.FormInputLayout_customer_isMandatory, false)
            mBackground = a.getResourceId(R.styleable.FormInputLayout_customer_background, R.drawable.bg_txt_square)
            isShowPassStrength = a.getBoolean(R.styleable.FormInputLayout_customer_showPassStrength, true)

            setLabel(mLabel)
            setMandatory(isMandatory)
            setHint(mHint)
            showPassStrength(isShowPassStrength)
            setBackground(mBackground)
            imgNoError.visibility= GONE
            setValue(mValue)
            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), tvLabel.text)
            if(isShowPassStrength){ txtPassword.addTextChangedListener(this) }


        }

    }



    private fun setLabel(label: String) {
        if (mLabel != "") {
            setLabelVisibility(true)
            tvLabel.text = label
        } else {
            setLabelVisibility(false)
        }
    }
    private fun setLabelVisibility(shouldShow: Boolean) {
        tvLabel.visibility = if (shouldShow) VISIBLE else GONE
    }

    private fun setMandatory(mandatory: Boolean) {
        isMandatory = mandatory
        tvMandatory.visibility = if (isMandatory) VISIBLE else GONE
    }

    private fun setHint(hint: String) {
            txtPassword.hint = hint
    }

    fun setValue(value: String) {
        mValue = value
        txtPassword.setText(value)
    }

    private fun showPassStrength(isShowStrength: Boolean) {
        layPassStrength.visibility = if (isShowStrength) VISIBLE else GONE
    }
    fun setBackground(background: Int) {
        passView.setBackgroundResource(background)
    }
    private fun showNoErrorIcon(visibility: Int){
        imgNoError.visibility= visibility
    }


    private fun updatePasswordStrengthView(password: String) {
        when {
            password.isEmpty() -> {
                showInputError(
                    String.format(resources.getString(R.string.cantBeEmpty), tvLabel.text),
                    View.VISIBLE
                )
                pgPassStrength.progress = 0
                return
            }
            password.length < 8 -> showInputError(
                String.format(resources.getString(R.string.hintPassword), mLabel),
                View.VISIBLE
            )
            else -> showInputError("", View.GONE)
        }

        val str = PasswordStrength().calculateStrength(password)

        tvPassStrength.text = str.toString()
        tvPassStrength.setTextColor(str.getColor())

        Log.d(TAG,str.toString())
        pgPassStrength.progressDrawable.setColorFilter(str.getColor(), PorterDuff.Mode.SRC_ATOP)

        when {
            str.toString() == "Weak" -> pgPassStrength.progress = 25
            str.toString() == "Medium" -> pgPassStrength.progress = 75
            else -> pgPassStrength.progress = 100
        }
    }

    private fun showInputError(error: String, visible: Int) {
        mErrorMessage = error
        tvError.text = error
        tvError.visibility = visible
        inputError = if (visible == VISIBLE) {
            showNoErrorIcon(GONE)
            1
        } else {
            showNoErrorIcon(VISIBLE)
            0
        }
    }

    fun isError(parentView: View?): Boolean {
        return if (inputError == 1) {
            showInputError(mErrorMessage, View.VISIBLE)
            if (parentView != null) {
                parentView.scrollTo(0, tvError.top)
                txtPassword.requestFocus()
            }
            true
        } else {
            showInputError("", View.GONE)
            false
        }
    }


    fun getValue(): String {
        return txtPassword.text.toString()
    }


    fun getPasswordInput(): EditText {
        return txtPassword
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
            Log.d(TAG,s.toString())
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