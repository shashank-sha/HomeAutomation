package com.zemosolabs.zinteract.interfaces;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zemosolabs.zinteract.sdk.Zinteract;

import org.json.JSONObject;

/**
 * Created by vedaprakash on 3/3/15.
 */
public abstract class ZinteractInAppNotification extends DialogFragment {
    protected static int mNum;
    protected static String campaignId;
    protected static String title;
    protected static String message;
    private final String TAG = "customInAppNotification";

    /**
     * Create a new instance of InAppNotification, providing "num"
     * as an argument.
     */
    public static void customize(int num, String campaignId, String title, String message) {
        ZinteractInAppNotification.campaignId = campaignId;
        ZinteractInAppNotification.title = title;
        ZinteractInAppNotification.message = message;
        ZinteractInAppNotification.mNum = num;
    }

    //Include super.onCreate() if overriding this method in the concrete class.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
    }
    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                                      Bundle savedInstanceState);

    //Include super.dismiss() if overriding this method in the concrete class.
    @Override
    public void dismiss() {
        super.dismiss();
    }

    //Include super.onPause() if overriding this method in the concrete class.
    @Override
    public void onPause() {
        super.onPause();
        Zinteract.updatePromotionAsSeen(campaignId);
    }
}
