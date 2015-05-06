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
    private Float grandTotal=0.00f;
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
        grandTotal = prefs.getFloat("grandTotal",0.00f);
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
        getSharedPreferences("PurchasePrefs",Activity.MODE_PRIVATE).getFloat("grandTotal",0.00f);
    }

    @Override
    public void onPause(){
        getSharedPreferences("PurchasePrefs",Activity.MODE_PRIVATE).edit().putFloat("grandTotal",grandTotal).apply();
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
                String grandTotalString = (String)((TextView)findViewById(R.id.grandTotalValue)).getText();
                JSONObject purchaseDetails = new JSONObject();
                try {
                    purchaseDetails.put("grand_total",Double.valueOf(grandTotalString));
                } catch (JSONException e) {
                    Log.e("Purchase","Activity 4 update of grand total failed",e);
                }
                Zinteract.logPurchaseCompletedEvent(purchaseDetails);
                grandTotal = 0.00f;
                ((TextView)findViewById(R.id.grandTotalValue)).setText(df.format(grandTotal));
                break;
            case R.id.buttonAddToCart:
                grandTotal += (float)(Math.random()*5000);
                ((TextView)findViewById(R.id.grandTotalValue)).setText(df.format(grandTotal));
                Zinteract.logPurchaseAttempted();
                break;
            case R.id.buttonRemove:
                grandTotal = 0.00f;
                ((TextView)findViewById(R.id.grandTotalValue)).setText(df.format(grandTotal));
                break;
            default:
                break;
        }
    }
}
