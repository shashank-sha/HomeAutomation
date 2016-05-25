package com.shashank.homeautomation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BeforeMain_3 extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button nextButton3;
    private List<EditText> EditTextList = new ArrayList<EditText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_main_3);

        sharedPreferences = getSharedPreferences("shaPreferences", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        int rooms=sharedPreferences.getInt("rooms",4);

        Log.d("zzzzzzzzzzzzzzzzzzz",rooms+"");

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearlayoutVertical3);

        for(int i=0;i<rooms;i++) {
            EditText e = new EditText(this);
            String hint = sharedPreferences.getString("room"+(i+1)+"_name","Error");
            e.setHint(hint);
            e.setGravity(Gravity.CENTER);
            e.setId(i+1);
            e.setInputType(InputType.TYPE_CLASS_NUMBER);
            linearLayout.addView(e);
            EditTextList.add(e);        //EditTexts will be added to the List in order provided(1-5)
        }

        linearLayout.setGravity(Gravity.CENTER_VERTICAL);


        nextButton3 = (Button) findViewById(R.id.nextButton_3_button);
        nextButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean not_empty=true;
                for (EditText editText:EditTextList){
                    if(editText.getText().toString().matches(""))
                        not_empty=false;
                }
                if(not_empty) {
                    int i = 1;
                    for (EditText editText : EditTextList) {
                        //String room = "room" + i + "_name";
                        editor.putInt("room"+i+"_num", Integer.parseInt(editText.getText().toString()));
                        editor.commit();
                        i++;
                    }
                    Intent intent=new Intent(BeforeMain_3.this,BeforeMain_4.class);
                    startActivity(intent);
                }

                else {
                    Toast.makeText(getApplicationContext(), "Text fields cannot be empty", Toast.LENGTH_SHORT).show();
                    not_empty=true;
                }
            }
        });
    }
}
