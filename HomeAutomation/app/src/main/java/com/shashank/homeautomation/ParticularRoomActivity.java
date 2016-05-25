package com.shashank.homeautomation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Set;

public class ParticularRoomActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_room);

        final int room_number=getIntent().getExtras().getInt("room_number");
        Toast.makeText(getApplicationContext(), room_number+"", Toast.LENGTH_SHORT).show();

        sharedPreferences = getSharedPreferences("shaPreferences", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final int appliances=sharedPreferences.getInt("room"+room_number+"_num",4);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearlayoutVerticalRoom);

        String room_name = sharedPreferences.getString("room" + room_number + "_name", "roomname");
        TextView room = new TextView(this);
        room.setText(room_name.toUpperCase());
        room.setTypeface(null, Typeface.BOLD);
        room.setGravity(Gravity.CENTER);
        linearLayout.addView(room);

        //LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //llp.setMargins(0, 20, 0, 0);
        //textView.setLayoutParams(llp);



            Set<String> s = sharedPreferences.getStringSet("room" + room_number + "_appliances",null);
            Iterator<String> itr=s.iterator();
            while(itr.hasNext()) {
                final String appliance_name=itr.next();

                //String appliance_name = sharedPreferences.getString("room" + (i + 1) + "_name", "roomname");
                TextView textView = new TextView(this);
                textView.setText(appliance_name);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                llp.setMargins(0, 20, 0, 0);
                textView.setLayoutParams(llp);

                textView.setClickable(true);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int val=sharedPreferences.getInt("room"+room_number+"_"+appliance_name,0);
                        Toast.makeText(getApplicationContext(), val+"", Toast.LENGTH_SHORT).show();
                    }
                });

                linearLayout.addView(textView);

        }


    }
}
