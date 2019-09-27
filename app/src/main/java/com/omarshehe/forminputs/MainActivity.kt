package com.omarshehe.forminputs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSubmit.getButton().setOnClickListener {
            run {
                if(!gender.isError(mainView)  && !fullName.isError(mainView) && !price.isError(mainView) && !fullName.isError(mainView) &&  !phoneNumber.isError(mainView) && !ID.isError(mainView) && !email.isError(mainView) && !about.isError(mainView)  && !password.isError(mainView)){
                    btnSubmit.showLoading(true)


                }
            }}




        /* btnSubmit.button.setOnClickListener {
             run {
                 if(!gender.isError(mainView)  && !company.isError(mainView) && !price.isError(mainView) && !fullName.isError(mainView) &&  !phoneNumber.isError(mainView) && !ID.isError(mainView) && !email.isError(mainView) && !about.isError(mainView)  && !password.isError(mainView)){
                     btnSubmit.showLoading(View.VISIBLE)
                     Log.d("MA",price.valueSpinner_Input[0] +" "+price.valueSpinner_Input[1])

                 }
             }
         }


          ArrayList<String>m=new ArrayList<>();
          m.add("Select Name");
          m.add("Omar Shehe");
          m.add("Juma Shehe");
          mmm.setSpinner(m,this);*/
    }
    /*@Override
   public void onSpinnerItemSelected(String item) {
       Toast.makeText(getApplicationContext(),item,Toast.LENGTH_LONG).show();
   }*/

}
