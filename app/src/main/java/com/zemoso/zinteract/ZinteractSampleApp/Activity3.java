package com.zemoso.zinteract.ZinteractSampleApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zemosolabs.zinteract.sdk.ZeTarget;

import org.json.JSONException;
import org.json.JSONObject;


public class Activity3 extends Activity {
    private static final String TAG = "SampleApp.Activity3" ;
    long inTime;
    long outTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity3);

        TextView messageView = (TextView) findViewById(R.id.activity3_heading);
        messageView.setText(ZeTarget.getData("text", "Hello"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity3, menu);
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
        inTime = System.currentTimeMillis();
        // Example logging Event with name 'viewed screen3'
        ZeTarget.logEvent("viewed screen3");
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    /* This method is called when the button with text 'Screen 4' is clicked*/
    public void sendToActivity4(View view)
    {
        /*Example logging Event with name 'clicked to view screen4' and extra details*/
        outTime=System.currentTimeMillis();
        JSONObject timeSpent = new JSONObject();
        try {
            timeSpent.put("inTime",inTime);
            timeSpent.put("outTime",outTime);
        } catch (JSONException e) {
            Log.e(TAG, "log details exception", e);
        }

        ZeTarget.logEvent("clicked to view screen4", timeSpent);
        Intent intent = new Intent(Activity3.this, Activity4.class);
        startActivity(intent);
    }

    /* This method is called when the button with text 'Screen 2' is clicked*/
    public void sendToActivity2(View view)
    {
        Intent intent = new Intent(Activity3.this, Activity2.class);
        startActivity(intent);
    }
}
