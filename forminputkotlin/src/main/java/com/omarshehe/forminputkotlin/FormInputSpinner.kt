package com.omarshehe.forminputkotlin

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.Spinner
import androidx.core.view.children
import com.omarshehe.forminputkotlin.utils.SavedState
import com.omarshehe.forminputkotlin.utils.Utils
import com.omarshehe.forminputkotlin.utils.Utils.showInputError
import kotlinx.android.synthetic.main.form_input_spinner.view.*
import java.util.*

class FormInputSpinner : RelativeLayout {
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
            mLabel = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_label))
            mHint = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_hint))
            mValue = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_form_value))
            mHeight = a.getDimension(R.styleable.FormInputLayout_form_height, resources.getDimension(R.dimen.input_box_height)).toInt()
            isMandatory = a.getBoolean(R.styleable.FormInputLayout_form_isMandatory, false)
            mBackground = a.getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square)
            mInputType = a.getInt(R.styleable.FormInputLayout_form_inputType, 1)
            isShowValidIcon = a.getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true)

            val list = a.getResourceId(R.styleable.FormInputLayout_form_array, R.array.array)
            setIcons()
            mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
            height = mHeight
            setMandatory(isMandatory)
            setBackground(mBackground)
            imgNoError.visibility = GONE
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
        imgNoError.setImageResource(R.drawable.check_green)
    }
    fun setLabel(text:String): FormInputSpinner{
        mLabel=Utils.setLabel(tvLabel,text,isMandatory)
        return this
    }
    fun setMandatory(mandatory: Boolean) : FormInputSpinner {
        isMandatory =mandatory
        mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
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
        val lparams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )
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
        val spinnerArrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSpinner.adapter = spinnerArrayAdapter
        initClickListener()
        return this
    }

    fun setAdapter(items: ArrayList<String>, listener: SpinnerSelectionListener) :FormInputSpinner {
        mArrayList=items
        val spinnerArrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
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
        val errorResult=showInputError(tvError,imgNoError,isShowValidIcon, error, visible)
        mErrorMessage=errorResult[0].toString()
        inputError=errorResult[1].toString().toInt()
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
        spSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isMandatory && firstOpen!=0) {
                    mListener?.onSpinnerItemSelected(parent.selectedItem.toString())
                    validateSpinner(mHint)
                }
                firstOpen=1
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