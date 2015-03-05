package com.zemosolabs.zinteract.user_interfaces;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zemosolabs.zinteract.R;
import com.zemosolabs.zinteract.sdk.Zinteract;

import org.json.JSONObject;

/**
 * Created by praveen on 30/01/15.
 */
public class DefaultInAppNotification extends ZinteractInAppNotification {


    private static final String TAG = "InAppNotification";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("inside Notification","control came here");

        View v;
        if(mNum == 1){
            v = inflater.inflate(R.layout.activity_in_app_message, container, false);
            Button button = (Button)v.findViewById(R.id.close_button);
            button.setOnClickListener(closeHhandler);
        }
        else if(mNum == 2){
            v = inflater.inflate(R.layout.activity_in_app_rating, container, false);
            Button askLaterButton = (Button)v.findViewById(R.id.ask_later);
            askLaterButton.setOnClickListener(askMeLater);

            Button dontAskButton = (Button)v.findViewById(R.id.dont_ask);
            dontAskButton.setOnClickListener(dontAskMeAgain);

            Button rateItButton = (Button)v.findViewById(R.id.close_button);
            rateItButton.setOnClickListener(rateItHhandler);
        }
        else {
            v = inflater.inflate(R.layout.activity_in_app_message, container, false);
        }

        View tv = v.findViewById(R.id.title);
        ((TextView)tv).setText(title);

        View mv = v.findViewById(R.id.textView2);
        ((TextView)mv).setText(message);
        return v;
    }

    View.OnClickListener askMeLater = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                JSONObject j = new JSONObject();
                j.put("campaignId", campaignId);
                Zinteract.logEvent("ViewEventLater", j);
            }
            catch (Exception e){
                Log.e(TAG, "Exception: " + e);
            }

            dismiss();
        }
    };

    View.OnClickListener dontAskMeAgain = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                JSONObject k = new JSONObject();
                k.put("campaignId", campaignId);
                Zinteract.logEvent("DoNotAskMeAgain", k);
                Zinteract.updatePromotionAsSeen(campaignId);
            }
            catch (Exception e){
                Log.e(TAG, "Exception: " + e);
            }
            dismiss();
        }
    };

    View.OnClickListener closeHhandler = new View.OnClickListener() {
        public void onClick(View v) {
            Zinteract.updatePromotionAsSeen(campaignId);
            dismiss();
        }
    };

    View.OnClickListener rateItHhandler = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                JSONObject f = new JSONObject();
                f.put("campaignId", campaignId);
                Zinteract.logEvent("RateItClicked", f);
                Zinteract.updatePromotionAsSeen(campaignId);
            }
            catch (Exception e){
                Log.e(TAG, "Exception: " + e);
            }
            dismiss();
        }
    };

}
