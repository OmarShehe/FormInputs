package com.omarshehe.forminputkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.SparseArray
import android.view.*
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.view.children
import com.omarshehe.forminputkotlin.utils.FormInputContract
import com.omarshehe.forminputkotlin.utils.FormInputPresenterImpl
import com.omarshehe.forminputkotlin.utils.SavedState
import kotlinx.android.synthetic.main.form_input_multiline.view.*

class FormInputMultiline  : RelativeLayout, FormInputContract.View, TextWatcher {


    var TAG : String ="FormInputMultilineA"
    private lateinit var mPresenter: FormInputContract.Presenter


    private var mLabel: String = ""
    private var mHint: String = ""
    private var mValue : String = ""
    private var mErrorMessage :String = ""
    private var mBackground: Int =R.drawable.bg_txt_square
    private var inputError:Int = 1
    private var isMandatory: Boolean = false

    private var mMaxLength:Int = 0
    private var mHeight: Int = 200
    private var mMaxLines: Int = 5


    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        LayoutInflater.from(context).inflate(R.layout.form_input_multiline, this, true)

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
            mMaxLines = a.getInt(R.styleable.FormInputLayout_customer_maxLines, 5)
            mMaxLength = a.getInt(R.styleable.FormInputLayout_customer_maxLength, 300)
            mHeight = a.getInt(R.styleable.FormInputLayout_customer_height, 200)

            setLabel(mLabel)
            setMandatory(isMandatory)
            setHint(mHint)
            setBackground(mBackground)
            imgNoError.visibility= GONE
            setValue(mValue)
            setScroll()
            setMaxLength(mMaxLength)
            setMaxLines(mMaxLines)

            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), tvLabel.text)
            txtMultiline.addTextChangedListener(this)




        }
    }


    fun setMaxLength(getMaxLength: Int) {
        mMaxLength = getMaxLength
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(mMaxLength)
        txtMultiline.filters = filterArray
        countRemainInput()
    }

    @SuppressLint("ClickableViewAccessibility", "RtlHardcoded")
    private fun setScroll() {

        val lparams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            mHeight
        )
        txtMultiline.layoutParams = lparams
        txtMultiline.setSingleLine(false)
        txtMultiline.gravity = Gravity.LEFT or Gravity.TOP
        txtMultiline.setPadding(15, 15, 15, 15)


        txtMultiline.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        txtMultiline.isVerticalScrollBarEnabled = true
        txtMultiline.overScrollMode = 0
        txtMultiline.height = mHeight
        txtMultiline.setOnTouchListener { v, event ->

            v.parent.requestDisallowInterceptTouchEvent(true)
            if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }

            false
        }
    }

    fun setMaxLines(maxLines: Int) {
        txtMultiline.maxLines = maxLines
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
        txtMultiline.hint = hint
    }

    fun setValue(value: String) {
        mValue = value
        txtMultiline.setText(value)
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
                txtMultiline.requestFocus()
            }
            true
        } else {
            showInputError("", View.GONE)
            false
        }
    }

    fun getValue(): String {
        return txtMultiline.text.toString()
    }

    fun getTextInput() : EditText{
        return txtMultiline
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
        if (mValue.isEmpty()) {
            if (isMandatory) {
                showInputError(String.format(resources.getString(R.string.cantBeEmpty), tvLabel.text), View.VISIBLE)
            } else {
                showInputError("", View.GONE)
            }

        } else {
            showInputError("", View.GONE)
        }
        countRemainInput()
    }

    @SuppressLint("SetTextI18n")
    private fun countRemainInput(){
        val rem = mMaxLength - mValue.length
        txtLengthDesc.text = "$rem / $mMaxLength Characters Only"
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