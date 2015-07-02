package com.zemosolabs.zetarget.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by praveen on 30/01/15.
 */
public class ZeTargetActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    static Activity currentActivity;
    private ScreenEditor UIEditor;
    private ShakeListener shakeListener=null;
    TripleTapListener tripleTapListener;

    public ZeTargetActivityLifecycleCallbacks() {

    }

    @Override
    public void onActivityStarted(final Activity activity) {
        currentActivity = activity;
        ZeTarget.currentActivity = activity;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Intent startingIntent = activity.getIntent();
        if(startingIntent.hasExtra(Constants.Z_EVENT_TYPE)){
            if(startingIntent.getStringExtra(Constants.Z_EVENT_TYPE).equals(Constants.Z_CAMPAIGN_VIEWED_EVENT)){
                String campaignId = startingIntent.getStringExtra("campaignId");
                if(startingIntent.getStringExtra(Constants.Z_CAMPAIGN_TYPE).equals(Constants.Z_CAMPAIGN_TYPE_GEOCAMPAIGN)){
                    String geofenceId = startingIntent.getStringExtra("geofenceId");
                    JSONObject properties = new JSONObject();
                    try {
                        properties.put("campaignId",campaignId);
                        properties.put("geofenceId",geofenceId);
                    } catch (JSONException e) {
                        Log.e("CAMPAIGN VIEWED LOG","GeoNotification failed",e);
                    }
                    ZeTarget.logEvent(Constants.Z_CAMPAIGN_VIEWED_EVENT, properties);
                }else if(startingIntent.getStringExtra(Constants.Z_CAMPAIGN_TYPE).equals(Constants.Z_CAMPAIGN_TYPE_SIMPLE_EVENT_CAMPAIGN)){
                    JSONObject properties = new JSONObject();
                    try {
                        properties.put("campaignId",campaignId);
                    } catch (JSONException e) {
                        Log.e("CAMPAIGN VIEWED LOG","SimpleEvent Notification failed",e);
                    }
                    ZeTarget.logEvent(Constants.Z_CAMPAIGN_VIEWED_EVENT, properties);
                }
            }
        }

    }

    @Override
    public void onActivityPaused(Activity activity) {
        ZeTarget._endSession();
        if(UIEditor!=null) {
            UIEditor.purge();
        }
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
        String packageName = activity.getPackageName();
        PackageManager pm = activity.getPackageManager();

        Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
        if(launchIntent!=null) {
            ComponentName compName = launchIntent.getComponent();
            String launchingClassName = compName.getClassName();
            if (activity.getClass().getCanonicalName().equals(launchingClassName)) {
                if (currentActivity == null || activity != currentActivity) {
                    String campaignId = activity.getIntent()
                            .getStringExtra(Constants.Z_BUNDLE_KEY_PUSH_NOTIFICATION_CAMPAIGN_ID);
                    if (campaignId != null && !campaignId.isEmpty()) {
                        ZeTarget.updatePromotionAsSeen(campaignId);
                        Log.i("PushNotificationViewed", campaignId);
                    }
                }
            }
        }
        /*PackageManager pm = activity.getPackageManager();*/
        String name = activity.getComponentName().getClassName();
        String label = null;
        try {
            label = pm.getActivityInfo(activity.getComponentName(),0).loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ActivityDetails","PackageManager Not Found in ActivityLifeCycles",e);
        }
        ZeTarget.updateActivityDetails(label, name);
        ZeTarget._startSession(activity);

        if(ZeTarget.isDebuggingOn()) {
            shakeListener = ShakeListener.getInstance();
            shakeListener.initialize();
            tripleTapListener= new TripleTapListener();
            activity.getWindow().getDecorView().setOnTouchListener(tripleTapListener);
        }

        UIEditor = ScreenEditor.getInstance(activity);
        UIEditor.edit();

    }

    @Override
    public void onActivityStopped(Activity activity) { }
}
