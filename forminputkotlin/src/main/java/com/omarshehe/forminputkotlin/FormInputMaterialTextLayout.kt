package com.omarshehe.forminputkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.*
import android.text.method.MovementMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.google.android.material.textfield.TextInputLayout
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import com.omarshehe.forminputkotlin.interfaces.ViewOnClickListener
import com.omarshehe.forminputkotlin.utils.*

/**
 * Created by omars on 10/16/2019.
 * Author omars
 */
class FormInputMaterialTextLayout: TextInputLayout, TextWatcher {

    private var mPresenter: FormInputContract.Presenter = FormInputPresenterImpl()
    private var inputError: Boolean = true
    private var mErrorMessage: String = ""
    private var isMandatory: Boolean = true
    private var mInputType: Int = 1
    private var viewToConfirm: FormInputMaterialTextLayout? = null
    private var showClearButton: Boolean = true
    private var showLabel: Boolean = true
    private var tempTextHelper: String = ""
    private var defaultTextHelperColor: Int = R.color.colorGrey
    private var defaultTextColor = R.color.black
    private var mClearIcon: Drawable? = getDrawable(R.drawable.ic_close)
    private var mListener: ViewOnClickListener? = null
    private var mTextChangeListener: OnTextChangeListener? = null

    val text: String
        get() = editText?.text?.toString().orEmpty()

