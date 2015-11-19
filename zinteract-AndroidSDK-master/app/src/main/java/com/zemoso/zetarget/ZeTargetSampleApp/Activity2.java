package com.zemoso.zetarget.ZeTargetSampleApp;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zemosolabs.zetarget.sdk.ZeTarget;


public class Activity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*Inflate the menu; this adds items to the action bar if it is present.*/
        getMenuInflater().inflate(R.menu.menu_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*Handle action bar item clicks here. The action bar will
        automatically handle clicks on the Home/Up button, so long
        as you specify a parent activity in AndroidManifest.xml.*/
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

        /*Example usage of the user properties.
        Once the userproperty key and value are defined with ZeTarget.setUserProperty as in the MainActivity,
        They String 'value' can be extracted by using the 'key'.*/

        String userFName = ZeTarget.getUserProperty("fname", "Friend");
        String userLName = ZeTarget.getUserProperty("lname", "");
        String message = "Hello " + userFName + " " + userLName+"!!!"+" How are you doing?";
        tV.setText(message);

        /*Example logging event with name 'viewed screen2'*/
        ZeTarget.logEvent("viewed screen2");

        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void sendToActivity3(View view) {
        Intent intent = new Intent(Activity2.this, Activity3.class);
        startActivity(intent);
    }

    public void sendToActivity1(View view)
    {
        Intent intent = new Intent(Activity2.this, MainActivity.class);
        startActivity(intent);
    }

    public void showFragment(View view) {
        FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        BlankFragment frag = new BlankFragment();
        fTrans.replace(android.R.id.content,frag).commit();
    }
}
