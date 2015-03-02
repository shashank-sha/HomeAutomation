package com.zemosolabs.zinteract.sdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by praveen on 30/01/15.
 */
public class ZinteractActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    public ZinteractActivityLifecycleCallbacks() {
    }

    @Override
    public void onActivityStarted(Activity activity) {
//        if (!activity.isTaskRoot()) {
//            return; // No checks, no nothing.
//        }

    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) { }

    @Override
    public void onActivityPaused(Activity activity) {
        Zinteract._endSession();
    }

    @Override
    public void onActivityDestroyed(Activity activity) { }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

    @Override
    public void onActivityResumed(Activity activity) {
        Zinteract._startSession(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) { }


}
