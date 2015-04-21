package com.zemoso.zinteract.ZinteractSampleApp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zemosolabs.zinteract.sdk.Zinteract;


public class Activity2 extends Activity {

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

        TextView tV = (TextView)findViewById(R.id.userSpecificText);
        String userFName = Zinteract.getUserProperty("fname","Friend");
        String userLName = Zinteract.getUserProperty("lname","");
        String message = "Hello " + userFName + " " + userLName+"!!!"+" How are you doing?";
        tV.setText(message);
        Zinteract.logEvent("view screen2");
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
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

    public void showFragment(View view) {
        FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        BlankFragment frag = new BlankFragment();
        fTrans.replace(android.R.id.content,frag).commit();
    }
}
