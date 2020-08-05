package com.omarshehe.forminputkotlin

/**
 * Created by omars on 10/2/2019.
 * Author omars
 */
import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.*
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.omarshehe.forminputkotlin.adapter.AutoCompleteAdapter
import com.omarshehe.forminputkotlin.utils.FormInputContract
import com.omarshehe.forminputkotlin.utils.FormInputPresenterImpl
import com.omarshehe.forminputkotlin.utils.Utils
import com.omarshehe.forminputkotlin.utils.changeIconState
import kotlinx.android.synthetic.main.form_input_autocomplete.view.*
import java.util.*
import kotlin.properties.Delegates

class FormInputAutoComplete : BaseFormInput, TextWatcher {
    private lateinit var mAdapterAutocomplete: AutoCompleteAdapter
    private lateinit var mPresenter: FormInputContract.Presenter

    private var mTextColor=R.color.black
    private var mLabel: String = ""
    private var mHint: String = ""
    private var mValue : String = ""
    private var mErrorMessage :String = ""
    private var mBackground: Int =R.drawable.bg_txt_square
    private var inputError:Int = 1
    private var isMandatory: Boolean = false
    private var isShowValidIcon= true
    private var isFirstOpen: Boolean = true
    private var mArrayList :List<String> = emptyArray<String>().toList()
    private var isShowLabel:Boolean =true

    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0
    private var mListener : AutoCompleteAdapter.ItemSelectedListener? =null

    constructor(context: Context) : super(context){
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


    @SuppressLint("NewApi")
    private fun initView(){
        LayoutInflater.from(context).inflate(R.layout.form_input_autocomplete, this, true)
        mPresenter = FormInputPresenterImpl()
        /**
         * Get Attributes
         */
        if(context!=null){
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout,styleAttr,0)
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

            mErrorMessage= String.format(resources.getString(R.string.cantBeEmpty), mLabel)
            txtInputBox.addTextChangedListener(this)

            txtInputBox.setOnClickListener { txtInputBox.showDropDown() }
            disableAutoCompleteTextSelection()

            val autoCompleteArray = resources.getStringArray(list)
            val autoCompleteListArray = ArrayList(listOf(*autoCompleteArray))
            setAdapter(autoCompleteListArray)

            iconDropDown.setOnClickListener{ showDropDown();arrowIconState=true }
            txtInputBox.setOnDismissListener{ arrowIconState=false }

            a.recycle()
        }
    }
    private var arrowIconState: Boolean by Delegates.observable(false) { _, old, new ->
        if (new != old) iconDropDown.changeIconState(new)
    }

    /**
     * Set components
     */
    private fun setIcons(){
        validIcon.setImageResource(R.drawable.check_green)
    }

