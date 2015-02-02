package com.zemoso.zinteract.ZinteractSampleApp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zemoso.zinteract.sdk.Zinteract;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Zinteract.initializeWithContextAndKey(getApplicationContext(),"43eb-a23d-0106af662c83","914500168484");
        Zinteract.setUserProperty("firstname","John");
        Zinteract.setUserProperty("lastname","Doe");
        Zinteract.setUserProperty("age","28");
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
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void sendToActivity2(View view)
    {
        Intent intent = new Intent(MainActivity.this, Activity2.class);
        startActivity(intent);
    }
}
