package com.omarshehe.forminputjava;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.ArrayList;

public class FormInputLayoutPresenterImpl implements FormInputLayoutContract.Presenter {

    private FormInputLayoutContract.View mView;

    public FormInputLayoutPresenterImpl(FormInputLayoutContract.View view) {
        mView = view;
    }

    @Override
    public ArrayList<String> getTimeSlots() {
        ArrayList<String> arrayList = new ArrayList<>();
        return arrayList;
    }

    /**
     * to pan 0 for less than 10 values
     *
     * @param input
     * @return
     */
    @Override
    public String pad(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }

    @Override
    public  boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!phoneNumber.equals("")){
            if( phoneNumber.length() == 10  || phoneNumber.length() == 13) {
                return Patterns.PHONE.matcher(phoneNumber).matches();
            }else{
                return false;
            }
        }else{
            return false;
        }

    }
}
