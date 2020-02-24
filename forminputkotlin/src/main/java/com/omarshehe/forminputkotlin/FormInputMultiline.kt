package com.omarshehe.forminputkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.SparseArray
import android.view.*
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.omarshehe.forminputkotlin.utils.FormInputContract
import com.omarshehe.forminputkotlin.utils.FormInputPresenterImpl
import com.omarshehe.forminputkotlin.utils.SavedState
import com.omarshehe.forminputkotlin.utils.Utils
import com.omarshehe.forminputkotlin.utils.Utils.hideKeyboard
import kotlinx.android.synthetic.main.form_input_multiline.view.*
import kotlinx.android.synthetic.main.form_input_multiline.view.imgNoError
import kotlinx.android.synthetic.main.form_input_multiline.view.layInputBox
import kotlinx.android.synthetic.main.form_input_multiline.view.tvError
import kotlinx.android.synthetic.main.form_input_multiline.view.tvLabel
import kotlinx.android.synthetic.main.form_input_text.view.*

class FormInputMultiline  : RelativeLayout, TextWatcher {
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
            mTextColor = a.getResourceId(R.styleable.FormInputLayout_form_textColor,R.color.black)
            mLabel = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_label))
            mHint = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_hint))
            mValue= Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_value))
            mHeight = a.getDimension(R.styleable.FormInputLayout_form_height,resources.getDimension( R.dimen.input_box_height)).toInt()
            isMandatory = a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, false)
            mBackground = a.getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square)
            mMaxLines = a.getInt(R.styleable.FormInputLayout_form_maxLines, 5)
            mMaxLength = a.getInt(R.styleable.FormInputLayout_form_maxLength, 300)
            isShowValidIcon  = a.getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true)

            setIcons()
            mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)

            setHint(mHint)
            setValue(mValue)
            height()
            setBackground(mBackground)
            setScroll()
            setMaxLength(mMaxLength)
            setMaxLines(mMaxLines)
            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)
            txtMultiline.addTextChangedListener(this)
            a.recycle()
        }
    }

    /**
     * Set components
     */
    private fun setIcons(){
        imgNoError.setImageResource(R.drawable.check_green)
    }

    fun setLabel(text:String): FormInputMultiline{
        mLabel=Utils.setLabel(tvLabel,text,isMandatory)
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputMultiline {
        isMandatory =mandatory
        mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
        return this
    }


    fun setHint(hint: String) : FormInputMultiline {
        txtMultiline.hint = hint
        return this
    }

    fun setValue(value: String) : FormInputMultiline{
        mValue = value
        txtMultiline.setText(value)
        return this
    }

    private fun height(){
        txtMultiline.layoutParams=LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight)
    }

    fun setHeight(height: Int) : FormInputMultiline {
        mHeight = (height * Resources.getSystem().displayMetrics.density).toInt()
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
        txtMultiline.setSingleLine(false)
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
        txtMultiline.maxLines = maxLines
        return this
    }

    fun setBackground(background: Int) : FormInputMultiline{
        layInputBox.setBackgroundResource(background)
        return this
    }

    fun showValidIcon(showIcon: Boolean) : FormInputMultiline {
        isShowValidIcon=showIcon
        return this
    }

    fun setTextColor(color:Int):FormInputMultiline{
        mTextColor=color
        txtInputBox.setTextColor(ContextCompat.getColor(context,mTextColor))
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
        val errorResult=Utils.showInputError(tvError,imgNoError,isShowValidIcon, error, visible)
        mErrorMessage=errorResult[0].toString()
        inputError=errorResult[1].toString().toInt()
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