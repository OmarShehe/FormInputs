package com.omarshehe.forminputkotlin

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.omarshehe.forminputkotlin.utils.FormInputContract
import com.omarshehe.forminputkotlin.utils.FormInputPresenterImpl
import com.omarshehe.forminputkotlin.utils.SavedState
import com.omarshehe.forminputkotlin.utils.Utils
import com.omarshehe.forminputkotlin.utils.Utils.hideKeyboard
import kotlinx.android.synthetic.main.form_input_spinner_inputbox.view.*
import java.util.*

class FormInputSpinnerInputBox  : RelativeLayout, FormInputContract.View, TextWatcher {


    var TAG : String ="FormInputSpinnerInputBoxA"
    private lateinit var mPresenter: FormInputContract.Presenter

    val INPUTTYPE_TEXT = 1
    val INPUTTYPE_PHONE = 2
    val INPUTTYPE_NUMBER = 3
    val INPUTTYPE_EMAIL = 4

    private var mLabel: String = ""
    private var mHint: String = ""
    private var mValue : String = ""
    private var mHeight : Int = 100
    private var mErrorMessage :String = ""
    private var mBackground: Int =R.drawable.bg_txt_square
    private var inputError:Int = 1
    private var isMandatory: Boolean = false
    private var mInputType:Int = 1
    private var mArrayList :List<String> = emptyArray<String>().toList()
    private var firstOpen: Int = 0
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
        LayoutInflater.from(context).inflate(R.layout.form_input_spinner_inputbox, this, true)
        mPresenter = FormInputPresenterImpl(this)
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
            mInputType = a.getInt(R.styleable.FormInputLayout_customer_inputType, 1)
            isShowValidIcon  = a.getBoolean(R.styleable.FormInputLayout_customer_showValidIcon, true)

            val list = a.getResourceId(R.styleable.FormInputLayout_customer_array, R.array.array)
            setIcons()
            mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)

            setHint(mHint)
            height = mHeight
            setInputType(mInputType)
            setBackground(mBackground)
            imgNoError.visibility= GONE

            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)
            txtInputBox.addTextChangedListener(this)


            iconCancel.setOnClickListener { txtInputBox.setText("") }

            val getIntArray = resources.getStringArray(list)
            setSpinner(listOf(*getIntArray))
            setValue(spSpinner.selectedItem.toString(),mValue)
            a.recycle()
        }
    }

    /**
     * Set components
     */
    private fun setIcons(){
        iconCancel.setImageResource(R.drawable.ic_close_grey)
        imgNoError.setImageResource(R.drawable.check_green)
    }
    fun setLabel(text:String): FormInputSpinnerInputBox{
        mLabel=Utils.setLabel(tvLabel,text,isMandatory)
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputSpinnerInputBox {
        isMandatory =mandatory
        mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
        return this
    }

    fun setHint(hint: String) : FormInputSpinnerInputBox {
        txtInputBox.hint = hint
        return this
    }

    fun setHeight(height: Int) : FormInputSpinnerInputBox {
        val lSparams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            height
        )
        val lInparams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )
        //txtInputBox.layoutParams=lInparams
        return this
    }

    fun setValue(spinnerValue: String,inputBoxValue: String ) : FormInputSpinnerInputBox{
        mValue = inputBoxValue
        txtInputBox.setText(inputBoxValue)
        setSpinnerValue(spinnerValue)
        return this
    }

    private fun setSpinnerValue(mValue: String) : FormInputSpinnerInputBox  {
        for (index in mArrayList.indices) {
            if (mValue == mArrayList[index]) {
                spSpinner.setSelection(index)
            }
        }
        return this
    }

    fun setBackground(background: Int) : FormInputSpinnerInputBox  {
        layInputBox.setBackgroundResource(background)
        return this
    }

    fun showValidIcon(showIcon: Boolean) : FormInputSpinnerInputBox {
        isShowValidIcon=showIcon
        return this
    }

    fun setInputType(inputType: Int) : FormInputSpinnerInputBox {
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
        return this
    }


    /**
     * For save Instance State of the view in programmatically access
     */
    fun setID(id:Int):FormInputSpinnerInputBox{
        this.id=id
        return this
    }


    /**
     * Get components
     */
    fun getValue(): Array<String> {
        return arrayOf(spSpinner.selectedItem.toString(), txtInputBox.text.toString())
    }

    fun getSpinner():Spinner{
        return spSpinner
    }
    fun getInputBox():EditText{
        return txtInputBox
    }


    /**
     *  set up Spinner
     */
    fun setSpinner(items: List<String>): FormInputSpinnerInputBox {
        mArrayList=items
        val spinnerArrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSpinner.adapter = spinnerArrayAdapter
        spSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isMandatory && firstOpen!=0) {
                    validateSpinner(mHint)
                }
                firstOpen=1
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return this
    }

    fun setSpinner(items: ArrayList<String>, listener: SpinnerSelectionListener) : FormInputSpinnerInputBox {
        mArrayList=items
        val spinnerArrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                listener.onSpinnerItemSelected(parent.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        spSpinner.adapter = spinnerArrayAdapter

        return this
    }


    private fun validateSpinner(hint: String) {
        if (getValue()[0] == hint) {
            verifyInputError(String.format(resources.getString(R.string.isRequired), mLabel), View.VISIBLE)
        } else {
            if(txtInputBox.text.toString() != ""){
                verifyInputError("", View.GONE)
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
                hideKeyboard(context)
                parentView.scrollTo(0, tvError.top)
                txtInputBox.requestFocus()
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
        if(firstOpen !=0){
            inputBoxOnTextChange(s.toString())
        }
        firstOpen=1

    }

    private fun inputBoxOnTextChange(value: String) {
        mValue=value
        iconCancel.visibility = if (mValue.isNotEmpty()) VISIBLE else GONE

        if (mValue.isEmpty()) {
            if (isMandatory) {
                verifyInputError(String.format(resources.getString(R.string.cantBeEmpty), mLabel), View.VISIBLE)
            } else {
                verifyInputError("", View.GONE)
            }
        } else {
            verifyInputError("", View.GONE)

            if (mInputType == INPUTTYPE_EMAIL) {
                if (mPresenter.isValidEmail(mValue)) {
                    txtInputBox.setTextColor(ContextCompat.getColor(context,R.color.black))
                    verifyInputError("", View.GONE)
                } else {
                    txtInputBox.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    verifyInputError(resources.getString(R.string.inValidEmail), View.VISIBLE)
                }
            }

            if (mInputType == INPUTTYPE_PHONE) {
                if (mPresenter.isValidPhoneNumber(mValue)) {
                    txtInputBox.setTextColor(ContextCompat.getColor(context,R.color.black))
                    verifyInputError("", View.GONE)
                } else {
                    txtInputBox.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    verifyInputError(resources.getString(R.string.inValidPhoneNumber), View.VISIBLE)
                }
            }
        }
    }


    /**
     * Interface and Listener
     */

    interface SpinnerSelectionListener {
        fun onSpinnerItemSelected(item: String)
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