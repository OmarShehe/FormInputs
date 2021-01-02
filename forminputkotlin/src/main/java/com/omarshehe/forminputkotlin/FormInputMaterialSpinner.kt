package com.omarshehe.forminputkotlin

import android.content.Context
import android.os.Parcelable
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.omarshehe.forminputkotlin.interfaces.SpinnerSelectionListener
import com.omarshehe.forminputkotlin.utils.*


class FormInputMaterialSpinner : MaterialAutoCompleteTextView {
    private var inputError:Boolean = true
    private var mErrorMessage :String = ""
    private var isMandatory: Boolean = true
    private var showLabel:Boolean =true
    private var mTextInputLayout: TextInputLayout? =null
    private var tempTextHelper:String =""
    private var mHint: String = ""
    private var defaultTextHelperColor:Int =R.color.colorGrey

    private var mArrayList :List<String> = emptyList()
    private var mListener : SpinnerSelectionListener? =null

    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0

    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.attrs=attrs
        initView()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.attrs = attrs
        styleAttr=defStyleAttr
        initView()
    }



    private fun initView() {
        disableEditable()
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout, styleAttr, 0)
        setShowLabel(a.getBoolean(R.styleable.FormInputLayout_form_showLabel, true))
        setMandatory(a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, true))
        setLabel(a.getString(R.styleable.FormInputLayout_form_label).orEmpty())
        setViewHint(a.getString(R.styleable.FormInputLayout_form_hint).orEmpty())

        val list = a.getResourceId(R.styleable.FormInputLayout_form_array, R.array.array)
        setListAdapter(resources.getStringArray(list).toList())

        initTextInputLayout()
        a.recycle()

    }

    private fun disableEditable(){
        inputType = InputType.TYPE_NULL
    }

    /**
     * Set components
     */
    fun setTextInputLayout(textInputLayout: TextInputLayout){
        mTextInputLayout=textInputLayout
        initTextInputLayout()
        mErrorMessage=formatString()
        setMandatory(isMandatory)
    }

    fun setOnSpinnerItemSelected(listener: SpinnerSelectionListener): FormInputMaterialSpinner {
        mListener=listener
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputMaterialSpinner {
        isMandatory=mandatory
        if(!mandatory){ inputError=false }
        val stringMandatory= if(isMandatory) "*" else ""
        mTextInputLayout?.helperText = context.getString(
            R.string.label,
            tempTextHelper,
            stringMandatory
        ).toHtml()
        mTextInputLayout?.setHelperTextColor(ContextCompat.getColorStateList(context, defaultTextHelperColor))
        mTextInputLayout?.isHelperTextEnabled=showLabel
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String){
        textColor(R.color.colorRed)
        verifyInputError(errorMessage)
    }

    fun setValue(value: String) {
        for (index in mArrayList.indices) {
            if (value == mArrayList[index]) {
                setText(value)
                performFiltering("", 0)
            }
        }
    }

    fun setLabel(text: String): MaterialAutoCompleteTextView{
        tempTextHelper=text
        return this
    }

    fun setShowLabel(show: Boolean): FormInputMaterialSpinner {
        showLabel=show
        return this
    }

    fun setViewHint(hint: String) :MaterialAutoCompleteTextView{
        mHint=hint
        return this
    }


    /**
     *  set up Spinner
     */
    fun setListAdapter(items: List<String>): FormInputMaterialSpinner{
        mArrayList=items
        val spinnerArrayAdapter = ArrayAdapter(context, R.layout.spinner_item_material, items)
        setAdapter(spinnerArrayAdapter)
        initClickListener()
        return this
    }


    /**
     * Get components
     */

    /**
     * return the selected item
     * if the selected item is equal to [mHint] return empty, else return the selected item
     */
    fun getValue(): String {
        return if(text.toString()==mHint) "" else text.toString()
    }

    /**
     * Errors
     */
    private fun validateSpinner(hint: String) {
        if (getValue()== hint && isMandatory) {
            verifyInputError(String.format(resources.getString(R.string.isRequired), tempTextHelper))
        } else {
            verifyInputError("")
        }
    }

    private fun verifyInputError(error: String) {
        if(error.isNotEmpty()){
            mTextInputLayout?.isHelperTextEnabled=showLabel
            mTextInputLayout?.helperText = error
            mTextInputLayout?.setHelperTextColor(ContextCompat.getColorStateList(context, R.color.colorRed))
            mTextInputLayout?.error=error
            mTextInputLayout?.errorIconDrawable=null
            inputError=true
            mErrorMessage=error
        }else{
            inputError=false
            mErrorMessage=""
            setMandatory(isMandatory)
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
     */
    fun noError(parentView: View? = null):Boolean{
        inputError.isTrue {
            verifyInputError(mErrorMessage)
            parentView.hideKeyboard()
            parentView?.scrollTo(0, this.top)
        }.isNotTrue {
            verifyInputError("")
        }
        return !inputError
    }


    private fun formatString(string: String = resources.getString(R.string.cantBeEmpty), label: String = tempTextHelper):String{
        return String.format(string, label)
    }


    /**
     * Invoke [mListener] only if the valid item selected
     */
    private fun initClickListener(){
        onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if(getValue().isNotEmpty()){
                mListener?.onSpinnerItemSelected(adapter.getItem(position).toString())
            }
            validateSpinner(mHint)
        }
    }

    /**
     * Initialize mTextInputLayout, get default label and text color
     */
    private fun initTextInputLayout(){
        mTextInputLayout?.let{
            tempTextHelper=it.helperText.toString()
            mHint=it.hint.toString()
        }
    }


    /**
     * On restore view, reset the filter; this will make all items to be visible.
     */
    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        state?.let { performFiltering("", 0) }
    }
}