package com.omarshehe.forminputkotlin

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.ColorRes
import androidx.core.content.withStyledAttributes
import com.omarshehe.forminputkotlin.databinding.FormInputSpinnerBinding
import com.omarshehe.forminputkotlin.interfaces.ItemSelectedListener
import com.omarshehe.forminputkotlin.utils.*

class FormInputSpinner : BaseFormInput {
    private lateinit var binding:FormInputSpinnerBinding
    private var inputError:Boolean = true
    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mHint: String = ""
    private var mErrorMessage :String = ""
    private var isMandatory: Boolean = true
    private var mArrayList :List<String> = emptyArray<String>().toList()
    private var isFirstOpen: Boolean = true
    private var mListener : ItemSelectedListener? =null
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
        binding=FormInputSpinnerBinding.inflate(LayoutInflater.from(context),this)
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
        mLabel=binding.tvLabel.setLabel(text, isMandatory)
        return this
    }

    /**
     * set red star in the label for mandatory view.
     * if view not mandatory set [inputError] false
     */
    fun setMandatory(mandatory: Boolean) : FormInputSpinner {
        isMandatory =mandatory
        if(!mandatory){ inputError=false }
        mLabel=binding.tvLabel.setLabel(mLabel, isMandatory)
        return this
    }

    fun setLabelVisibility(show: Boolean): FormInputSpinner {
        binding.tvLabel.visibleIf(show)
        return this
    }

    fun setHint(hint: String) :FormInputSpinner{
        mHint=hint
        return this
    }


    fun setValue(value: String) {
        for (index in mArrayList.indices) {
            if (value == mArrayList[index]) {
                binding.spSpinner.setSelection(index)
                validateSpinner(mHint)
            }
        }
    }

    fun setSpinnerHeight(height: Int) : FormInputSpinner {
        binding.spSpinner.layoutParams=FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, height)
        return this
    }

    fun setBackground(background: Int): FormInputSpinner {
        binding.layInputBox.setBackgroundResource(background)
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
        (binding.spSpinner.selectedView as TextView?)?.textColor(mTextColor)
        return this
    }

    fun setLabelTextColor(@ColorRes color: Int):FormInputSpinner{
        binding.tvLabel.textColor(color)
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
        return binding.spSpinner.selectedItem.toString()
    }

    fun getSpinner(): Spinner {
        return binding.spSpinner
    }



    /**
     *  set up Spinner
     */
    fun setAdapter(items: List<String>):FormInputSpinner {
        mArrayList=items
        val spinnerArrayAdapter = ArrayAdapter(context, R.layout.spinner_item, items)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spSpinner.adapter = spinnerArrayAdapter
        initClickListener()
        return this
    }

    /**
     * pass [adapter] and [items]
     * [items] will be used when [setValue] is called
     */
    fun setAdapter(adapter: BaseAdapter,items: List<String>):FormInputSpinner {
        mArrayList=items
        binding.spSpinner.adapter = adapter
        return this
    }

    fun setOnSpinnerItemSelected(listener: ItemSelectedListener):FormInputSpinner{
        mListener=listener
        return this
    }

    private fun initClickListener(){
        binding.spSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when(isFirstOpen){
                    true -> (view as TextView?)?.textColor(mTextColor)
                    false -> {
                        validateSpinner(mHint)
                        mListener?.onItemSelected(parent.selectedItem.toString())
                    }
                }
                isFirstOpen=false
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
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
        inputError=binding.tvError.showInputError(binding.validIcon, showValidIcon, stringError, visible)
        (binding.spSpinner.selectedView as TextView?)?.textColor(if (visible == View.VISIBLE) R.color.colorOnError else mTextColor)
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
    fun noError(parentView: View? = null, showError:Boolean=true):Boolean{
        inputError.isTrue {
            showError.isTrue {
                verifyInputError(mErrorMessage, View.VISIBLE)
                parentView.hideKeyboard()
                parentView?.scrollTo(0, binding.spSpinner.top)
                binding.spSpinner.requestFocus()
            }
        }.isNotTrue {
            verifyInputError("", View.GONE)
        }
        return !inputError
    }
}