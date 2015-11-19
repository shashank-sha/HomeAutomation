package com.zemosolabs.zetarget.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.util.Log;

/**
 * Created by sudhanshu on 9/10/15.
 */
public class ZContext extends ContextWrapper {

    String TAG ="com.zemos.zint.sdk.zint";

    public ZContext(Context ctx) {
        super(ctx);
    }

    private String activityClassName = null;

    private Activity act =null;
    //private ZResources swappedOne = null;

    private ZResourcesNew swappedOne = null;

    public void setActivityClassName(Activity act) {
        activityClassName=act.getClass().getName();
        this.act=act;
    }

    @Override
    public Resources getResources() {

        if (swappedOne==null) {
            Resources orig = super.getResources();
            swappedOne = new ZResourcesNew(getAssets(),orig.getDisplayMetrics(),orig.getConfiguration());
            swappedOne.setActivityClassName(activityClassName,act);

        }
        Log.d(TAG,"method getResources called for" + getClass().getName());
        return swappedOne;
    }



}
