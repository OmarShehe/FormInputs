package com.omarshehe.forminputkotlin

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.annotation.ColorRes
import androidx.core.content.withStyledAttributes
import com.omarshehe.forminputkotlin.interfaces.SpinnerSelectionListener
import com.omarshehe.forminputkotlin.utils.*
import kotlinx.android.synthetic.main.form_input_spinner.view.*
import java.util.*

class FormInputSpinner : BaseFormInput {
    private var inputError:Boolean = true
    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mHint: String = ""
    private var mErrorMessage :String = ""
    private var isMandatory: Boolean = true
    private var mArrayList :List<String> = emptyArray<String>().toList()
    private var isFirstOpen: Boolean = true
    private var mListener : SpinnerSelectionListener? =null
    private var showValidIcon= true

    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0

    constructor(activity: Activity) : super(activity){
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
        inflate(context, R.layout.form_input_spinner, this)
        orientation= VERTICAL
        context.withStyledAttributes(attrs, R.styleable.FormInputLayout, styleAttr, 0) {
            setTextColor(getResourceId(R.styleable.FormInputLayout_form_textColor, R.color.black))
            setLabelTextColor(getResourceId(R.styleable.FormInputLayout_form_textColorLabel, R.color.black))
            setMandatory(getBoolean(R.styleable.FormInputLayout_form_isMandatory, true))
            setLabel(getString(R.styleable.FormInputLayout_form_label).orEmpty())
            setHint(getString(R.styleable.FormInputLayout_form_hint).orEmpty())
            setValue(getString(R.styleable.FormInputLayout_form_value).orEmpty())
            setSpinnerHeight(getDimension(R.styleable.FormInputLayout_form_height, resources.getDimension(R.dimen.formInputInput_box_height)).toInt())
            setBackground(getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square))

            showValidIcon(getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true))
            setLabelVisibility(getBoolean(R.styleable.FormInputLayout_form_showLabel, true))

            mErrorMessage = String.format(resources.getString(R.string.isRequired), mLabel)

            val itemList =resources.getStringArray(getResourceId(R.styleable.FormInputLayout_form_array, R.array.array)).toList()
            setAdapter(itemList)
        }

    }

    /**
     * Set components
     */
    fun setLabel(text: String): FormInputSpinner{
        mLabel=tvLabel.setLabel(text, isMandatory)
        return this
    }

    /**
     * set red star in the label for mandatory view.
     * if view not mandatory set [inputError] false
     */
    fun setMandatory(mandatory: Boolean) : FormInputSpinner {
        isMandatory =mandatory
        if(!mandatory){ inputError=false }
        mLabel=tvLabel.setLabel(mLabel, isMandatory)
        return this
    }

    fun setLabelVisibility(show: Boolean): FormInputSpinner {
        tvLabel.visibleIf(show)
        return this
    }

    fun setHint(hint: String) :FormInputSpinner{
        mHint=hint
        return this
    }


    fun setValue(value: String) {
        for (index in mArrayList.indices) {
            if (value == mArrayList[index]) {
                spSpinner.setSelection(index)
                validateSpinner(mHint)
            }
        }
    }

    fun setSpinnerHeight(height: Int) : FormInputSpinner {
        spSpinner.layoutParams=FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, height)
        return this
    }

    fun setBackground(background: Int): FormInputSpinner {
        layInputBox.setBackgroundResource(background)
        return this
    }
    fun showValidIcon(showIcon: Boolean) : FormInputSpinner {
        showValidIcon=showIcon
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String){
        verifyInputError(errorMessage, VISIBLE)
    }

    fun setTextColor(color: Int):FormInputSpinner{
        mTextColor=color
        (spSpinner.selectedView as TextView?)?.textColor(mTextColor)
        return this
    }

    fun setLabelTextColor(@ColorRes color: Int):FormInputSpinner{
        tvLabel.textColor(color)
        return this
    }

    /**
     * For save Instance State of the view in programmatically access
     */
    fun setID(id: Int):FormInputSpinner{
        this.id=id
        return this
    }


    /**
     * Get components
     */
    fun getValue(): String {
        return spSpinner.selectedItem.toString()
    }

    fun getSpinner(): Spinner {
        return spSpinner
    }



    /**
     *  set up Spinner
     */
    fun setAdapter(items: List<String>):FormInputSpinner {
        mArrayList=items
        val spinnerArrayAdapter = ArrayAdapter(context, R.layout.spinner_item, items)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSpinner.adapter = spinnerArrayAdapter
        initClickListener()
        return this
    }

    fun setAdapter(items: ArrayList<String>, listener: SpinnerSelectionListener) :FormInputSpinner {
        mArrayList=items
        val spinnerArrayAdapter = ArrayAdapter(context, R.layout.spinner_item, items)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        initClickListener()
        spSpinner.adapter = spinnerArrayAdapter
        return this
    }

    //////////////////////////////////////////
    fun setAdapter(adapter: BaseAdapter):FormInputSpinner {
        spSpinner.adapter = adapter
        return this
    }


    /**
     * Errors
     */
    private fun validateSpinner(hint: String) {
        if (getValue()== hint && isMandatory) {
            verifyInputError(String.format(resources.getString(R.string.isRequired), mLabel), View.VISIBLE)
        } else {
            verifyInputError("", View.GONE)
        }
    }

    private fun verifyInputError(stringError: String, visible: Int){
        mErrorMessage=stringError
        inputError=tvError.showInputError(validIcon, showValidIcon, stringError, visible)
        (spSpinner.selectedView as TextView?)?.textColor(if (visible == View.VISIBLE) R.color.colorOnError else mTextColor)
    }


    fun noError(parentView: View? = null, focus : Boolean = true):Boolean{
        inputError.isTrue {
            verifyInputError(mErrorMessage, View.VISIBLE)
            parentView.hideKeyboard()
            parentView?.scrollTo(0, spSpinner.top)
            focus.isTrue {spSpinner.requestFocus()}
        }.isNotTrue {
            verifyInputError("", View.GONE)
        }
        return !inputError
    }


    fun setOnSpinnerItemSelected(listener: SpinnerSelectionListener):FormInputSpinner{
        mListener=listener
        initClickListener()
        return this
    }

    private fun initClickListener(){
        spSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when(isFirstOpen){
                    true -> (view as TextView?)?.textColor(mTextColor)
                    false -> {
                        mListener?.onSpinnerItemSelected(parent.selectedItem.toString())
                        validateSpinner(mHint)
                    }
                }
                isFirstOpen=false
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}