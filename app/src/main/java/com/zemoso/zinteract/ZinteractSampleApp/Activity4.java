package com.zemoso.zinteract.ZinteractSampleApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zemosolabs.zinteract.sdk.Zinteract;

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
        /*Inflate the menu; this adds items to the action bar if it is present.*/
        getMenuInflater().inflate(R.menu.menu_activity4, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();

        /*Example logging Event with name 'viewed screen4'*/
        Zinteract.logEvent("viewed screen4");
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
        /*Example logging Event 'clicked to view screen5'*/
        Zinteract.logEvent("clicked to view screen5");
        Intent intent = new Intent(Activity4.this, Activity5.class);
        startActivity(intent);
    }

    public void sendToActivity3(View view)
    {
        /*Example logging Event 'clicked to view screen3'*/
        Zinteract.logEvent("clicked to view screen3");
        Intent intent = new Intent(Activity4.this, Activity3.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonBuy:

                /*Example of logging purchase Completed event
                Purchase completed event with only the grandTotal is shown here.
                Other details such as currency, quantity,tax, shipping, orderId, receiptId, productSku
                etc can be logged in the same event using the corresponding methods provided in the sdk
                for logging purchase completed events.*/

                Zinteract.logPurchaseCompleted(grandTotal);
                grandTotal = 0.00;
                ((TextView)findViewById(R.id.grandTotalValue)).setText(df.format(grandTotal));
                break;
            case R.id.buttonAddToCart:

                /*Example of logging purchase Attempted event*/

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
