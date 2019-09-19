package com.omarshehe.forminputjava.Utils;

import android.content.res.Resources;
import android.widget.Spinner;

public class Utils {
    public static void SetDataInSpinner(Resources resources, String field, int array, Spinner spinner){
        final String[] CategoryArray = resources.getStringArray(array);
        for (int index = 0;index< CategoryArray.length;index++){
            if(field.equals(CategoryArray[index])){
                spinner.setSelection(index);
            }
        }
    }
}
