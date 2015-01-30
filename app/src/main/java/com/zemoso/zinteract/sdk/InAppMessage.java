package com.zemoso.zinteract.sdk;

import com.zemoso.zinteract.sdk.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.zemoso.zinteract.ZinteractSampleApp.R;

import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class InAppMessage extends Activity {

    private static String campaignId;
    private static final String TAG = "com.zemoso.zinteract.sdk.inAppMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.in_app);
        setContentView(R.layout.activity_in_app_message);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        campaignId = intent.getStringExtra("campaignId");

        TextView messageView = (TextView) findViewById(R.id.textView2);
        //TextView titleView = (TextView) findViewById(R.id.title);
        setTitle(title);

        //titleView.setText(title);
        messageView.setText(message);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();

        try {
            DbHelper dbHelper = DbHelper.getDatabaseHelper(getApplicationContext());
            dbHelper.markPromotionAsSeen(campaignId);
            JSONObject promotionEvent = new JSONObject();
            promotionEvent.put("campaignId", campaignId);
            Zinteract.logEvent("promotion", promotionEvent);
        }
        catch (Exception e){
            Log.e(TAG, "Exception: " + e);
        }
        finish();
    }
}
