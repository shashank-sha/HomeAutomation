package com.zemoso.zinteract.ZinteractSampleApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.crashlytics.android.Crashlytics;

import com.zemosolabs.zinteract.sdk.Zinteract;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Crashlytics.start(this);
        setContentView(R.layout.activity_main);
        Zinteract.initializeWithContextAndKey(getApplicationContext(),"43eb-a23d-0106af662c83","914500168484");
        //Zinteract.initializeWithContextAndKey(getApplicationContext(),"ios67","914500168484");
        Zinteract.setUserProperty("fname","John");
        Zinteract.setUserProperty("lname","Doe");
        Zinteract.setUserProperty("age","39");
        Zinteract.enableDebugging();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        Zinteract.logEvent("view screen1");
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void sendToActivity2(View view)
    {
        Zinteract.logEvent("clicked to view screen2");
        Intent intent = new Intent(MainActivity.this, Activity2.class);
        startActivity(intent);
    }
}
