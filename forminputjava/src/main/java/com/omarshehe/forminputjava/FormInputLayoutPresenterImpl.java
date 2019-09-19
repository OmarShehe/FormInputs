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
        arrayList.add("12:00 AM");
        arrayList.add("12:30 AM");
        arrayList.add("01:00 AM");
        arrayList.add("01:30 AM");
        arrayList.add("02:00 AM");
        arrayList.add("02:30 AM");
        arrayList.add("03:00 AM");
        arrayList.add("03:30 AM");
        arrayList.add("04:00 AM");
        arrayList.add("04:30 AM");
        arrayList.add("05:00 AM");
        arrayList.add("05:30 AM");
        arrayList.add("06:00 AM");
        arrayList.add("06:30 AM");
        arrayList.add("07:00 AM");
        arrayList.add("07:30 AM");
        arrayList.add("08:00 AM");
        arrayList.add("08:30 AM");
        arrayList.add("09:00 AM");
        arrayList.add("09:30 AM");
        arrayList.add("10:00 AM");
        arrayList.add("10:30 AM");
        arrayList.add("11:00 AM");
        arrayList.add("11:30 AM");
        arrayList.add("12:00 PM");
        arrayList.add("12:30 PM");
        arrayList.add("01:00 PM");
        arrayList.add("01:30 PM");
        arrayList.add("02:00 PM");
        arrayList.add("02:30 PM");
        arrayList.add("03:00 PM");
        arrayList.add("03:30 PM");
        arrayList.add("04:00 PM");
        arrayList.add("04:30 PM");
        arrayList.add("05:00 PM");
        arrayList.add("05:30 PM");
        arrayList.add("06:00 PM");
        arrayList.add("06:30 PM");
        arrayList.add("07:00 PM");
        arrayList.add("07:30 PM");
        arrayList.add("08:00 PM");
        arrayList.add("08:30 PM");
        arrayList.add("09:00 PM");
        arrayList.add("09:30 PM");
        arrayList.add("10:00 PM");
        arrayList.add("10:30 PM");
        arrayList.add("11:00 PM");
        arrayList.add("11:30 PM");
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
