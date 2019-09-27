package com.omarshehe.forminputkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import com.omarshehe.forminputkotlin.adapter.AutoCompleteAdapter
import com.omarshehe.forminputkotlin.utils.FormInputContract
import kotlinx.android.synthetic.main.form_input_row.view.*
import java.util.*

class FormInputLayout : RelativeLayout,TextWatcher{


    var TAG : String ="FormInputLayout"
    var mOnTouchListener : OnTouchListener? = null
    private var mAdapterAutocomplete: AutoCompleteAdapter? = null
    private val mPresenter: FormInputContract.Presenter? = null

    val TYPE_INPUTBOX = 1
    val TYPE_AUTO_COMPLETE = 2
    val TYPE_SPINNER = 3
    val TYPE_PASSWORD = 4
    val TYPE_BUTTON = 5

    val INPUTTYPE_TEXT = 1
    val INPUTTYPE_PHONE = 2
    val INPUTTYPE_NUMBER = 3
    val INPUTTYPE_EMAIL = 4



    private var mComponentType: Int = 0
    private var mInputType:Int = 0
    private var maxLength:Int = 0
    private var mHeight: Int = 0
    private var inputError:Int = 0
    private var isMandatory: Boolean = false
    var isTagPrimary: Boolean = false
    var isMultiline:Boolean = false
    var isShowStrength: Boolean =false
    private var mLabel: String = ""
    private var mValue:String = ""
    private var BgBackground: Int?=null
    var mArrayList: Int = 0

    private var autoCompleteTxt: AutoCompleteView? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        val mInflater = LayoutInflater.from(context)
        val view = mInflater.inflate(R.layout.form_input_row, this, true)

        tvLabel

        /*tvLabel = view.findViewById(R.id.tvLabel)
        tvMandatory = view.findViewById(R.id.tvMandatory)
        txtLengthDesc = view.findViewById(R.id.txtLengthDesc)
        tvError = view.findViewById(R.id.tvError)
        tvErrorPass = view.findViewById(R.id.tvErrorPass)


        PassProgressStrength = view.findViewById(R.id.progressBar)
        tvPassStrength = view.findViewById(R.id.tvPassStrength)

        iconCancel = view.findViewById(R.id.iconCancel)
        iconDropDown = view.findViewById(R.id.iconDropDown)

        txtInputBox = view.findViewById(R.id.txtInputBox)
        txtPassword = view.findViewById(R.id.txtPassword)

        layInputBox = view.findViewById(R.id.layInputBox)
        layPassStrength = view.findViewById(R.id.layPassStrength)
        layPassword = view.findViewById(R.id.layPassword)
        layAutoComplete = view.findViewById(R.id.layAutoComplete)
        laySpinner = view.findViewById(R.id.laySpinner)
        layButton = view.findViewById(R.id.layButton)
        layLabel = view.findViewById(R.id.layLabel)

        spSpinner = view.findViewById(R.id.spSpinner)
        autoCompleteTxt = view.findViewById(R.id.autoCompleteTxt)*/


