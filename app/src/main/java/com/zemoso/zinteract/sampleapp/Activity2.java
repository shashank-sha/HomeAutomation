package com.zemoso.zinteract.sampleapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zemoso.zinteract.sdk.Zinteract;


public class Activity2 extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        Zinteract.startSession();
        Zinteract.logEvent("view screen2");
    }

    @Override
    public void onPause(){
        super.onPause();
        Zinteract.endSession();
    }

    public void sendToActivity3(View view)
    {
        Zinteract.logEvent("clicked to view screen3");
        Intent intent = new Intent(Activity2.this, Activity3.class);
        startActivity(intent);
    }

    public void sendToActivity1(View view)
    {
        Zinteract.logEvent("clicked to view screen1");
        Intent intent = new Intent(Activity2.this, MainActivity.class);
        startActivity(intent);
    }
}
