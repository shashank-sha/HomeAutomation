package com.shashank.homeautomation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    //private List<TextView> TextViewList = new ArrayList<TextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("shaPreferences", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final int rooms=sharedPreferences.getInt("rooms",4);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearlayoutVerticalHome);

        for(int i=0;i<rooms;i++) {
            String room_name = sharedPreferences.getString("room" + (i + 1) + "_name", "roomname");
            final TextView textView = new TextView(this);
            textView.setText(room_name.toUpperCase());
            textView.setTypeface(null, Typeface.BOLD);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 20, 0, 0);
            textView.setLayoutParams(llp);

            textView.setClickable(true);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), textView.getText().toString(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), sharedPreferences.getInt(textView.getText().toString(),100)+"", Toast.LENGTH_SHORT).show();

                    int temp=sharedPreferences.getInt(textView.getText().toString(),100);
                    //Set<String> s = sharedPreferences.getStringSet("room" + temp + "_appliances",null);
                    //Iterator<String> itr=s.iterator();
                    //while(itr.hasNext())
                    //Toast.makeText(getApplicationContext(), s+"", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(HomeActivity.this,ParticularRoomActivity.class);
                    intent.putExtra("room_number",temp);
                    startActivity(intent);



                }
            });
            //textView.setId(i + 1);
            //Log.d("zzzzzzzzzzzzzzzzzzz",room_name+"");
            linearLayout.addView(textView);
        }
    }
}
