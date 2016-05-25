package com.shashank.homeautomation;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BeforeMain_4 extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button nextButton3;
    private List<EditText> EditTextList = new ArrayList<EditText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_main_4);

        sharedPreferences = getSharedPreferences("shaPreferences", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        int rooms=sharedPreferences.getInt("rooms",4);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearlayoutVertical4);


        for(int i=0;i<rooms;i++) {
            String room_name=sharedPreferences.getString("room" + (i+1) +"_name","roomname");
            TextView textView =new TextView(this);
            textView.setText(room_name.toUpperCase());
            //textView.setId(i + 1);
            Log.d("zzzzzzzzzzzzzzzzzzz",room_name+"");

            linearLayout.addView(textView);
            int num_appliances=sharedPreferences.getInt(room_name+"_num",1);
            Log.d("zzzzzzzzzzzzzzzzzzz",num_appliances+"");
            for (int j = 0; j < num_appliances; j++) {
                EditText e = new EditText(this);
                String hint = "name of appliance "+(j+1);
                e.setHint(hint);
                e.setId(j + 1);
                //e.setInputType(InputType.TYPE_CLASS_NUMBER);
                linearLayout.addView(e);
                EditTextList.add(e);        //EditTexts will be added to the List in order provided(1-5)
            }
        }

    }
}
