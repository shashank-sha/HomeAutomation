package com.zemoso.zinteract.ZinteractSampleApp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zemosolabs.zinteract.sdk.Zinteract;


public class Activity5 extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity5);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity5, menu);
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
        Zinteract.logEvent("view screen5");
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void sendToActivity4(View view)
    {
        Zinteract.logEvent("clicked to view screen4");
        Intent intent = new Intent(Activity5.this, Activity4.class);
        startActivity(intent);
    }

    public void sendToActivity1(View view)
    {
        Zinteract.logEvent("clicked to view screen1");
        Intent intent = new Intent(Activity5.this, MainActivity.class);
        startActivity(intent);
    }
}
