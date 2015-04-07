package com.zemosolabs.zinteract.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by praveen on 30/01/15.
 */
public class ZinteractActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private static ArrayList<Activity> activities = new ArrayList<Activity>();
    static Activity currentActivity;
    public ZinteractActivityLifecycleCallbacks() {

    }

    @Override
    public void onActivityStarted(final Activity activity) {

    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) { }

    @Override
    public void onActivityPaused(Activity activity) {
        if(activities.get(0)==activity){
            activities.remove(activity);
        }else{
            activities.remove(0);
        }

        Zinteract._endSession();
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

    @Override
    public void onActivityResumed(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        String name = activity.getComponentName().getClassName();
        String label = null;
        try {
            label = pm.getActivityInfo(activity.getComponentName(),0).loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Zinteract.updateActivityDetails(label,name);

        activities.add(0,activity);
        currentActivity = activity;

        TripleTapListener tripleTapListener = new TripleTapListener();
        activity.getWindow().getDecorView().setOnTouchListener(tripleTapListener);
        Zinteract._startSession(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) { }

    static ArrayList<Activity> getCurrentActivities(){
        return activities;
    }

}
