package com.omarshehe.forminputkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.*
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.omarshehe.forminputkotlin.utils.FormInputContract
import com.omarshehe.forminputkotlin.utils.FormInputPresenterImpl
import com.omarshehe.forminputkotlin.utils.Utils
import com.omarshehe.forminputkotlin.utils.Utils.hideKeyboard
import kotlinx.android.synthetic.main.form_input_multiline.view.*

class FormInputMultiline  :BaseFormInput, TextWatcher {
    private lateinit var mPresenter: FormInputContract.Presenter
    private var mTextColor=R.color.black
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
    private var isShowValidIcon= true
    private var isShowLabel:Boolean =true

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

    private fun initView(){
        LayoutInflater.from(context).inflate(R.layout.form_input_multiline, this, true)
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
            setHeight(a.getDimensionPixelSize(R.styleable.FormInputLayout_form_height,resources.getDimensionPixelSize( R.dimen.formInputInput_box_height)))
            setBackground(a.getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square))
            showValidIcon(a.getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true))
            setLabelVisibility(a.getBoolean(R.styleable.FormInputLayout_form_showLabel, true))

            setMaxLines(a.getInt(R.styleable.FormInputLayout_form_maxLines, 5))
            setMaxLength( a.getInt(R.styleable.FormInputLayout_form_maxLength, 300))


            setIcons()

            setScroll()
            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)
            txtMultiline.addTextChangedListener(this)
            a.recycle()
        }
    }

    /**
     * Set components
     */
    private fun setIcons(){
        validIcon.setImageResource(R.drawable.check_green)
    }

    fun setLabel(text:String): FormInputMultiline{
        mLabel=Utils.setLabel(tvLabel,text,isMandatory)
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputMultiline {
        isMandatory =mandatory
        if(!mandatory){ inputError=0 }
        mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
        return this
    }

    fun setLabelVisibility(show:Boolean): FormInputMultiline {
        isShowLabel=Utils.setViewVisibility(tvLabel,show)
        return this
    }


    fun setHint(hint: String) : FormInputMultiline {
        mHint=hint
        txtMultiline.hint = hint
        return this
    }

    fun setValue(value: String) : FormInputMultiline{
        mValue = value
        txtMultiline.setText(value)
        return this
    }

    fun setHeight(height: Int) : FormInputMultiline {
        mHeight =  height
        val lp = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight)
        txtMultiline.layoutParams = lp
        return this
    }

    fun setMaxLength(getMaxLength: Int) : FormInputMultiline{
        mMaxLength = getMaxLength
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(mMaxLength)
        txtMultiline.filters = filterArray
        countRemainInput()
        return this
    }

    @SuppressLint("ClickableViewAccessibility", "RtlHardcoded")
    private fun setScroll() {
        txtMultiline.isSingleLine = false
        txtMultiline.gravity = Gravity.LEFT or Gravity.TOP
        txtMultiline.setPadding(15, 15, 15, 15)
        txtMultiline.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        txtMultiline.isVerticalScrollBarEnabled = true
        txtMultiline.overScrollMode = 0
        txtMultiline.setOnTouchListener { v, event ->
            if(txtMultiline.isFocused){
                v.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }

            }
            false

        }
    }

    fun setMaxLines(maxLines: Int) : FormInputMultiline{
        mMaxLines=maxLines
        txtMultiline.maxLines = maxLines
        return this
    }

    fun setBackground(background: Int) : FormInputMultiline{
        mBackground=background
        layInputBox.setBackgroundResource(background)
        return this
    }

    fun showValidIcon(showIcon: Boolean) : FormInputMultiline {
        isShowValidIcon=showIcon
        return this
    }

    fun setTextColor(color:Int):FormInputMultiline{
        mTextColor=color
        txtMultiline.setTextColor(ContextCompat.getColor(context,mTextColor))
        return this
    }


    /**
     * For save Instance State of the view in programmatically access
     */
    fun setID(id:Int):FormInputMultiline{
        this.id=id
        return this
    }


    /**
     * Get components
     */
    fun getValue(): String {
        return txtMultiline.text.toString()
    }

    fun getInputBox() : EditText{
        return txtMultiline
    }

    /**
     * Errors
     */
    private fun verifyInputError(error: String, visible: Int){
        val errorResult=Utils.showInputError(tvError,validIcon,checkIfShouldShowValidIcon(), error, visible)
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
                txtMultiline.requestFocus()
            }
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
        if (mValue.isEmpty()) {
            if (isMandatory) {
                verifyInputError(String.format(resources.getString(R.string.cantBeEmpty), mLabel), View.VISIBLE)
            } else {
                verifyInputError("", View.GONE)
            }

        } else {
            verifyInputError("", View.GONE)
        }
        countRemainInput()
    }

    @SuppressLint("SetTextI18n")
    private fun countRemainInput(){
        val rem = mMaxLength - mValue.length
        txtLengthDesc.text = "$rem / $mMaxLength Characters Only"
    }
}