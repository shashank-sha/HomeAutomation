package com.shashank.homeautomation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.ArraySet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BeforeMain_4 extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button nextButton4;
    private List<EditText> EditTextList = new ArrayList<EditText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_main_4);

        sharedPreferences = getSharedPreferences("shaPreferences", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final int rooms=sharedPreferences.getInt("rooms",4);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearlayoutVertical4);




        for(int i=0;i<rooms;i++) {
            String room_name=sharedPreferences.getString("room" + (i+1) +"_name","roomname");
            TextView textView =new TextView(this);
            textView.setText(room_name.toUpperCase());
            textView.setTypeface(null, Typeface.BOLD);
            textView.setGravity(Gravity.CENTER);
            //textView.setId(i + 1);
            Log.d("zzzzzzzzzzzzzzzzzzz",room_name+"");
            linearLayout.addView(textView);
            int num_appliances=sharedPreferences.getInt("room"+(i+1)+"_num",1);
            //tot_appliances=tot_appliances+num_appliances;
            Log.d("zzzzzzzzzzzzzzzzzzz",num_appliances+"");


            for (int j = 0; j < num_appliances; j++) {
                EditText e = new EditText(this);
                String hint = "name of appliance "+(j+1);
                e.setHint(hint);
                e.setGravity(Gravity.CENTER);
                e.setId(j + 1);
                //e.setInputType(InputType.TYPE_CLASS_NUMBER);
                linearLayout.addView(e);
                EditTextList.add(e);        //EditTexts will be added to the List in order provided(1-5)
            }
        }

        linearLayout.setGravity(Gravity.CENTER);

        nextButton4 = (Button) findViewById(R.id.nextButton_4_button);
        nextButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean not_empty=true;
                for (EditText editText:EditTextList){
                    if(editText.getText().toString().matches(""))
                        not_empty=false;
                }
                if(not_empty) {
                    Log.d("ssssssssssssssssssssss", EditTextList.size()+"");

                    int room_num, appliance_num, i = 0;
                    LinkedHashSet<String> appliances = new LinkedHashSet<String>();
                    for (room_num = 0; room_num < rooms; room_num++) {
                        Log.d("aaaaaaaaaaaaaaaaaaaaaaa", room_num+"");
                        int num_appliances=sharedPreferences.getInt("room"+(room_num+1)+"_num",1);
                        //Log.d("nnnnnnnnnnnnnnnnnnnn", num_appliances+"");
                        for (appliance_num = 0; appliance_num < num_appliances; appliance_num++) {
                            Log.d("bbbbbbbbbbbbbbbbbb", appliance_num+"");
                            EditText editText = EditTextList.get(i);
                            Log.d("zzzzzzzzzzzzzzzzzzz", editText.getText().toString());
                            editor.putString("room" + (room_num + 1) + "_appliance" + (appliance_num + 1) + "_name", editText.getText().toString());
                            editor.putInt("room" + (room_num + 1) + "_" + editText.getText().toString(),room_num+1);
                            editor.commit();
                            appliances.add(editText.getText().toString());
                            i++;
                        }
                        Log.d("nnnnnnnnnnnnnnnnnnnn", appliances+"");
                        editor.putStringSet("room" + (room_num + 1) + "_appliances",appliances);
                        editor.commit();
                        appliances.clear();
                    }

                    //Log.d("iiiiiiiiiiiiiiiiiiiii", sharedPreferences.getString("room1_appliance2_name","error"));
                    //Log.d("iiiiiiiiiiiiiiiiiiiii", sharedPreferences.getString("room2_appliance1_name","error"));
                    //Log.d("iiiiiiiiiiiiiiiiiiiii", sharedPreferences.getString("room2_appliance2_name","error"));

                    editor.putBoolean("first_time",false);      //4 activities will never shown again
                    editor.commit();


                    Intent intent=new Intent(BeforeMain_4.this,HomeActivity.class);
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
