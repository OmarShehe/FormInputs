package com.omarshehe.forminputkotlin

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.omarshehe.forminputkotlin.interfaces.SpinnerSelectionListener
import com.omarshehe.forminputkotlin.utils.textColor
import com.omarshehe.forminputkotlin.utils.visibleIf
import kotlinx.android.synthetic.main.form_input_spinner.view.*
import java.util.*

class FormInputSpinner : BaseFormInput {
    private var inputError:Boolean = true
    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mHint: String = ""
    private var mErrorMessage :String = ""
    private var isMandatory: Boolean = false
    private var mArrayList :List<String> = emptyArray<String>().toList()
    private var isFirstOpen: Boolean = true
    private var mListener : SpinnerSelectionListener? =null
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

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.form_input_spinner, this, true)
        /**
         * Get Attributes
         */
        if (context != null) {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout, 0, 0)
            setTextColor( a.getResourceId(R.styleable.FormInputLayout_form_textColor,R.color.black))
            setMandatory( a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, true))
            setLabel(a.getString(R.styleable.FormInputLayout_form_label).orEmpty())
            setHint(a.getString(R.styleable.FormInputLayout_form_hint).orEmpty())
            setValue(a.getString(R.styleable.FormInputLayout_form_value).orEmpty())
            setHeight(a.getDimension(R.styleable.FormInputLayout_form_height,resources.getDimension( R.dimen.formInputInput_box_height)).toInt())
            setBackground(a.getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square))

            showValidIcon(a.getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true))
            setLabelVisibility(a.getBoolean(R.styleable.FormInputLayout_form_showLabel, true))


            val list = a.getResourceId(R.styleable.FormInputLayout_form_array, R.array.array)


            validIcon.visibility = GONE
            mErrorMessage = String.format(resources.getString(R.string.isRequired), mLabel)
            val getIntArray = resources.getStringArray(list)
            setAdapter(listOf(*getIntArray))
            a.recycle()
        }
    }

    /**
     * Set components
     */
    fun setLabel(text:String): FormInputSpinner{
        mLabel=tvLabel.setLabel(text,isMandatory)
        return this
    }

    /**
     * set red star in the label for mandatory view.
     * if view not mandatory set [inputError] false
     */
    fun setMandatory(mandatory: Boolean) : FormInputSpinner {
        isMandatory =mandatory
        if(!mandatory){ inputError=false }
        mLabel=tvLabel.setLabel(mLabel,isMandatory)
        return this
    }

    fun setLabelVisibility(show:Boolean): FormInputSpinner {
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

    fun setHeight(height: Int) : FormInputSpinner {
        val lparams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        spSpinner.layoutParams=lparams
        return this
    }

    fun setBackground(background: Int): FormInputSpinner {
        layInputBox.setBackgroundResource(background)
        return this
    }
    fun showValidIcon(showIcon: Boolean) : FormInputSpinner {
        isShowValidIcon=showIcon
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String){
        verifyInputError(errorMessage, VISIBLE)
    }

    fun setTextColor(color:Int):FormInputSpinner{
        mTextColor=color
        (spSpinner.selectedView as TextView?)?.textColor(mTextColor)
        return this
    }

    /**
     * For save Instance State of the view in programmatically access
     */
    fun setID(id:Int):FormInputSpinner{
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


    private fun validateSpinner(hint: String) {
        if (getValue()== hint) {
            verifyInputError(String.format(resources.getString(R.string.isRequired), mLabel), View.VISIBLE)
        } else {
            verifyInputError("", View.GONE)
        }
    }

    /**
     * Errors
     */
    private fun verifyInputError(stringError: String, visible: Int){
        mErrorMessage=stringError
        inputError=tvError.showInputError(validIcon,isShowValidIcon, stringError, visible)
        (spSpinner.selectedView as TextView?)?.textColor(if(visible== View.VISIBLE)R.color.colorRed else mTextColor)
    }


    fun isError(parentView: View?): Boolean {
        return if (inputError) {
            verifyInputError(mErrorMessage, View.VISIBLE)
            parentView?.scrollTo(0, spSpinner.top)
            spSpinner.requestFocus()
            true
        } else {
            verifyInputError("", View.GONE)
            false
        }
    }


    fun setOnSpinnerItemSelected(listener: SpinnerSelectionListener):FormInputSpinner{
        mListener=listener
        initClickListener()
        return this
    }

    private fun initClickListener(){
        spSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                when{
                    !isFirstOpen-> mListener?.onSpinnerItemSelected(parent.selectedItem.toString())
                  isMandatory && !isFirstOpen-> validateSpinner(mHint)
                    isFirstOpen-> (view as TextView?)?.textColor(mTextColor)
                        
                }

                isFirstOpen=false

            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}