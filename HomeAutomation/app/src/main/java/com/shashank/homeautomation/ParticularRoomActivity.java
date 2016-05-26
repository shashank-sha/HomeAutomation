package com.shashank.homeautomation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
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
        TextView room = (TextView) findViewById(R.id.room_textView);
        room.setText(room_name.toUpperCase());
        room.setTypeface(null, Typeface.BOLD);
        room.setGravity(Gravity.CENTER);
        //linearLayout.addView(room);

        room.setTextSize(TypedValue.COMPLEX_UNIT_DIP,40.f);
        room.setTextColor(Color.parseColor("#FFFFFF"));
        room.setShadowLayer(5,3,3,Color.BLACK);

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

                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25.f);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setShadowLayer(5,3,3,Color.BLACK);

                textView.setClickable(true);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int val=sharedPreferences.getInt("room"+room_number+"_"+appliance_name,0);
                        Toast.makeText(getApplicationContext(), val+"", Toast.LENGTH_SHORT).show();
                    }
                });

                LinearLayout linearLayoutHorizontal = new LinearLayout(this);
                linearLayoutHorizontal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);

                linearLayoutHorizontal.addView(textView);


                linearLayout.addView(linearLayoutHorizontal);
                View v = new View(this);
                v.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        2
                ));
                v.setBackgroundColor(Color.parseColor("#FFFFFF"));

                setMargins(v,0,15,0,0);

                linearLayout.addView(v);

        }


    }


    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}