    fun setLabel(text:String): FormInputAutoComplete{
        mLabel=Utils.setLabel(tvLabel,text,isMandatory)
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputAutoComplete {
        isMandatory =mandatory
        if(!mandatory){ inputError=0 }
        mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
        return this
    }
    fun setLabelVisibility(show:Boolean): FormInputAutoComplete {
        isShowLabel=Utils.setViewVisibility(tvLabel,show)
        return this
    }


    fun setHint(hint: String) :FormInputAutoComplete {
        mHint=hint
        txtInputBox.hint = hint
        return this
    }

    fun setValue(value: String) :FormInputAutoComplete {
        mValue = value
        if(mArrayList.contains(mValue)){
            txtInputBox.setText(value)
            verifyInputError("", View.GONE)
        }else{
            if(isMandatory && !isFirstOpen){
                verifyInputError(String.format(resources.getString(R.string.isRequired), mLabel), View.VISIBLE)
            }
            isFirstOpen=false
        }
        return this
    }

    fun setHeight(height: Int) : FormInputAutoComplete {
        txtInputBox.height=height
        return this
    }

    fun setBackground(background: Int) : FormInputAutoComplete  {
        mBackground=background
        layInputBox.setBackgroundResource(background)
        return this
    }

    fun showValidIcon(showIcon: Boolean) : FormInputAutoComplete {
        isShowValidIcon=showIcon
        return this
    }


    fun setOnItemSelectedListener(listener: AutoCompleteAdapter.ItemSelectedListener):FormInputAutoComplete{
        mListener=listener
        return this
    }

    fun setTextColor(color:Int):FormInputAutoComplete{
        mTextColor=color
        txtInputBox.setTextColor(ContextCompat.getColor(context,mTextColor))
        return this
    }

    /**
     * For save Instance State of the view in programmatically access
     */
    fun setID(id:Int):FormInputAutoComplete{
        this.id=id
        return this
    }

    /**
     * Get components
     */
    fun getValue(): String {
        return txtInputBox.text.toString()
    }

    fun getInputBox() : EditText{
        return txtInputBox
    }


    /**
     * Errors
     */
    private fun verifyInputError(error: String, visible: Int){
        val errorResult=Utils.showInputError(tvError,validIcon,checkIfShouldShowValidIcon(), error, visible)
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
            verifyInputError(mErrorMessage, VISIBLE)
            if (parentView != null) {
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
     * Auto Complete
     *
     */
    fun setAdapter(items: ArrayList<String>): FormInputAutoComplete {
        mArrayList=items
        txtInputBox.setShowAlways(true)
        mAdapterAutocomplete = AutoCompleteAdapter(context, R.id.tvView, items, itemSelectListener)
        txtInputBox.setAdapter(mAdapterAutocomplete)

        //txtInputBox.setOnItemClickListener(object :On)
        return this
    }

    private val itemSelectListener=object :AutoCompleteAdapter.ItemSelectedListener {
        override fun onItemSelected(item: String) {
            mValue = item
            txtInputBox.setText(item)
            txtInputBox.setSelection(mValue.length)
            txtInputBox.dismissDropDown()
            verifyInputError("", View.GONE)
            arrowIconState=false
            mListener?.onItemSelected(item)
        }
    }


    fun disableSearch(): FormInputAutoComplete {
        mAdapterAutocomplete.disableFilter(true)
        txtInputBox.isLongClickable = false
        txtInputBox.setTextIsSelectable(false)
        txtInputBox.isFocusable = false
        txtInputBox.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(actionMode: ActionMode, item: MenuItem): Boolean {
                return false
            }

            override fun onDestroyActionMode(actionMode: ActionMode) {}
        }
        return this
    }

    private fun disableAutoCompleteTextSelection() {
        txtInputBox.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
                return false
            }
            override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(actionMode: ActionMode, item: MenuItem): Boolean {
                return false
            }

            override fun onDestroyActionMode(actionMode: ActionMode) {}
        }
        txtInputBox.isLongClickable = false
        txtInputBox.setTextIsSelectable(false)
    }

    fun showDropDown(): FormInputAutoComplete {
        txtInputBox.showDropDown()
        return this
    }


    /**
     * Listener on text change
     * */
    override fun afterTextChanged(s: Editable?) {
    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!isFirstOpen) {
            inputBoxOnTextChange(s.toString())
        }
        isFirstOpen=false
    }
    private fun inputBoxOnTextChange(value: String) {
        mValue=value
        arrowIconState = txtInputBox.isPopupShowing
        if (mValue.isEmpty()) {
            if (isMandatory) {
                verifyInputError(String.format(resources.getString(R.string.cantBeEmpty), mLabel), View.VISIBLE)
            } else {
                verifyInputError("", View.GONE)
            }
        } else {
            if (isMandatory) {
                if(mArrayList.contains(mValue)){
                    setTextColor(mTextColor)
                    verifyInputError("", View.GONE)
                }else{
                    txtInputBox.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
                    verifyInputError(String.format(resources.getString(R.string.isRequired), mLabel), View.VISIBLE)
                }
            }else{
                setTextColor(mTextColor)
                verifyInputError("", View.GONE)
            }
        }
    }

}
