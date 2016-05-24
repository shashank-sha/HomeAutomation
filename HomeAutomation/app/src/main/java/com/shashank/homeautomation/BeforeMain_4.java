package com.shashank.homeautomation;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearlayoutVertical3);


    }
}
