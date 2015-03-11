package com.zemosolabs.zinteract.user_interfaces;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.zemosolabs.zinteract.sdk.Zinteract;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vedaprakash on 3/3/15.
 */
public abstract class ZinteractInAppNotification extends DialogFragment {


     /**
     * Customize InAppNotification, providing Context "context",String "campaignId",JSONOjbect "template"
     * as arguments.
     *
     * JSONObject template structure:
     *
     *
     */
    public abstract void customize(Context context, String campaignId, JSONObject template);

    //Include super.onCreate() if overriding this method in the concrete class.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
    }
    //use the string fields title and message in the views where title and message need to be displayed.
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
    }
}