        /**
         * Get Attributes
         */
        @SuppressLint("CustomViewStyleable", "Recycle")
        if(context!=null){
            val a = context.obtainStyledAttributes(attrs, R.styleable.FormInputLayout)
            mLabel = a.getString(R.styleable.FormInputLayout_customer_label) as String
            val hint = a.getString(R.styleable.FormInputLayout_customer_hint)
            val value = a.getString(R.styleable.FormInputLayout_customer_value)
            val maxLines = a.getInt(R.styleable.FormInputLayout_customer_maxLines, 5)

            maxLength = a.getInt(R.styleable.FormInputLayout_customer_maxLength, 300)
            mHeight = a.getInt(R.styleable.FormInputLayout_customer_height, 200)
            mComponentType = a.getInt(R.styleable.FormInputLayout_customer_component, 1)
            isMandatory = a.getBoolean(R.styleable.FormInputLayout_customer_isMandatory, false)
            isMultiline = a.getBoolean(R.styleable.FormInputLayout_customer_isMultiLine, false)
            mInputType = a.getInt(R.styleable.FormInputLayout_customer_inputType, 1)
            BgBackground = a.getResourceId(R.styleable.FormInputLayout_customer_background, R.drawable.bg_txt_square)
            isShowStrength = a.getBoolean(R.styleable.FormInputLayout_customer_showPassStrength, false)
            mArrayList = a.getResourceId(R.styleable.FormInputLayout_customer_array, R.array.array)


            /**
             * Set up the values
             */
            inputError = 0
            setLabel(mLabel)
            setMandatory(isMandatory)
            setHint(hint)
            setInputType(mInputType)
            setComponentType(mComponentType)
            setMaxLength(maxLength)
        }

    }



    private fun setLabel(label: String) {
        if (mLabel != "") {
            if (mLabel.isNotEmpty()) {
                layLabel.visibility = View.VISIBLE
            } else {
                setLabelVisibility(false)
            }
            tvLabel.text = label
        } else {
            layLabel.visibility = View.GONE
        }
    }

    private fun setLabelVisibility(shouldShow: Boolean) {
        tvLabel.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    private fun setHint(hint: String?) {
        if (mComponentType == TYPE_INPUTBOX)
            txtInputBox.hint = hint
        if (mComponentType == TYPE_AUTO_COMPLETE)
            autoCompleteTxt?.hint = hint
        if (mComponentType == TYPE_PASSWORD)
            txtPassword.hint = hint
    }

    private fun setMaxLength(getMaxLength: Int) {
        maxLength = getMaxLength
        if (mComponentType == TYPE_INPUTBOX) {
            val filterArray = arrayOfNulls<InputFilter>(1)
            filterArray[0] = InputFilter.LengthFilter(maxLength)
            txtInputBox.filters = filterArray
        }
        if (mComponentType == TYPE_AUTO_COMPLETE) {
            val filterArray = arrayOfNulls<InputFilter>(1)
            filterArray[0] = InputFilter.LengthFilter(maxLength)
            autoCompleteTxt?.filters = filterArray
        }
    }


    private fun setComponentType(componentType: Int) {
        mComponentType = componentType
        when (componentType) {

            TYPE_INPUTBOX -> {
                layLabel.visibility = View.VISIBLE
                layInputBox.visibility = View.VISIBLE
                txtInputBox.addTextChangedListener(this)
            }
            TYPE_PASSWORD -> {
                layLabel.visibility = View.VISIBLE
                layPassword.visibility = View.VISIBLE
                txtPassword.addTextChangedListener(this)
                ///PasswordStrength2(isShowStrength)
            }
            TYPE_AUTO_COMPLETE -> {
                layLabel.visibility = View.VISIBLE
                layAutoComplete.visibility = View.VISIBLE
                val autoCompleteArray = resources.getStringArray(mArrayList)
                val autoCompleteListArray = ArrayList(Arrays.asList(*autoCompleteArray))
                //setAutoCompleteList(autoCompleteListArray)
            }
            TYPE_SPINNER -> {
                layLabel.visibility = View.VISIBLE
                //laySpinner.visibility = View.VISIBLE
                val getArray = resources.getStringArray(mArrayList)
                val listArray = Arrays.asList(*getArray)
                //setSpinner(listArray)
            }
            TYPE_BUTTON -> {
                layLabel.visibility = View.GONE
                //layButton.visibility = View.VISIBLE
            }

            else -> layInputBox.visibility = View.VISIBLE
        }
    }

    private fun setInputType(inputType: Int) {
        mInputType = inputType
        if (mComponentType == TYPE_INPUTBOX) {
            when (mInputType) {
                INPUTTYPE_TEXT -> txtInputBox.inputType = InputType.TYPE_CLASS_TEXT
                INPUTTYPE_PHONE -> txtInputBox.inputType = InputType.TYPE_CLASS_PHONE
                INPUTTYPE_NUMBER -> txtInputBox.inputType = InputType.TYPE_CLASS_NUMBER
                INPUTTYPE_EMAIL -> txtInputBox.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
        }
    }

    private fun setMandatory(mandatory: Boolean) {
        isMandatory = mandatory
        tvMandatory.visibility = if (isMandatory) View.VISIBLE else View.GONE
    }



    /**
     * Listener on text change
     * */
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (mComponentType == TYPE_PASSWORD) {
            //updatePasswordStrengthView(s.toString())
        }
        if (mComponentType == TYPE_INPUTBOX) {
            //inputBoxOnTextChange(s.toString())
        }
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
    internal
    class SavedState : BaseSavedState {
        var childrenStates: SparseArray<Parcelable>? = null
        constructor(superState: Parcelable?) : super(superState)
        constructor(source: Parcel) : super(source) {
           childrenStates = source.readSparseArray(javaClass.classLoader) as SparseArray<Parcelable>?
        }
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeSparseArray(childrenStates as SparseArray<Any>)
        }
        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel) = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }
    
}

