package com.zemoso.zinteract.sdk;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zemoso.zinteract.ZinteractSampleApp.R;

import org.json.JSONObject;

/**
 * Created by praveen on 30/01/15.
 */
public class InAppNotification extends DialogFragment {

    int mNum;
    private static String campaignId;
    private static String title;
    private static String message;
    private static final String TAG = "com.zemoso.zinteract.sdk.inAppNotification";
    private static JSONObject promotionEvent = new JSONObject();

    /**
     * Create a new instance of InAppNotification, providing "num"
     * as an argument.
     */
    static InAppNotification newInstance(int num, String campaignId, String title, String message) {
        InAppNotification f = new InAppNotification();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putString("title", title);
        args.putString("campaignId", campaignId);
        args.putString("message", message);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
//        switch ((mNum-1)%6) {
//            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
//            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
//            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
//            case 4: style = DialogFragment.STYLE_NORMAL; break;
//            case 5: style = DialogFragment.STYLE_NORMAL; break;
//            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
//            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
//            case 8: style = DialogFragment.STYLE_NORMAL; break;
//        }
//        switch ((mNum-1)%6) {
//            case 4: theme = android.R.style.Theme_Holo; break;
//            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
//            case 6: theme = android.R.style.Theme_Holo_Light; break;
//            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
//            case 8: theme = android.R.style.Theme_Holo_Light; break;
//        }
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        campaignId = getArguments().getString("campaignId");
        title = getArguments().getString("title");
        message = getArguments().getString("message");
        mNum = getArguments().getInt("num");
        View v = null;
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
                Zinteract.logEvent(Constants.Z_CAMPAIGN_SHOW_LATER_EVENT, j);
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
                Zinteract.logEvent(Constants.Z_CAMPAIGN_DONOT_SHOW_EVENT, k);
            }
            catch (Exception e){
                Log.e(TAG, "Exception: " + e);
            }
            dismiss();
        }
    };

    View.OnClickListener closeHhandler = new View.OnClickListener() {
        public void onClick(View v) {
            dismiss();
        }
    };

    View.OnClickListener rateItHhandler = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                JSONObject f = new JSONObject();
                f.put("campaignId", campaignId);
                Zinteract.logEvent(Constants.Z_CAMPAIGN_RATE_EVENT, f);
            }
            catch (Exception e){
                Log.e(TAG, "Exception: " + e);
            }
            dismiss();
        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            Zinteract.markPromotionAsSeen(campaignId);
            promotionEvent.put("campaignId", campaignId);
            Zinteract.logEvent(Constants.Z_CAMPAIGN_VIEWED_EVENT, promotionEvent);
        }
        catch (Exception e){
            Log.e(TAG, "Exception: " + e);
        }
    }
}
