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

    private final String TAG ="ZeTarget.ZContext";

    public ZContext(Context ctx, String pName) {
        super(ctx);
        context = ctx;
        packageName = pName;
    }

    private String activityClassName = null;
    private static Context context;
    private static String packageName;

    private Activity act =null;
    //private ZResources swappedOne = null;

    private static ZResources swappedOne = null;

    public void setActivityClassName(Activity act) {
        activityClassName=act.getClass().getName();
        this.act=act;
    }

    @Override
    public Resources getResources() {

        if (swappedOne==null) {
            Resources orig = super.getResources();
            swappedOne = new ZResources(getAssets(),orig.getDisplayMetrics(),orig.getConfiguration(),orig,context,packageName);
            //swappedOne.setActivityClassName(activityClassName,act);

        }
        Log.d(TAG,"method getResources called for" + getClass().getName());
        return swappedOne;
    }

}