    constructor(context: Context): super(context) {
        initView(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initView(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initView(attrs,defStyleAttr)
    }

    private fun initView(attrs: AttributeSet?, styleAttr: Int) {
        mClearIcon?.callback = this
        context.withStyledAttributes(attrs, R.styleable.FormInputLayout, styleAttr, 0) {
            setViewInputType(getInt(R.styleable.FormInputLayout_form_inputType, 1))
            setShowClearButton(getBoolean(R.styleable.FormInputLayout_form_showClearButton, true))
            setShowLabel(getBoolean(R.styleable.FormInputLayout_form_showLabel, true))
            setMandatory(getBoolean(R.styleable.FormInputLayout_form_isMandatory, true))
            setDefaultTextColor(getResourceId(R.styleable.FormInputLayout_android_textColor, R.color.black))
        }
        initTextInputLayout()
        mErrorMessage = formatString()
    }

    /**
     * Set components
     */
    fun setOnViewClickListener(listener: ViewOnClickListener): FormInputMaterialTextLayout {
        mListener = listener
        initClickListener()
        return this
    }

    fun setOnTextChangeListener(listener: OnTextChangeListener): FormInputMaterialTextLayout {
        mTextChangeListener = listener
        return this
    }

    fun setMandatory(mandatory: Boolean): FormInputMaterialTextLayout {
        isMandatory = mandatory
        if (!mandatory) {
            inputError = false
        }
        val stringMandatory = if (isMandatory) "*" else ""
        helperText = context.getString(R.string.label, tempTextHelper, stringMandatory).toHtml()
        setHelperTextColor(ContextCompat.getColorStateList(context, defaultTextHelperColor))
        isHelperTextEnabled = showLabel
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String) {
        editText?.textColor(R.color.colorOnError)
        verifyInputError(errorMessage)
    }

    fun setViewToConfirm(view: FormInputMaterialTextLayout): FormInputMaterialTextLayout {
        viewToConfirm = view
        return this
    }

    fun setViewInputType(type: Int): FormInputMaterialTextLayout {
        mInputType = type
        editText?.apply {
            when (mInputType) {
                INPUT_TYPE_TEXT -> inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                INPUT_TYPE_PHONE -> inputType = InputType.TYPE_CLASS_PHONE
                INPUT_TYPE_NUMBER -> inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                INPUT_TYPE_EMAIL -> inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                INPUT_TYPE_PASSWORD -> {
                    inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    transformationMethod = PasswordTransformationMethod()
                }
                INPUT_TYPE_MULTILINE -> {
                    setScroll()
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                }
            }
        }
        return this
    }

    fun setShowLabel(show: Boolean): FormInputMaterialTextLayout {
        showLabel = show
        return this
    }

    fun setShowClearButton(show: Boolean): FormInputMaterialTextLayout {
        showClearButton = show
        return this
    }

    fun setDefaultTextColor(@ColorRes color: Int): FormInputMaterialTextLayout {
        defaultTextColor = color
        return this
    }

    @SuppressLint("ClickableViewAccessibility", "RtlHardcoded")
    private fun setScroll() {
        editText?.isSingleLine = false
        gravity = Gravity.LEFT or Gravity.TOP
        scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        isVerticalScrollBarEnabled = true
        overScrollMode = 0
        setOnTouchListener {v, event ->
            if (isFocused) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
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

    override fun onTextChanged(value: CharSequence?, start: Int, before: Int, count: Int) {
        if (hasFocus()) {
            showIcon(value.isNullOrBlank())
        }
        performOnTextChange(value.toString())
    }

    private fun performOnTextChange(value: String) {
        if (viewToConfirm != null) {
            if (value.isNotEmpty() && viewToConfirm?.text.toString() == value) {
                verifyInputError("")
            } else {
                verifyInputError(formatString(resources.getString(R.string.doNotMatch)))
            }
        } else if (value.isEmpty()) {
            if (isMandatory) {
                verifyInputError(formatString())
            } else {
                verifyInputError("")
            }
        } else {
            verifyInputError("")
            when (mInputType) {
                INPUT_TYPE_NUMBER -> {
                    if (mPresenter.isValidNumber(value)) {
                        editText?.textColor(defaultTextColor)
                        verifyInputError("")
                    } else {
                        editText?.textColor(R.color.colorOnError)
                        verifyInputError(resources.getString(R.string.isInvalid, tempTextHelper))
                    }
                }
                INPUT_TYPE_EMAIL -> {
                    if (mPresenter.isValidEmail(value)) {
                        editText?.textColor(defaultTextColor)
                        verifyInputError("")
                    } else {
                        editText?.textColor(R.color.colorOnError)
                        verifyInputError(resources.getString(R.string.inValidEmail))
                    }
                }
                INPUT_TYPE_PHONE -> {
                    if (mPresenter.isValidPhoneNumber(value)) {
                        editText?.textColor(defaultTextColor)
                        verifyInputError("")
                    } else {
                        editText?.textColor(R.color.colorOnError)
                        verifyInputError(resources.getString(R.string.inValidPhoneNumber))
                    }
                }
                INPUT_TYPE_URL -> {
                    if (mPresenter.isValidUrl(value)) {
                        editText?.textColor(defaultTextColor)
                        verifyInputError("")
                    } else {
                        editText?.textColor(R.color.colorOnError)
                        verifyInputError(resources.getString(R.string.invalidUrl))
                    }
                }
            }
        }
        mTextChangeListener?.onTextChange(value)
    }

    private fun verifyInputError(mError: String) {
        if (mError.isNotEmpty()) {
            isHelperTextEnabled = showLabel
            helperText = mError
            setHelperTextColor(ContextCompat.getColorStateList(context, R.color.colorOnError))
            error = mError
            errorIconDrawable = null
            inputError = true
            mErrorMessage = mError
        } else {
            inputError = false
            mErrorMessage = ""
            setMandatory(isMandatory)
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
    fun noError(parentView: View? = null, showError: Boolean = true): Boolean {
        inputError.isTrue {
            showError.isTrue {
                verifyInputError(mErrorMessage)
                parentView.hideKeyboard()
                parentView?.scrollTo(0, this.top)
                requestFocus()
            }
        }.isNotTrue {
            verifyInputError("")
        }
        return !inputError
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        showIcon(focused && !TextUtils.isEmpty(text.toString()))
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isClearIconClicked(event) && text.isNotBlank()) {
            editText?.text = null
            event.action = MotionEvent.ACTION_CANCEL
            showIcon(false)
            return false
        }
        return super.onTouchEvent(event)
    }

    private fun showIcon(show: Boolean, icon: Drawable? = mClearIcon) {
        if (showClearButton && show) {
            editText?.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
        } else {
            editText?.setCompoundDrawables(null, null, null, null)
        }
    }

    /**
     * Determine if the clearIcon been clicked.
     * measure the clicked space by [width] of the view minus by the [compoundPaddingRight].
     * Return true if clicked space is greater than measured space. Else return  false
     */
    private fun isClearIconClicked(event: MotionEvent): Boolean {
        val touchPointX = event.x.toInt()
        val widthOfView = width
        return if (editText?.compoundDrawables!![2].isNotNull()) {
            touchPointX >= widthOfView - editText!!.compoundPaddingRight
        } else {
            false
        }
    }

    private fun formatString(string: String = resources.getString(R.string.cantBeEmpty), label: String = tempTextHelper): String {
        return String.format(string, label)
    }

    private fun initClickListener() {
        editText?.movementMethod = object: MovementMethod {
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

        this.setOnClickListener {
            mListener?.onClick()
        }
    }

    /**
     * Initialize mTextInputLayout, get default label and text color
     */
    private fun initTextInputLayout() {
        tempTextHelper = helperText.toString()
    }
}