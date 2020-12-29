package com.omarshehe.forminputkotlin

import android.content.Context
import android.text.InputType
import android.text.Spannable
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.omarshehe.forminputkotlin.interfaces.ViewOnClickListener
import com.omarshehe.forminputkotlin.utils.isNotTrue
import com.omarshehe.forminputkotlin.utils.isTrue
import com.omarshehe.forminputkotlin.utils.textColor
import com.omarshehe.forminputkotlin.utils.toHtml


class FormInputMaterialSpinner : MaterialAutoCompleteTextView {
    private var mArrayList :List<String> = emptyList()

    private var inputError:Boolean = true
    private var mErrorMessage :String = ""
    private var isMandatory: Boolean = true
    private var showLabel:Boolean =true

    private var mTextInputLayout: TextInputLayout? =null
    private var tempTextHelper:String =""
    private var defaultTextHelperColor:Int =R.color.colorGrey

    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0

    private var mListener : ViewOnClickListener? =null
    lateinit var spinnerArrayAdapter :ArrayAdapter<String>

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

        val list = a.getResourceId(R.styleable.FormInputLayout_form_array, R.array.array)
        setListAdapter(resources.getStringArray(list).toList())

        initTextInputLayout()
        a.recycle()
    }
    private fun disableEditable(){
        inputType = InputType.TYPE_NULL
    }


    /**
     * Initialize mTextInputLayout, get default label and text color
     */
    private fun initTextInputLayout(){
        mTextInputLayout?.let{
            tempTextHelper=it.helperText.toString()
            defaultTextHelperColor=it.helperTextCurrentTextColor
        }
    }


    private fun initClickListener(){
        this.movementMethod=object : MovementMethod {
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

        this.setOnClickListener{
            mListener?.onClick()
        }
    }


    /**
     * Set components
     */

    fun setOnViewClickListener(listener: ViewOnClickListener): FormInputMaterialSpinner {
        mListener=listener
        initClickListener()
        return this
    }


    fun setTextInputLayout(textInputLayout: TextInputLayout){
        initTextInputLayout()
        mTextInputLayout=textInputLayout
        tempTextHelper=mTextInputLayout?.helperText.toString()
        mErrorMessage=formatString()
        setMandatory(isMandatory)
    }

    fun setMandatory(mandatory: Boolean) : FormInputMaterialSpinner {
        isMandatory=mandatory
        if(!mandatory){ inputError=false }
        val stringMandatory= if(isMandatory) "*" else ""
        mTextInputLayout?.helperText = context.getString(R.string.label, tempTextHelper, stringMandatory).toHtml()
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

    fun setShowLabel(show: Boolean): FormInputMaterialSpinner {
        showLabel=show
        return this
    }

    /**
     *  set up Spinner
     */
    fun setListAdapter(items: List<String>): FormInputMaterialSpinner{
        mArrayList=items
        spinnerArrayAdapter = ArrayAdapter(context, R.layout.spinner_item_material, items)
        setAdapter(spinnerArrayAdapter)
        return this
    }




    /**
     * Get components
     */
    fun getValue(): String {
        return text.toString()
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
            parentView?.scrollTo(0, this.top)
            requestFocus()
        }.isNotTrue {
            verifyInputError("")
        }
        return !inputError
    }


    private fun formatString(string: String = resources.getString(R.string.cantBeEmpty), label: String = tempTextHelper):String{
        return String.format(string, label)
    }
}