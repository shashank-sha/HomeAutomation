package com.zemoso.zinteract.ZinteractSampleApp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zemoso.zinteract.sdk.Zinteract;


public class Activity3 extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity3);

        TextView messageView = (TextView) findViewById(R.id.activity3_heading);
        messageView.setText(Zinteract.getData("text","Hello"));
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
        Zinteract.startSession(this);
        Zinteract.logEvent("view screen3");
    }

    @Override
    public void onPause(){
        super.onPause();
        Zinteract.endSession();
    }

    public void sendToActivity4(View view)
    {
        Zinteract.logEvent("clicked to view screen4");
        Intent intent = new Intent(Activity3.this, Activity4.class);
        startActivity(intent);
    }

    public void sendToActivity2(View view)
    {
        Zinteract.logEvent("clicked view screen2");
        Intent intent = new Intent(Activity3.this, Activity2.class);
        startActivity(intent);
    }
}
