package com.omarshehe.forminputkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.children
import com.omarshehe.forminputkotlin.utils.FormInputContract
import com.omarshehe.forminputkotlin.utils.FormInputPresenterImpl
import com.omarshehe.forminputkotlin.utils.SavedState
import kotlinx.android.synthetic.main.form_input_text.view.*


class FormInputText : RelativeLayout, FormInputContract.View, TextWatcher  {


    var TAG : String ="FormInputLayout"
    private lateinit var mPresenter: FormInputContract.Presenter

    val INPUTTYPE_TEXT = 1
    val INPUTTYPE_PHONE = 2
    val INPUTTYPE_NUMBER = 3
    val INPUTTYPE_EMAIL = 4

    private var mLabel: String = ""
    private var mHint: String = ""
    private var mValue : String = ""
    private var mErrorMessage :String = ""
    private var mBackground: Int =R.drawable.bg_txt_square
    private var inputError:Int = 1
    private var isMandatory: Boolean = false
    private var mInputType:Int = 1


    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        LayoutInflater.from(context).inflate(R.layout.form_input_text, this, true)

        mPresenter = FormInputPresenterImpl(this)
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
            mInputType = a.getInt(R.styleable.FormInputLayout_customer_inputType, 1)

            setLabel(mLabel)
            setMandatory(isMandatory)
            setInputType(mInputType)
            setHint(mHint)
            setBackground(mBackground)
            imgNoError.visibility= GONE
            setValue(mValue)
            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), tvLabel.text)
            txtInputBox.addTextChangedListener(this)

            iconCancel.visibility=GONE
            iconCancel.setOnClickListener { txtInputBox.setText("") }

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
        txtInputBox.hint = hint
    }

    fun setValue(value: String) {
        mValue = value
        txtInputBox.setText(value)
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
                txtInputBox.requestFocus()
            }
            true
        } else {
            showInputError("", View.GONE)
            false
        }
    }

    fun getValue(): String {
        return txtInputBox.text.toString()
    }

    private fun setInputType(inputType: Int) {
        mInputType = inputType

        when (mInputType) {
            INPUTTYPE_TEXT -> {
                txtInputBox.inputType = InputType.TYPE_CLASS_TEXT
                txtInputBox.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            }
            INPUTTYPE_PHONE -> txtInputBox.inputType = InputType.TYPE_CLASS_PHONE
            INPUTTYPE_NUMBER -> txtInputBox.inputType = InputType.TYPE_CLASS_NUMBER
            INPUTTYPE_EMAIL -> txtInputBox.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

    }


    fun setBackground(background: Int) {
        layInputBox.setBackgroundResource(background)
    }


    private fun showNoErrorIcon(visibility: Int){
        imgNoError.visibility= visibility
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

        if (mValue.isEmpty()) {
            if (isMandatory) {
                showInputError(String.format(resources.getString(R.string.cantBeEmpty), tvLabel.text), View.VISIBLE)
            } else {
                showInputError("", View.GONE)
            }
        } else {
            showInputError("", View.GONE)

            if (mInputType == INPUTTYPE_EMAIL) {
                Log.d("EMAIL",mPresenter.isValidEmail(mValue).toString())
                if (mPresenter.isValidEmail(mValue)) {
                    txtInputBox.setTextColor(resources.getColor(R.color.black))
                    showInputError("", View.GONE)
                } else {
                    txtInputBox.setTextColor(resources.getColor(R.color.red))
                    showInputError(resources.getString(R.string.inValidEmail), View.VISIBLE)
                }
            }

            if (mInputType == INPUTTYPE_PHONE) {
                if (mPresenter.isValidPhoneNumber(mValue)) {
                    txtInputBox.setTextColor(resources.getColor(R.color.black))
                    showInputError("", View.GONE)
                } else {
                    txtInputBox.setTextColor(resources.getColor(R.color.red))
                    showInputError(resources.getString(R.string.inValidPhoneNumber), View.VISIBLE)
                }
            }
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