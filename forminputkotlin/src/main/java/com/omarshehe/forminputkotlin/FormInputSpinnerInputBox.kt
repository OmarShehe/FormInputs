package com.omarshehe.forminputkotlin

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.core.content.withStyledAttributes
import com.omarshehe.forminputkotlin.interfaces.ItemSelectedListener
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import com.omarshehe.forminputkotlin.utils.*
import kotlinx.android.synthetic.main.form_input_spinner_inputbox.view.*

class FormInputSpinnerInputBox  : BaseFormInput, TextWatcher {
    private lateinit var mPresenter: FormInputContract.Presenter

    private var inputError:Boolean = true
    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mErrorMessage :String = ""
    private var isMandatory: Boolean = false
    private var mInputType:Int = 1
    private var mArrayList :List<String> = emptyArray<String>().toList()
    private var isFirstOpen: Boolean = true
    private var mListener : ItemSelectedListener? =null
    private var mTextChangeListener : OnTextChangeListener? =null

    private var showValidIcon = true

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
        inflate(context, R.layout.form_input_spinner_inputbox, this)
        mPresenter = FormInputPresenterImpl()
        orientation= VERTICAL
        context.withStyledAttributes(attrs, R.styleable.FormInputLayout, styleAttr, 0) {
            setTextColor( getResourceId(R.styleable.FormInputLayout_form_textColor,R.color.black))
            setHintTextColor(getResourceId(R.styleable.FormInputLayout_form_textColorHint,R.color.hint_text_color))
            setLabelTextColor(getResourceId(R.styleable.FormInputLayout_form_textColorLabel,R.color.black))
            setMandatory( getBoolean(R.styleable.FormInputLayout_form_isMandatory, true))
            setLabel(getString(R.styleable.FormInputLayout_form_label).orEmpty())
            setHint(getString(R.styleable.FormInputLayout_form_hint).orEmpty())
            setSpinnerHeight(getDimension(R.styleable.FormInputLayout_form_height, resources.getDimension(R.dimen.formInputInput_box_height)).toInt())
            setBackground(getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square))
            showValidIcon(getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true))
            setInputType( getInt(R.styleable.FormInputLayout_form_inputType, 1))
            setLabelVisibility(getBoolean(R.styleable.FormInputLayout_form_showLabel, true))

            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)

            val itemList = resources.getStringArray(getResourceId(R.styleable.FormInputLayout_form_array, R.array.array)).toList()
            setSpinner(itemList)
            setValue(spSpinner.selectedItem.toString(),getString(R.styleable.FormInputLayout_form_value).orEmpty())
        }
        txtInputBox.addTextChangedListener(this)
        iconCancel.setOnClickListener { txtInputBox.setText("") }
    }

    /**
     * Set components
     */
    fun setLabel(text:String): FormInputSpinnerInputBox{
        mLabel=tvLabel.setLabel(text,isMandatory)
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputSpinnerInputBox {
        isMandatory =mandatory
        if(!mandatory){ inputError=false }
        mLabel=tvLabel.setLabel(mLabel,isMandatory)
        return this
    }
    fun setLabelVisibility(show:Boolean): FormInputSpinnerInputBox {
        tvLabel.visibleIf(show)
        return this
    }

    fun setHint(hint: String) : FormInputSpinnerInputBox {
        txtInputBox.hint = hint
        return this
    }

    fun setSpinnerHeight(height: Int) : FormInputSpinnerInputBox {
        layInputBox.layoutParams=LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        return this
    }

    fun setValue(spinnerValue: String,inputBoxValue: String) : FormInputSpinnerInputBox{
        txtInputBox.setText(inputBoxValue)
        setSpinnerValue(spinnerValue)
        return this
    }

    private fun setSpinnerValue(value: String) : FormInputSpinnerInputBox  {
        for (index in mArrayList.indices) {
            if (value == mArrayList[index]) {
                spSpinner.setSelection(index)
            }
        }
        return this
    }

    fun setBackground(background: Int) : FormInputSpinnerInputBox  {
        layInputBox.setBackgroundResource(background)
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String){
        txtInputBox.textColor(R.color.colorOnError)
        verifyInputError(errorMessage, VISIBLE)
    }

    fun showValidIcon(showIcon: Boolean) : FormInputSpinnerInputBox {
        showValidIcon=showIcon
        return this
    }

    fun setInputType(inputType: Int) : FormInputSpinnerInputBox {
        mInputType = inputType
        txtInputBox.setInputTypes(mInputType)
        return this
    }

    fun setOnTextChangeListener(listener: OnTextChangeListener):FormInputSpinnerInputBox{
        mTextChangeListener=listener
        return this
    }

    fun setTextColor(color:Int):FormInputSpinnerInputBox{
        mTextColor=color
        txtInputBox.textColor(mTextColor)
        return this
    }

    fun setHintTextColor(@ColorRes color: Int):FormInputSpinnerInputBox{
        txtInputBox.hintTextColor(color)
        return this
    }

    fun setLabelTextColor(@ColorRes color: Int):FormInputSpinnerInputBox{
        tvLabel.textColor(color)
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
        val spinnerArrayAdapter = ArrayAdapter(context, R.layout.spinner_item, items)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSpinner.adapter = spinnerArrayAdapter
        initClickListener()
        return this
    }

    /**
     * pass [adapter] and [items]
     * [items] will be used when [setValue] is called
     */
    fun setAdapter(adapter: BaseAdapter,items: List<String>):FormInputSpinnerInputBox {
        mArrayList=items
        spSpinner.adapter = adapter
        return this
    }


    fun setOnSpinnerItemSelected(listener: ItemSelectedListener):FormInputSpinnerInputBox{
        mListener=listener
        return this
    }

    private fun initClickListener(){
        spSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                isFirstOpen.isTrue{
                    (view as TextView?)?.textColor(mTextColor)
                }.isNotTrue{
                    (view as TextView?)?.textColor(mTextColor)
                    mListener?.onItemSelected(parent.selectedItem.toString())
                }
                isFirstOpen=false
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    /**
     * Errors
     */
    private fun verifyInputError(stringError: String, visible: Int){
        mErrorMessage=stringError
        inputError=tvError.showInputError(validIcon,checkIfShouldShowValidIcon(),stringError,visible)
    }

    private fun checkIfShouldShowValidIcon():Boolean{
        return if(getValue()[1].isBlank()){
            false
        }else{
            showValidIcon
        }
    }


    /**
     * Check if there is an error.
     * if there any
     * * * return true,
     * * * hide softKeyboard
     * * * scroll top to the view
     * * * put view on focus
     * * * show error message
     * else return false
     * set [showError] to false if you want to get only the return value
     */
    fun noError(parentView: View? = null, showError:Boolean=true):Boolean{
        inputError.isTrue {
            showError.isTrue {
                verifyInputError(mErrorMessage, VISIBLE)
                parentView.hideKeyboard()
                parentView?.scrollTo(0, tvError.top)
                txtInputBox.requestFocus()
            }
        }.isNotTrue {
            verifyInputError("", View.GONE)
        }
        return !inputError
    }


    /**
     * Listener on text change
     * */
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(value: CharSequence?, start: Int, before: Int, count: Int) { performOnTextChange(value.toString()) }

    private fun performOnTextChange(value: String) {
        iconCancel.visibleIf(value.isNotEmpty())

        /**
         *  If the [value] is empty, show input error only if [isMandatory] is true
         */
        if (value.isEmpty()) {
            isMandatory.isTrue {
                verifyInputError(String.format(resources.getString(R.string.cantBeEmpty), mLabel), View.VISIBLE)
            }.isNotTrue{
                verifyInputError("", View.GONE)
            }

            /**
             * The [value] is not empty, remove error.
             * Validate view based on [mInputType]
             */
        } else {
            verifyInputError("", View.GONE)

            when(mInputType){
                INPUT_TYPE_NUMBER->{
                    if(mPresenter.isValidNumber(value) ){
                        setTextColor(mTextColor)
                        verifyInputError("", GONE)
                    }else{
                        txtInputBox.textColor(R.color.colorOnError)
                        verifyInputError(String.format(resources.getString(R.string.isInvalid), mLabel), VISIBLE)
                    }
                }

                INPUT_TYPE_EMAIL-> {
                    if (mPresenter.isValidEmail(value)) {
                        setTextColor(mTextColor)
                        verifyInputError("", GONE)
                    } else {
                        txtInputBox.textColor(R.color.colorOnError)
                        verifyInputError(resources.getString(R.string.inValidEmail), VISIBLE)
                    }
                }

                INPUT_TYPE_PHONE-> {
                    if (mPresenter.isValidPhoneNumber(value)) {
                        setTextColor(mTextColor)
                        verifyInputError("", GONE)
                    } else {
                        txtInputBox.textColor(R.color.colorOnError)
                        verifyInputError(resources.getString(R.string.inValidPhoneNumber), VISIBLE)
                    }
                }

                INPUT_TYPE_URL->{
                    if (mPresenter.isValidUrl(value)) {
                        setTextColor(mTextColor)
                        verifyInputError("", GONE)
                    } else {
                        txtInputBox.textColor(R.color.colorOnError)
                        verifyInputError(resources.getString(R.string.invalidUrl), VISIBLE)
                    }
                }
            }

        }
        mTextChangeListener?.onTextChange(value)
    }
}