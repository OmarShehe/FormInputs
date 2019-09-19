package com.omarshehe.forminputs

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSubmit.button.setOnClickListener {
            run {
                Toast.makeText(this, "Hi", Toast.LENGTH_LONG).show()
                Log.d("MainActivityA", "Omar")
                if(gender.error==0 && fullName.error==0 && phoneNumber.error==0 && ID.error==0 && email.error==0 && about.error ==0 && password.error==0){
                    btnSubmit.showLoading(View.VISIBLE)
                }
            }
        }

        /*
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
