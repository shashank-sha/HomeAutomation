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
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);

        /*Initialize Zinteract with Context and key provided upon registration on Zinteract website.
        Optionally the Google Api Project key and custom class for IN APP PROMOTION can be passed
        in as arguments.*/

        Zinteract.initializeWithContextAndKey(getApplicationContext(),
                                                "43eb-a23d-0106af662c83","914500168484");

        /* Setting the user properties(optional).
         Provide the user properties which can be useful to segment users. Once the user properties
         are set through these methods, they can used to filter users through you Zinteract account */

        Zinteract.setUserProperty("fname","John");
        Zinteract.setUserProperty("lname","Doe");
        Zinteract.setUserProperty("age","39");

        /*enableDebugging method should only be used for uploading screens for to the Zinteract
         website for dynamic editing or to read the logs for troubleshooting */

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
        /*An example of logging event with name 'viewed screen1'
        Any specific event that occurs in the app can be recorded by using the method Zinteract.logEvent().
        A string parameter can be passed to the method naming the event.*/
        Zinteract.logEvent("viewed screen1");
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void sendToActivity2(View view){
        Intent intent = new Intent(MainActivity.this, Activity2.class);
        startActivity(intent);
    }

    public void show(View view){
        findViewById(R.id.visibleText).setVisibility(View.VISIBLE);
        findViewById(R.id.hiddenText).setVisibility(View.VISIBLE);
    }
}
