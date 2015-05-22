package com.zemoso.zinteract.ZinteractSampleApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zemosolabs.zinteract.sdk.Zinteract;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class Activity4 extends Activity implements View.OnClickListener {
    private Double grandTotal=0.00;
    private DecimalFormat df = new DecimalFormat("#.##");
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity4);
        Button addToCart = (Button)findViewById(R.id.buttonAddToCart);
        addToCart.setOnClickListener(this);
        Button buy = (Button)findViewById(R.id.buttonBuy);
        buy.setOnClickListener(this);
        Button removeFromCart = (Button)findViewById(R.id.buttonRemove);
        removeFromCart.setOnClickListener(this);
        prefs = getSharedPreferences("PurchasePrefs",Activity.MODE_PRIVATE);
        grandTotal = Double.valueOf(prefs.getString("grandTotal","0.00"));
        ((TextView)findViewById(R.id.grandTotalValue)).setText(df.format(grandTotal));
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
        grandTotal = Double.valueOf(getSharedPreferences("PurchasePrefs",Activity.MODE_PRIVATE).getString("grandTotal","0.00"));
        ((TextView)findViewById(R.id.grandTotalValue)).setText(df.format(grandTotal));
    }

    @Override
    public void onPause(){
        getSharedPreferences("PurchasePrefs",Activity.MODE_PRIVATE).edit().putString("grandTotal",grandTotal.toString()).apply();
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonBuy:
                /*if(Zinteract.robolectricTesting) {
                    System.out.println("APP: buy button clicked");
                }*/
                Zinteract.logPurchaseCompletedEvent(grandTotal);
                grandTotal = 0.00;
                ((TextView)findViewById(R.id.grandTotalValue)).setText(df.format(grandTotal));
                break;
            case R.id.buttonAddToCart:
                /*if(Zinteract.robolectricTesting) {
                    System.out.println("APP: add to cart button clicked");
                }*/
                grandTotal += (Math.random()*5000);
                ((TextView)findViewById(R.id.grandTotalValue)).setText(df.format(grandTotal));
                Zinteract.logPurchaseAttempted();
                break;
            case R.id.buttonRemove:
                grandTotal = 0.00;
                ((TextView)findViewById(R.id.grandTotalValue)).setText(df.format(grandTotal));
                break;
            default:
                break;
        }
    }
}
