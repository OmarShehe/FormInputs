package com.omarshehe.forminputjava;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.omarshehe.forminputjava.Utils.PasswordStrength;
import com.omarshehe.forminputjava.adapter.AutoCompleteAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormInputLayout extends ConstraintLayout implements FormInputLayoutContract.View,TextWatcher{

    private static final String TAG = FormInputLayout.class.getName();
    private OnTouchListener mOnTouchListener;
    private AutoCompleteAdapter mAdapterAutocomplete;
    private FormInputLayoutContract.Presenter mPresenter;


    public static final int TYPE_INPUTBOX = 1;
    public static final int TYPE_AUTO_COMPLETE = 2;
    public static final int TYPE_SPINNER = 3;
    public static final int TYPE_PASSWORD = 4;
    public static final int TYPE_BUTTON = 5;


    public static final int INPUTTYPE_TEXT = 1;
    public static final int INPUTTYPE_PHONE = 2;
    public static final int INPUTTYPE_NUMBER = 3;
    public static final int INPUTTYPE_EMAIL = 4;


    private TextView tvLabel, tvMandatory, txtLengthDesc,tvPassStrength, tvError;
    private EditText txtInputBox,txtPassword;
    private View layInputBox,layNestedInputBox, layAutoComplete,laySpinner,layButton,layPassStrength,layPassword, layLabel;
    private Spinner spSpinner;
    private ImageView iconCancel, iconDropDown;
    private ProgressBar PassProgressStrength,btnProgressBar;

    private int mComponentType, mInputType, maxLength,height;
    private boolean isMandatory, isTagPrimary, isMultiline, isShowStrength;
    private String mLabel,mErrorMessage = "", mValue,mHint;
    private int BgBackground,arrayList;
    private Button btnNoImage;

    private int  inputError;

    private AutoCompleteView autoCompleteTxt;


    public FormInputLayout(Context context) {
        super(context);
    }

    public FormInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSaveFromParentEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.form_input_row, this, true);

        mPresenter = new FormInputLayoutPresenterImpl(this);

        tvLabel =  view.findViewById(R.id.tvLabel);
        tvMandatory = view.findViewById(R.id.tvMandatory);
        txtLengthDesc =  view.findViewById(R.id.txtLengthDesc);
        tvError= view.findViewById(R.id.tvError);

        PassProgressStrength=  findViewById(R.id.progressBar);
        tvPassStrength = findViewById(R.id.tvPassStrength);

        iconCancel = view.findViewById(R.id.iconCancel);
        iconDropDown =  view.findViewById(R.id.iconDropDown);

        txtInputBox= view.findViewById(R.id.txtInputBox);
        txtPassword= view.findViewById(R.id.txtPassword);

        layInputBox = view.findViewById(R.id.layInputBox);
        layNestedInputBox= view.findViewById(R.id.layNestedInputBox);
        layPassStrength=view.findViewById(R.id.layPassStrength);
        layPassword=view.findViewById(R.id.layPassword);
        layAutoComplete =  view.findViewById(R.id.layAutoComplete);
        laySpinner=  view.findViewById(R.id.laySpinner);
        layButton=view.findViewById(R.id.layButton);
        layLabel =  view.findViewById(R.id.layLabel);

        spSpinner= view.findViewById(R.id.spSpinner);


        btnNoImage=view.findViewById(R.id.btnNoImage);
        btnProgressBar=view.findViewById(R.id.btnProgressBar);


        autoCompleteTxt =  view.findViewById(R.id.autoCompleteView);



        /**
         * Get Attributes
         */
        @SuppressLint({"CustomViewStyleable", "Recycle"})
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FormInputLayout);
        mLabel = a.getString(R.styleable.FormInputLayout_customer_label);
        mHint = a.getString(R.styleable.FormInputLayout_customer_hint);
        mValue = a.getString(R.styleable.FormInputLayout_customer_value);
        final int maxLines = a.getInt(R.styleable.FormInputLayout_customer_maxLines, 5);
        maxLength = a.getInt(R.styleable.FormInputLayout_customer_maxLength, 300);
        height=a.getInt(R.styleable.FormInputLayout_customer_height, 200);
        mComponentType = a.getInt(R.styleable.FormInputLayout_customer_component, 1);
        isMandatory = a.getBoolean(R.styleable.FormInputLayout_customer_isMandatory, false);
        isMultiline = a.getBoolean(R.styleable.FormInputLayout_customer_isMultiLine, false);
        mInputType = a.getInt(R.styleable.FormInputLayout_customer_inputType, 1);
        BgBackground=a.getResourceId(R.styleable.FormInputLayout_customer_background,R.drawable.bg_txt_square);
        isShowStrength=a.getBoolean(R.styleable.FormInputLayout_customer_showPassStrength,false);
        arrayList=a.getResourceId(R.styleable.FormInputLayout_customer_array,R.array.array);


        /**
         * Set up the values
         */
        setLabel(mLabel);
        setMandatory(isMandatory);
        setHint(mHint);
        setInputType(mInputType);
        setBgBackground(BgBackground);
        setComponentType(mComponentType);
        setMaxLength(maxLength);

        if (isMultiline) {
            setMultiline(true, maxLength);
        }


        iconCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                txtInputBox.setText("");
            }
        });

        autoCompleteTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTxt.showDropDown();
            }
        });

        disableDropDownTextSelection();
    }


    private void PasswordStrength(boolean isShowStrength){
        layPassStrength.setVisibility(isShowStrength ? VISIBLE : GONE);
    }

    private void showInputError(String error, int visible){
        mErrorMessage=error;
        tvError.setText(error);
        tvError.setVisibility(visible);
        if(visible==VISIBLE){
            inputError=1;
        }else {
            inputError=0;
        }
    }

    private void updatePasswordStrengthView(String password) {
        if (password.length() == 0) {
            showInputError(String.format(getResources().getString(R.string.cantBeEmpty),tvLabel.getText()),VISIBLE);
            PassProgressStrength.setProgress(0);
            return;
        }else if(password.length() <8){
            showInputError(String.format(getResources().getString(R.string.hintPassword),mLabel),VISIBLE);
        }else {
            showInputError("",GONE);
        }

        PasswordStrength str = PasswordStrength.calculateStrength(password);
        tvPassStrength.setText(str.toString());
        tvPassStrength.setTextColor(str.getColor());

       PassProgressStrength.getProgressDrawable().setColorFilter(str.getColor(), PorterDuff.Mode.SRC_ATOP);

        if (str.toString().equals("Weak")) {
            PassProgressStrength.setProgress(25);
        } else if (str.toString().equals("Medium")) {
            PassProgressStrength.setProgress(75);
        } else {
            PassProgressStrength.setProgress(100);

        }
    }

    public void setLabel(String label) {
        if (mLabel != null) {
            if (mLabel.length() > 0) {
                layLabel.setVisibility(VISIBLE);
            } else {
                setLabelVisibility(false);
            }
            tvLabel.setText(label);
        }else {
            layLabel.setVisibility(GONE);
        }
    }

    public void setLabelVisibility(boolean shouldShow) {
        tvLabel.setVisibility(shouldShow ? VISIBLE : GONE);
    }

    public void setHint(String hint) {
        if (mComponentType == TYPE_INPUTBOX)
            txtInputBox.setHint(hint);
        if (mComponentType == TYPE_AUTO_COMPLETE)
            autoCompleteTxt.setHint(hint);
        if (mComponentType== TYPE_PASSWORD)
            txtPassword.setHint(hint);

    }

    public void setInputType(int inputType) {
        mInputType = inputType;
        if (mComponentType == TYPE_INPUTBOX) {
            if (mInputType == INPUTTYPE_TEXT) {
                txtInputBox.setInputType(InputType.TYPE_CLASS_TEXT);
                txtInputBox.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            } else if (mInputType == INPUTTYPE_PHONE) {
                txtInputBox.setInputType(InputType.TYPE_CLASS_PHONE);
            } else if (mInputType == INPUTTYPE_NUMBER) {
                txtInputBox.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (mInputType == INPUTTYPE_EMAIL) {
                txtInputBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            }
        }
    }

    public void setBgBackground(int background){
        layNestedInputBox.setBackgroundResource(background);
    }

    public void setComponentType(int componentType) {
        mComponentType = componentType;
        switch (componentType) {
            case TYPE_INPUTBOX:
                layLabel.setVisibility(VISIBLE);
                layInputBox.setVisibility(VISIBLE);
                txtInputBox.addTextChangedListener(this);
                break;
            case TYPE_PASSWORD:
                layLabel.setVisibility(VISIBLE);
                layPassword.setVisibility(VISIBLE);
                txtPassword.addTextChangedListener(this);
                PasswordStrength(isShowStrength);
                break;
            case TYPE_AUTO_COMPLETE:
                layLabel.setVisibility(VISIBLE);
                layAutoComplete.setVisibility(VISIBLE);
                String[] autoCompleteArray=getResources().getStringArray(arrayList);
                ArrayList<String> autoCompleteListArray = new ArrayList<>(Arrays.asList(autoCompleteArray));
                setAutoCompleteList(autoCompleteListArray);
                break;
            case TYPE_SPINNER:
                layLabel.setVisibility(VISIBLE);
                laySpinner.setVisibility(VISIBLE);
                String[] getArray=getResources().getStringArray(arrayList);
                List<String> listArray = Arrays.asList(getArray);
                setSpinner(listArray);
                break;
            case TYPE_BUTTON:
                layLabel.setVisibility(GONE);
                layButton.setVisibility(VISIBLE);
                break;

            default:
                layInputBox.setVisibility(VISIBLE);
                break;
        }
    }

    public void setMaxLength(int getMaxLength) {
        maxLength = getMaxLength;
        if (mComponentType == TYPE_INPUTBOX) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(maxLength);
            txtInputBox.setFilters(filterArray);
        }
        if (mComponentType == TYPE_AUTO_COMPLETE) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(maxLength);
            autoCompleteTxt.setFilters(filterArray);
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "RtlHardcoded"})
    public void setMultiline(boolean isMultiline, int maxLength) {
        setMaxLength(maxLength);
        this.isMultiline = isMultiline;
        txtLengthDesc.setVisibility(VISIBLE);
        iconCancel.setVisibility(GONE);

        final LayoutParams lparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height); // Width , height
        getInputBox().setLayoutParams(lparams);
        getInputBox().setSingleLine(false);
        getInputBox().setGravity(Gravity.LEFT | Gravity.TOP);
        getInputBox().setPadding(15, 15, 15, 15);

        txtInputBox.setScrollBarStyle(SCROLLBARS_INSIDE_INSET);
        txtInputBox.setVerticalScrollBarEnabled(true);
        txtInputBox.setOverScrollMode(0);
        txtInputBox.setHeight(height);
        txtInputBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.txtInputBox) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });
    }

    public FormInputLayout setMaxLines(int maxLines) {
        txtInputBox.setMaxLines(maxLines);
        return this;
    }

    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
        tvMandatory.setVisibility(isMandatory ? VISIBLE : GONE);
        //inputError=isMandatory ? 1 : 0;

        if(isMandatory){
            inputError=1;
        }else {
            inputError=0;
        }
    }

    public EditText getInputBox() {
        if (mComponentType == TYPE_INPUTBOX) {
            return txtInputBox;
        }else if(mComponentType == TYPE_PASSWORD){
            return txtPassword;
        }else {
            return null;
        }
    }

    public Spinner getSpinner(){
        if (mComponentType == TYPE_SPINNER) {
            return spSpinner;
        }else{
            return null;
        }
    }

    public AutoCompleteView getAutoCompleteView(){
        if (mComponentType == TYPE_AUTO_COMPLETE) {
            return autoCompleteTxt;
        }else{
            return null;
        }
    }

    public String getValue() {
        if (mComponentType == TYPE_INPUTBOX) {
            return txtInputBox.getText().toString();
        } else if (mComponentType == TYPE_AUTO_COMPLETE) {
            return autoCompleteTxt.getText().toString();
        } else  if (mComponentType == TYPE_PASSWORD){
            return txtPassword.getText().toString();
        } else if (mComponentType==TYPE_SPINNER){
            return spSpinner.getSelectedItem().toString();
        }

        return "";
    }

    public int getError(){
        showMessage(inputError+" ");
        if(inputError==1){
            showInputError(mErrorMessage,VISIBLE);
        }else {
            showInputError("",GONE);
        }

        return inputError;
    }

    public FormInputLayout setValue(String value) {
        if (mComponentType == TYPE_INPUTBOX) {
            mValue = value;
            txtInputBox.setText(value);
        } else if (mComponentType == TYPE_AUTO_COMPLETE) {
            mValue = value;
            autoCompleteTxt.setText(value);
        } else if (mComponentType==TYPE_SPINNER){
            mValue = value;
            setSpinnerValue(value);
        }
        return this;
    }





    public void setAutoCompleteShowAlways(boolean show) {
        autoCompleteTxt.setShowAlways(show);
    }


    private void showMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void disableDropDownTextSelection() {
        autoCompleteTxt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });

        autoCompleteTxt.setLongClickable(false);
        autoCompleteTxt.setTextIsSelectable(false);
    }

    public FormInputLayout showDropDown() {
        if (mComponentType == TYPE_AUTO_COMPLETE) {
            autoCompleteTxt.showDropDown();
        }
        return this;
    }

    public FormInputLayout disableAutoCompleteSearch() {

        if (mAdapterAutocomplete != null)
            mAdapterAutocomplete.disableFilter(true);

        autoCompleteTxt.setLongClickable(false);
        autoCompleteTxt.setTextIsSelectable(false);
        autoCompleteTxt.setFocusable(false);

        autoCompleteTxt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });
        return this;
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mOnTouchListener = listener;
    }



    public interface SpinnerSelectionListener {
        void onSpinnerItemSelected(String item);
    }

    public interface AutoCompleteSelectionListener {
        void onAutoCompleteItemSelected(String item);
    }

    public interface OnTouchListener {
        void onTouch();
    }

    public interface OnTagChangeListener {
        void onTagChange(boolean isPrimary);
    }





    /**
     * Auto Complete
     *
     */
    public FormInputLayout setAutoCompleteList(ArrayList<String> items, final AutoCompleteSelectionListener listener) {
        layAutoComplete.setVisibility(VISIBLE);

        layInputBox.setVisibility(GONE);
        autoCompleteTxt.setShowAlways(true);
//        autoCompleteTxt.setTypeface(BaseApplication.getLatoItalicTypeFace());
        mAdapterAutocomplete = new AutoCompleteAdapter(getContext(), R.layout.form_input_row, R.id.txtAutocomplete, items, new AutoCompleteAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(String item) {
                listener.onAutoCompleteItemSelected(item);
                mValue = item;
                if (mOnTouchListener != null)
                    mOnTouchListener.onTouch();

                autoCompleteTxt.setText(item);
                autoCompleteTxt.setSelection(item.length());
                autoCompleteTxt.dismissDropDown();
            }
        });
        autoCompleteTxt.setAdapter(mAdapterAutocomplete);
        return this;
    }

    public FormInputLayout setAutoCompleteList(ArrayList<String> items) {
        layAutoComplete.setVisibility(VISIBLE);

        layInputBox.setVisibility(GONE);
        autoCompleteTxt.setShowAlways(true);
//        autoCompleteTxt.setTypeface(BaseApplication.getLatoItalicTypeFace());
        mAdapterAutocomplete = new AutoCompleteAdapter(getContext(), R.layout.form_input_row, R.id.txtAutocomplete, items, new AutoCompleteAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(String item) {
                mValue = item;
                if (mOnTouchListener != null)
                    mOnTouchListener.onTouch();

                autoCompleteTxt.setText(item);
                autoCompleteTxt.setSelection(item.length());
                autoCompleteTxt.dismissDropDown();
            }
        });
        autoCompleteTxt.setAdapter(mAdapterAutocomplete);
        return this;
    }

    /**
     * Spinner
     * @param items
     * @param listener
     * @return
     */
    public FormInputLayout setSpinner(ArrayList<String> items, final SpinnerSelectionListener listener) {
        laySpinner.setVisibility(VISIBLE);

        layInputBox.setVisibility(GONE);
        layAutoComplete.setVisibility(GONE);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.onSpinnerItemSelected( parent.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spSpinner.setAdapter(spinnerArrayAdapter);
        return this;
    }

    public void setSpinner(List<String> items) {
        laySpinner.setVisibility(VISIBLE);

        layInputBox.setVisibility(GONE);
        layAutoComplete.setVisibility(GONE);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSpinner.setAdapter(spinnerArrayAdapter);


        spSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isMandatory){
                    validateSpinner(mHint);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void validateSpinner(String hint){
        if ( getValue().equals(hint) ){
            showInputError(String.format(getResources().getString(R.string.isRequired),tvLabel.getText()),VISIBLE);
        }else {
            showInputError("",GONE);
        }
    }

    private void setSpinnerValue(String mValue){
        String[] mArrayList=getResources().getStringArray(arrayList);
        for (int index = 0;index< mArrayList.length;index++){
            if(mValue.equals(mArrayList[index])){
                spSpinner.setSelection(index);
            }
        }
    }

    public FormInputLayout VarifySelectedSpinner(final String DefaultString) {

        spSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String stField =spSpinner.getSelectedItem().toString();
                TextView errorText = (TextView)spSpinner.getSelectedView();
                if (stField.equals(DefaultString)){
                    if(errorText!=null){
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);
                        errorText.setText(getResources().getString(R.string.notSelected));
                    }

                }else{
                    errorText.setError(null);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /*boolean error = true;
        String stField =spSpinner.getSelectedItem().toString();
        TextView errorText = (TextView)spSpinner.getSelectedView();
        if (stField.equals(DefaultString)){
            if(errorText!=null){
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText(getResources().getString(R.string.notSelected));
                error=false;
            }

        }else{
            errorText.setError(null);
        }*/
        return this;
    }

    public float convertDpToPixel(float dp) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


    /**
     * Button
     */

    public Button getButton(){
        return btnNoImage;
    }

    public void showLoading(int Visibility){
        btnProgressBar.setVisibility(Visibility);
    }



    /**
     * Listener on text change
     * */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(mComponentType==TYPE_PASSWORD){
            updatePasswordStrengthView( s.toString());
        }
        if (mComponentType==TYPE_INPUTBOX) {
            inputBoxOnTextChange(s.toString());
        }
    }
    @Override
    public void afterTextChanged(Editable s) {

    }

    private void inputBoxOnTextChange(String mValue){
        if (mOnTouchListener != null){
            mOnTouchListener.onTouch();
        }
        // Show or Hide Cancel button
        iconCancel.setVisibility(mValue.length() > 0 ? VISIBLE : GONE);

        if (mValue.length() == 0) {
            if(isMandatory){
                showInputError(String.format(getResources().getString(R.string.cantBeEmpty),tvLabel.getText()),VISIBLE);
            }else {
                showInputError("",GONE);
            }
            inputError=1;
        }else {
            // Hide empty error message
            showInputError("",GONE);
            inputError=0;




            if (mInputType == INPUTTYPE_EMAIL) {
                if (mPresenter.isValidEmail(mValue)) {
                    txtInputBox.setTextColor(getResources().getColor(R.color.black));
                    showInputError("",GONE);
                    inputError=0;
                } else{
                    txtInputBox.setTextColor(getResources().getColor(R.color.red));
                    showInputError(getResources().getString(R.string.inValidEmail),VISIBLE);
                    inputError=1;
                }
            }

            if (mInputType == INPUTTYPE_PHONE) {
                if (mPresenter.isValidPhoneNumber(mValue)) {
                    txtInputBox.setTextColor(getResources().getColor(R.color.black));
                    showInputError("",GONE);
                }else {
                    txtInputBox.setTextColor(getResources().getColor(R.color.red));
                    showInputError(getResources().getString(R.string.inValidPhoneNumber),VISIBLE);
                }
            }

            if (isMultiline) {
                int rem = maxLength - mValue.length();
                txtLengthDesc.setText(rem + "/" + maxLength + " Characters Only");
                iconCancel.setVisibility(GONE);
            }
        }
    }


    
    /**
     * Save Instance State of the view
     * */
    @SuppressWarnings("unchecked")
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable saveInstanceState = super.onSaveInstanceState();
        FormInputLayout.SavedState savedState = new FormInputLayout.SavedState(saveInstanceState);
        savedState.childrenStates = new SparseArray();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).saveHierarchyState(savedState.childrenStates);
        }
        return savedState;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        FormInputLayout.SavedState savedState = (FormInputLayout.SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).restoreHierarchyState(savedState.childrenStates);
        }
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    private static class SavedState extends BaseSavedState {
        private SparseArray childrenStates;
        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in, ClassLoader classLoader) {
            super(in);
            childrenStates = in.readSparseArray(classLoader);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSparseArray(childrenStates);
        }

        public static final ClassLoaderCreator<FormInputLayout.SavedState> CREATOR = new ClassLoaderCreator<FormInputLayout.SavedState>() {
            @Override
            public FormInputLayout.SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new FormInputLayout.SavedState(source, loader);
            }

            @Override
            public FormInputLayout.SavedState createFromParcel(Parcel source) {
                return createFromParcel(null);
            }

            public FormInputLayout.SavedState[] newArray(int size) {
                return new FormInputLayout.SavedState[size];
            }
        };
    }

}
