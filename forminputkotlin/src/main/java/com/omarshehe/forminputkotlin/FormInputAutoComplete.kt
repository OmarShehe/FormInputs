package com.omarshehe.forminputkotlin

/**
 * Created by omars on 10/2/2019.
 * Author omars
 */
import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.SparseArray
import android.view.*
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.children
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.omarshehe.forminputkotlin.adapter.AutoCompleteAdapter
import com.omarshehe.forminputkotlin.utils.FormInputContract
import com.omarshehe.forminputkotlin.utils.FormInputPresenterImpl
import com.omarshehe.forminputkotlin.utils.SavedState
import com.omarshehe.forminputkotlin.utils.Utils
import kotlinx.android.synthetic.main.form_input_autocomplete.view.*
import java.util.*
import kotlin.properties.Delegates

class FormInputAutoComplete : RelativeLayout, FormInputContract.View, TextWatcher {
    private lateinit var mAdapterAutocomplete: AutoCompleteAdapter
    private lateinit var mPresenter: FormInputContract.Presenter

    private var mLabel: String = ""
    private var mHint: String = ""
    private var mValue : String = ""
    private var mHeight : Int = 100
    private var mErrorMessage :String = ""
    private var mBackground: Int =R.drawable.bg_txt_square
    private var inputError:Int = 1
    private var isMandatory: Boolean = false
    private var mInputType:Int = 1
    private var isShowValidIcon= true
    private var mArrayList :List<String> = emptyArray<String>().toList()
    private var attrs: AttributeSet? =null
    private var styleAttr: Int = 0

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
        mPresenter = FormInputPresenterImpl(this)
        /**
         * Get Attributes
         */
        if(context!=null){
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FormInputLayout,styleAttr,0)
            mLabel = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_customer_label))
            mHint = Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_customer_hint))
            mValue=Utils.checkTextNotNull(a.getString(R.styleable.FormInputLayout_customer_value))
            mHeight = a.getDimension(R.styleable.FormInputLayout_customer_height,resources.getDimension( R.dimen.input_box_height)).toInt()
            mBackground = a.getResourceId(R.styleable.FormInputLayout_customer_background, R.drawable.bg_txt_square)
            isMandatory = a.getBoolean(R.styleable.FormInputLayout_customer_isMandatory, false)
            isShowValidIcon  = a.getBoolean(R.styleable.FormInputLayout_customer_showValidIcon, true)
            mInputType = a.getInt(R.styleable.FormInputLayout_customer_inputType, 1)
            val list = a.getResourceId(R.styleable.FormInputLayout_customer_array, R.array.array)


            setIcons()
            mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
            setHint(mHint)
            setValue(mValue)
            height = mHeight
            setBackground(mBackground)
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
        if (new != old) changeIconState(iconDropDown!!, new)
    }

    /**
     * Set components
     */
    private fun setIcons(){
        imgNoError.setImageResource(R.drawable.check_green)
    }

    fun setLabel(text:String): FormInputAutoComplete{
        mLabel=Utils.setLabel(tvLabel,text,isMandatory)
        return this
    }

    fun setMandatory(mandatory: Boolean) : FormInputAutoComplete {
        isMandatory =mandatory
        mLabel=Utils.setLabel(tvLabel,mLabel,isMandatory)
        return this
    }

    fun setHint(hint: String) :FormInputAutoComplete {
        txtInputBox.hint = hint
        return this
    }

    fun setValue(value: String) :FormInputAutoComplete {
        mValue = value
        txtInputBox.setText(value)
        return this
    }

    fun setHeight(height: Int) : FormInputAutoComplete {
        val lp = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        txtInputBox.layoutParams=lp
        return this
    }

    fun setBackground(background: Int) : FormInputAutoComplete  {
        layInputBox.setBackgroundResource(background)
        return this
    }

    fun showValidIcon(showIcon: Boolean) : FormInputAutoComplete {
        isShowValidIcon=showIcon
        return this
    }
    private fun changeIconState(view: AppCompatImageView, state: Boolean) {
        val animFromDoneToClose: AnimatedVectorDrawableCompat? =
            AnimatedVectorDrawableCompat.create(context, R.drawable.arrow_down_to_up)
        val animFromCloseToDone: AnimatedVectorDrawableCompat? =
            AnimatedVectorDrawableCompat.create(context, R.drawable.arrow_downtoup)
        val animation = if (state) animFromCloseToDone else animFromDoneToClose
        if (animation == view.drawable) return
        view.setImageDrawable(animation)
        animation?.start()
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
        val errorResult=Utils.showInputError(tvError,imgNoError,isShowValidIcon, error, visible)
        mErrorMessage=errorResult[0].toString()
        inputError=errorResult[1].toString().toInt()
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
        mAdapterAutocomplete = AutoCompleteAdapter(context, R.id.tvView, items, object : AutoCompleteAdapter.ItemSelectedListener {
            override fun onItemSelected(item: String?) {
                mValue = item.toString()
                txtInputBox.setText(item)
                txtInputBox.setSelection(mValue.length)
                txtInputBox.dismissDropDown()
                verifyInputError("", View.GONE)
                arrowIconState=false
            }
        })
        txtInputBox.setAdapter(mAdapterAutocomplete)
        return this
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
        inputBoxOnTextChange(s.toString())
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
                verifyInputError(String.format(resources.getString(R.string.isRequired), mLabel), View.VISIBLE)
            }else{
                verifyInputError("", View.GONE)
            }
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
}
