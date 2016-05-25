package com.shashank.homeautomation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ParticularRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_room);

        int room_number=getIntent().getExtras().getInt("room_number");
        Toast.makeText(getApplicationContext(), room_number+"", Toast.LENGTH_SHORT).show();

    }
}
