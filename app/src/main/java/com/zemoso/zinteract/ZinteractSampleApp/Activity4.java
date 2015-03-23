package com.zemoso.zinteract.ZinteractSampleApp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zemosolabs.zinteract.sdk.Zinteract;


public class Activity4 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity4);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity4, menu);
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
        Zinteract.logEvent("view screen4");
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void sendToActivity5(View view)
    {
        Zinteract.logEvent("clicked to view screen5");
        Intent intent = new Intent(Activity4.this, Activity5.class);
        startActivity(intent);
    }

    public void sendToActivity3(View view)
    {
        Zinteract.logEvent("clicked to view screen3");
        Intent intent = new Intent(Activity4.this, Activity3.class);
        startActivity(intent);
    }
}
