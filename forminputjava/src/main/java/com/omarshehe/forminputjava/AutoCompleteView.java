package com.omarshehe.forminputjava;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;


public class AutoCompleteView extends AppCompatAutoCompleteTextView {
    private boolean showAlways;
    public AutoCompleteView(Context context) {
        super(context);
    }

    public AutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setTypeface(BaseApplication.getLatoItalicTypeFace());
    }

    public AutoCompleteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setTypeface(BaseApplication.getLatoItalicTypeFace());
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public AutoCompleteView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public void setShowAlways(boolean showAlways) {
        this.showAlways = showAlways;
    }

    @Override
    public boolean enoughToFilter() {
        return showAlways || super.enoughToFilter();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        showDropDownIfFocused();
    }

    private void showDropDownIfFocused() {
        if (enoughToFilter() && isFocused() && getWindowVisibility() == View.VISIBLE) {

            showDropDown();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        showDropDownIfFocused();
    }


}
