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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.omarshehe.forminputkotlin.adapter.AutoCompleteAdapter
import com.omarshehe.forminputkotlin.databinding.FormInputAutocompleteBinding
import com.omarshehe.forminputkotlin.interfaces.ItemSelectedListener
import com.omarshehe.forminputkotlin.interfaces.OnTextChangeListener
import com.omarshehe.forminputkotlin.utils.*
import kotlin.properties.Delegates

class FormInputAutoComplete: BaseFormInput, TextWatcher {

    private lateinit var mAdapterAutocomplete: AutoCompleteAdapter
    private lateinit var mPresenter: FormInputContract.Presenter
    private lateinit var binding: FormInputAutocompleteBinding

    private var inputError: Boolean = true
    private var mTextColor = R.color.black
    private var mLabel: String = ""
    private var mErrorMessage: String = ""
    private var isMandatory: Boolean = true
    private var showValidIcon = true
    private var isFirstOpen: Boolean = true
    private var mArrayList: List<String> = emptyArray<String>().toList()

    private var attrs: AttributeSet? = null
    private var styleAttr: Int = 0

    private var mListener: ItemSelectedListener? = null
    private var mTextChangeListener: OnTextChangeListener? = null

    constructor(context: Context): super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        this.attrs = attrs
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.attrs = attrs
        styleAttr = defStyleAttr
        initView()
    }


    private fun initView() {
        binding = FormInputAutocompleteBinding.inflate(LayoutInflater.from(context), this)
        mPresenter = FormInputPresenterImpl()
        orientation = VERTICAL
        context.withStyledAttributes(attrs, R.styleable.FormInputLayout, styleAttr, 0) {
            setTextColor(getResourceId(R.styleable.FormInputLayout_form_textColor, R.color.black))
            setHintTextColor(getResourceId(R.styleable.FormInputLayout_form_textColorHint, R.color.hint_text_color))
            setLabelTextColor(getResourceId(R.styleable.FormInputLayout_form_textColorLabel, R.color.black))
            setMandatory(getBoolean(R.styleable.FormInputLayout_form_isMandatory, true))
            setLabel(getString(R.styleable.FormInputLayout_form_label).orEmpty())
            setHint(getString(R.styleable.FormInputLayout_form_hint).orEmpty())
            setValue(getString(R.styleable.FormInputLayout_form_value).orEmpty())
            setInputViewHeight(getDimension(R.styleable.FormInputLayout_form_height, resources.getDimension(R.dimen.formInputInput_box_height)).toInt())
            setBackground(getResourceId(R.styleable.FormInputLayout_form_background, R.drawable.bg_txt_square))
            showValidIcon(getBoolean(R.styleable.FormInputLayout_form_showValidIcon, true))
            setLabelVisibility(getBoolean(R.styleable.FormInputLayout_form_showLabel, true))

            mErrorMessage = String.format(resources.getString(R.string.cantBeEmpty), mLabel)
            val itemList = resources.getStringArray(getResourceId(R.styleable.FormInputLayout_form_array, R.array.array)).toList()
            setAdapter(itemList)
        }

        disableAutoCompleteTextSelection()
        binding.run {
            txtInputBox.setOnClickListener {showDropDown();arrowIconState = true}
            txtInputBox.addTextChangedListener(this@FormInputAutoComplete)
            iconDropDown.setOnClickListener {showDropDown();arrowIconState = true}
            txtInputBox.setOnDismissListener {arrowIconState = false}
        }
    }

    private var arrowIconState: Boolean by Delegates.observable(false) {_, old, new ->
        if (new != old) binding.iconDropDown.changeIconState(new)
    }

    /**
     * Set components
     */

    fun setLabel(text: String): FormInputAutoComplete {
        mLabel = binding.tvLabel.setLabel(text, isMandatory)
        return this
    }

    fun setMandatory(mandatory: Boolean): FormInputAutoComplete {
        isMandatory = mandatory
        mandatory.isNotTrue {inputError = false}
        mLabel = binding.tvLabel.setLabel(mLabel, isMandatory)
        return this
    }

    fun setLabelVisibility(show: Boolean): FormInputAutoComplete {
        binding.tvLabel.visibleIf(show)
        return this
    }

    fun setHint(hint: String): FormInputAutoComplete {
        binding.txtInputBox.hint = hint
        return this
    }

    fun setValue(value: String): FormInputAutoComplete {
        with(binding) {
            if (mArrayList.contains(value)) {
                txtInputBox.setText(value)
                verifyInputError("", GONE)
            } else {
                txtInputBox.setText("")
                if (isMandatory && !isFirstOpen) {
                    verifyInputError(String.format(resources.getString(R.string.isRequired), mLabel), VISIBLE)
                }
                isFirstOpen = false
            }
        }

        return this
    }

    fun setInputViewHeight(height: Int): FormInputAutoComplete {
        binding.txtInputBox.height = height
        return this
    }

    fun setBackground(background: Int): FormInputAutoComplete {
        binding.layInputBox.setBackgroundResource(background)
        return this
    }

    /**
     * Set custom error
     */
    fun setError(errorMessage: String) {
        binding.txtInputBox.textColor(R.color.colorOnError)
        verifyInputError(errorMessage, VISIBLE)
    }

    fun showValidIcon(showIcon: Boolean): FormInputAutoComplete {
        showValidIcon = showIcon
        return this
    }

    fun setOnItemSelectedListener(listener: ItemSelectedListener): FormInputAutoComplete {
        mListener = listener
        return this
    }

    fun setOnTextChangeListener(listener: OnTextChangeListener): FormInputAutoComplete {
        mTextChangeListener = listener
        return this
    }

    fun setHintTextColor(@ColorRes color: Int): FormInputAutoComplete {
        binding.txtInputBox.hintTextColor(color)
        return this
    }

    fun setLabelTextColor(@ColorRes color: Int): FormInputAutoComplete {
        binding.tvLabel.textColor(color)
        return this
    }

    fun setTextColor(color: Int): FormInputAutoComplete {
        mTextColor = color
        binding.txtInputBox.setTextColor(ContextCompat.getColor(context, mTextColor))
        return this
    }

    /**
     * For save Instance State of the view in programmatically access
     */
    fun setID(id: Int): FormInputAutoComplete {
        this.id = id
        return this
    }

    /**
     * Get components
     */
    fun getValue(): String {
        return binding.txtInputBox.text.toString()
    }

    fun getInputBox(): EditText {
        return binding.txtInputBox
    }

    /**
     * Errors
     */
    private fun verifyInputError(stringError: String, visible: Int) = with(binding) {
        mErrorMessage = stringError
        inputError = tvError.showInputError(validIcon, shouldShowValidIcon(), stringError, visible)
    }

    private fun shouldShowValidIcon(): Boolean {
        return if (getValue().isBlank()) false else showValidIcon
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
    fun noError(parentView: View? = null, showError: Boolean = true): Boolean {
        inputError.isTrue {
            showError.isTrue {
                verifyInputError(mErrorMessage, VISIBLE)
                parentView.hideKeyboard()
                parentView?.scrollTo(0, binding.tvError.top)
                binding.txtInputBox.requestFocus()
            }
        }.isNotTrue {
            verifyInputError("", GONE)
        }
        return !inputError
    }


    /**
     * Auto Complete
     *
     */
    fun setAdapter(items: List<String>): FormInputAutoComplete {
        mArrayList = items
        binding.run {
            txtInputBox.setShowAlways(true)
            mAdapterAutocomplete = AutoCompleteAdapter(context, R.id.tvView, items, itemSelectListener)
            txtInputBox.setAdapter(mAdapterAutocomplete)
        }
        return this
    }

    /**
     * pass [adapter] and [items]
     * [items] will be used when [setValue] is called
     */
    fun setAdapter(adapter: ArrayAdapter<Any>, items: List<String>): FormInputAutoComplete {
        mArrayList = items
        binding.run {
            txtInputBox.setShowAlways(true)
            txtInputBox.setAdapter(adapter)
        }
        initClickListener()
        return this
    }

    private fun initClickListener() {
        binding.txtInputBox.onItemClickListener = AdapterView.OnItemClickListener {parent, _, position, _ ->
            mListener?.onItemSelected(parent.getItemAtPosition(position).toString())
        }
    }

    private val itemSelectListener = object: ItemSelectedListener {
        override fun onItemSelected(item: String) {
            binding.run {
                txtInputBox.setText(item)
                txtInputBox.setSelection(item.length)
                txtInputBox.dismissDropDown()
            }
            verifyInputError("", GONE)
            arrowIconState = false
            mListener?.onItemSelected(item)
        }
    }

    fun disableSearch(): FormInputAutoComplete = with(binding) {
        mAdapterAutocomplete.disableFilter(true)
        txtInputBox.isLongClickable = false
        txtInputBox.setTextIsSelectable(false)
        txtInputBox.isFocusable = false
        txtInputBox.customSelectionActionModeCallback = object: ActionMode.Callback {
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
        return this@FormInputAutoComplete
    }

    private fun disableAutoCompleteTextSelection() = with(binding) {
        txtInputBox.customSelectionActionModeCallback = object: ActionMode.Callback {
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
        binding.txtInputBox.showDropDown()
        return this
    }

    /**
     * Listener on text change
     * */
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(value: CharSequence?, start: Int, before: Int, count: Int) {
        isFirstOpen.isNotTrue {performOnTextChange(value.toString())}
        isFirstOpen = false
    }

    private fun performOnTextChange(value: String) = with(binding) {
        arrowIconState = txtInputBox.isPopupShowing
        if (value.isEmpty()) {
            if (isMandatory) {
                verifyInputError(resources.getString(R.string.cantBeEmpty, mLabel), VISIBLE)
            } else {
                verifyInputError("", GONE)
            }
        } else {
            if (isMandatory) {
                if (mArrayList.contains(value)) {
                    setTextColor(mTextColor)
                    verifyInputError("", GONE)
                } else {
                    txtInputBox.setTextColor(ContextCompat.getColor(context, R.color.colorOnError))
                    verifyInputError(resources.getString(R.string.isRequired, mLabel), VISIBLE)
                }
            } else {
                setTextColor(mTextColor)
                verifyInputError("", GONE)
            }
        }
        mTextChangeListener?.onTextChange(value)
    }
}