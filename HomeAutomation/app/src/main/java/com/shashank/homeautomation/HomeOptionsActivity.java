package com.shashank.homeautomation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeOptionsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.rsz_bg);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



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
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,40.f);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            textView.setShadowLayer(5,3,3,Color.BLACK);
            //textView.setBackgroundColor(Color.BLACK);

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

                    Intent intent = new Intent(HomeOptionsActivity.this,ParticularRoomOptions.class);
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
