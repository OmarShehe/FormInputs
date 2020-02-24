package com.omarshehe.forminputkotlin

import android.content.Context
import android.os.Parcelable
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.TextWatcher
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.util.SparseArray
import android.view.*
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.omarshehe.forminputkotlin.utils.FormInputContract
import com.omarshehe.forminputkotlin.utils.FormInputPresenterImpl
import com.omarshehe.forminputkotlin.utils.SavedState
import com.omarshehe.forminputkotlin.utils.Utils
import com.omarshehe.forminputkotlin.utils.Utils.hideKeyboard
import kotlinx.android.synthetic.main.form_input_text.view.*


class FormInputText : RelativeLayout, TextWatcher  {
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
    private var mBackground: Int =R.drawable.bg_txt_square
    private var inputError:Int = 1
    private var isMandatory: Boolean = false
    private var mInputType:Int = 1
    private var isShowValidIcon= true
    private var viewToConfirm :FormInputText? = null
    private var isConfirmText:Boolean=false

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
            mTextColor = a.getResourceId(R.styleable.FormInputLayout_form_textColor,R.color.black)
            mLabel = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_label))
            mHint = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_hint))
            mValue=Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_value))
            mHeight = a.getDimension(R.styleable.FormInputLayout_form_height,resources.getDimension( R.dimen.input_box_height)).toInt()
            mBackground = a.getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square)
            isMandatory = a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, false)
            isShowValidIcon  = a.getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true)
            mInputType = a.getInt(R.styleable.FormInputLayout_form_inputType, 1)
            isConfirmText= a.getBoolean(R.styleable.FormInputLayout_form_confirm, false)

            setIcons()
            mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)

            setHint(mHint)
            setValue(mValue)
            height = mHeight
            setInputType(mInputType)
            setBackground(mBackground)
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
        mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
        return this
    }

    fun setHint(hint: String) :FormInputText {
        txtInputBox.hint = hint
        return this
    }

    fun setValue(value: String) :FormInputText {
        mValue = value
        txtInputBox.setText(value)
        return this
    }

    fun setHeight(height: Int) : FormInputText {
        val lp = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        txtInputBox.layoutParams=lp
        return this
    }

    fun setBackground(background: Int) : FormInputText  {
        layInputBox.setBackgroundResource(background)
        return this
    }

    fun setConfirmConfirmText(isConfirm: Boolean): FormInputText{
        isConfirmText= isConfirm
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

        if(isConfirmText){
            if(mValue.isNotEmpty() && viewToConfirm?.getValue()==mValue){
                setTextColor(mTextColor)
                verifyInputError("", View.GONE)
            }else{
                txtInputBox.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                verifyInputError(String.format(resources.getString(R.string.doNotMatch),mLabel), View.VISIBLE)
            }
        }else if (mValue.isEmpty()) {
            if (isMandatory) {
                verifyInputError(
                    String.format(resources.getString(R.string.cantBeEmpty), mLabel),
                    View.VISIBLE
                )
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