package com.zemoso.zetarget.ZeTargetSampleApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.zemosolabs.zetarget.sdk.ZeTarget;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        /*Initialize ZeTarget with Context and key provided upon registration on ZeTarget website.
        Optionally the Google Api Project key and custom class for IN APP PROMOTION can be passed
        in as arguments.*/
        ZeTarget.enableDebugging();
        ZeTarget.initializeWithContextAndKey(getApplicationContext(),
                "87c0e3f5-94c9-42cd-a838-2bec61209507", "914500168484");

        /* Setting the user properties(optional).
         Provide the user properties which can be useful to segment users. Once the user properties
         are set through these methods, they can used to filter users through you ZeTarget account */

        ZeTarget.setUserProperty("fname", "John");
        ZeTarget.setUserProperty("lname", "Doe");
        ZeTarget.setUserProperty("age", "39");

        /*enableDebugging method should only be used for uploading screens for to the ZeTarget
         website for dynamic editing or to read the logs for troubleshooting */
        TextView myAwesomeTextView = (TextView)findViewById(R.id.visibleText);
        //myAwesomeTextView.setText(R.string.hello_world);
        setTitle(R.string.app_name);
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
        Any specific event that occurs in the app can be recorded by using the method ZeTarget.logEvent().
        A string parameter can be passed to the method naming the event.*/
        ZeTarget.logEvent("viewed screen1");
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

    @Override
    protected void attachBaseContext(Context ctx) {
        super.attachBaseContext(ZeTarget.attachBaseContext(ctx,this));
    }
}
