package com.omarshehe.forminputkotlin

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.omarshehe.forminputkotlin.utils.SavedState
import com.omarshehe.forminputkotlin.utils.Utils
import com.omarshehe.forminputkotlin.utils.Utils.showInputError
import kotlinx.android.synthetic.main.form_input_spinner.view.*
import java.util.*

class FormInputSpinner : RelativeLayout {
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
    private var mArrayList :List<String> = emptyArray<String>().toList()
    private var isShowValidIcon= true
    private var firstOpen: Int = 0
    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0
    private var mListener : SpinnerSelectionListener? =null
    private var isShowLabel:Boolean =true

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
            setIcons()

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
    private fun setIcons(){
        validIcon.setImageResource(R.drawable.check_green)
    }
    fun setLabel(text:String): FormInputSpinner{
        mLabel=Utils.setLabel(tvLabel,text,isMandatory)
        return this
    }
    fun setMandatory(mandatory: Boolean) : FormInputSpinner {
        isMandatory =mandatory
        if(!mandatory){ inputError=0 }
        mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
        return this
    }

    fun setLabelVisibility(show:Boolean): FormInputSpinner {
        isShowLabel=Utils.setViewVisibility(tvLabel,show)
        return this
    }

    fun setHint(hint: String) :FormInputSpinner{
        mHint=hint
        return this
    }


    fun setValue(value: String) {
        mValue = value
        for (index in mArrayList.indices) {
            if (mValue == mArrayList[index]) {
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

    fun setTextColor(color:Int):FormInputSpinner{
        mTextColor=color
      //  txtInputBox.setTextColor(ContextCompat.getColor(context,mTextColor))
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
    private fun verifyInputError(error: String, visible: Int){
        val errorResult=showInputError(tvError,validIcon,checkIfShouldShowValidIcon(), error, visible)
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
            verifyInputError(mErrorMessage, View.VISIBLE)
            if (parentView != null) {
                parentView.scrollTo(0, tvError.top)
                spSpinner.requestFocus()
            }
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
                if (isMandatory && firstOpen!=0) {
                    mListener?.onSpinnerItemSelected(parent.selectedItem.toString())
                    validateSpinner(mHint)
                }
                firstOpen=1
                //Set text color to the selected item
                (view as TextView?)?.setTextColor(ContextCompat.getColor(context,mTextColor))
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
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