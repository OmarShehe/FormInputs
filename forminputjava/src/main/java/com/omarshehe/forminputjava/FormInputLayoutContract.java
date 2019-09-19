package com.omarshehe.forminputjava;

import java.util.ArrayList;

public interface FormInputLayoutContract {

    public interface View {

    }

    public interface Presenter {
        public ArrayList<String> getTimeSlots();

        public String pad(int input);

        public boolean isValidEmail(CharSequence email);

        public boolean isValidPhoneNumber(CharSequence phoneNumber);
    }
}
