package com.zemosolabs.zinteract.sdk;

import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by praveen on 30/01/15.
 */
public class ZinteractActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    public ZinteractActivityLifecycleCallbacks() {

    }

    @Override
    public void onActivityStarted(final Activity activity) {
        PackageManager pm = activity.getPackageManager();
        String name = activity.getComponentName().getClassName();
        String label = null;
        try {
            label = pm.getActivityInfo(activity.getComponentName(),0).loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Zinteract.updateActivityDetails(label,name);

    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) { }

    @Override
    public void onActivityPaused(Activity activity) {
        Zinteract._endSession();
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

    @Override
    public void onActivityResumed(Activity activity) {
        Zinteract._startSession(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) { }


}
