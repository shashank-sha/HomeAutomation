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

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("shaPreferences", Context.MODE_PRIVATE);

        Boolean check = sharedPreferences.getBoolean("first_time",true);

        if(check) {
            sharedPreferences.edit().clear().commit();
            Intent intent = new Intent(MainActivity.this, BeforeMain_1.class);
            startActivity(intent);
        }

        else{
            Intent intent = new Intent(MainActivity.this, HomeOptionsActivity.class);
            startActivity(intent);
        }

    }
}
