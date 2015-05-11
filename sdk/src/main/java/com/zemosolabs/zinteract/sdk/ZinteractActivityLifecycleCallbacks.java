package com.zemosolabs.zinteract.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by praveen on 30/01/15.
 */
public class ZinteractActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    static Activity currentActivity;
    private ScreenEditor UIEditor;
    private ShakeListener shakeListener=null;
    TripleTapListener tripleTapListener;

    public ZinteractActivityLifecycleCallbacks() {

    }

    @Override
    public void onActivityStarted(final Activity activity) {

    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //activity.getIntent()
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Zinteract._endSession();
        UIEditor.purge();
        if(shakeListener!=null) {
            shakeListener.purge();
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

    @Override
    public void onActivityResumed(Activity activity) {
        if(activity.getClass().getCanonicalName().equals(activity.getPackageManager()
                .getLaunchIntentForPackage(activity.getPackageName()).getComponent().getClassName())) {
            if(currentActivity==null||activity!=currentActivity) {
                String campaignId = activity.getIntent()
                        .getStringExtra(Constants.Z_BUNDLE_KEY_PUSH_NOTIFICATION_CAMPAIGN_ID);
                if (campaignId != null && !campaignId.isEmpty()) {
                    Zinteract.updatePromotionAsSeen(campaignId);
                    Log.i("PushNotificationViewed", campaignId);
                }
            }
        }
        PackageManager pm = activity.getPackageManager();
        String name = activity.getComponentName().getClassName();
        String label = null;
        try {
            label = pm.getActivityInfo(activity.getComponentName(),0).loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ActivityDetails","PackageManager Not Found in ActivityLifeCycles",e);
        }
        Zinteract.updateActivityDetails(label,name);

        currentActivity = activity;
        if(Zinteract.isDebuggingOn()) {
            shakeListener = ShakeListener.getInstance();
            shakeListener.initialize();
            tripleTapListener= new TripleTapListener();
            activity.getWindow().getDecorView().setOnTouchListener(tripleTapListener);
        }
        Zinteract._startSession(activity);
        UIEditor = ScreenEditor.getInstance(activity);
        UIEditor.edit();

    }

    @Override
    public void onActivityStopped(Activity activity) { }
}
