package com.shashank.homeautomation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BeforeMain_1 extends AppCompatActivity {

    private Button nextButton1;
    private EditText myHome,rooms;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_main_1);


        myHome = (EditText) findViewById(R.id.myHome_editText);
        rooms = (EditText) findViewById(R.id.rooms_editText);

        nextButton1 = (Button) findViewById(R.id.nextButton_1_button);
        nextButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rooms.getText().toString().matches(""))
                    Toast.makeText(getApplicationContext(), "No. of rooms cannot be empty", Toast.LENGTH_LONG).show();
                else{
                    sharedPreferences = getSharedPreferences("shaPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("rooms",Integer.parseInt(rooms.getText().toString()));
                    if(myHome.getText().toString().matches(""))
                        editor.putString("home_name","My Home");
                    else
                        editor.putString("home_name",myHome.getText().toString());
                    editor.commit();

                    Intent intent = new Intent(BeforeMain_1.this,BeforeMain_2.class);
                    startActivity(intent);
                }
            }
        });


    }
}
