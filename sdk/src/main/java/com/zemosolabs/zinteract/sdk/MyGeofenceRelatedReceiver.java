package com.zemosolabs.zinteract.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class MyGeofenceRelatedReceiver extends BroadcastReceiver {
    private final static String TAG = "zint.GeofenceReceiver";
    private final static String locationModeOff = "com.zemosolabs.zinteract.locationModeOff";
    public MyGeofenceRelatedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int locMode=-100;
        if(intent!=null) {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
                if (intent.getAction().equals("android.location.MODE_CHANGED")) {
                   /* Log.i(TAG,"BuildVersion > Kitkat and MODE_CHANGED");*/
                    try {
                        locMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                    } catch (Settings.SettingNotFoundException e) {
                        Log.e(TAG, "Settings Couldn't be found", e);
                    }
                    Log.i(TAG,Integer.valueOf(locMode).toString());
                    if (locMode == Settings.Secure.LOCATION_MODE_OFF) {
                        CommonUtils.getSharedPreferences(context).edit().putBoolean(locationModeOff, true).apply();
                    } else if (CommonUtils.getSharedPreferences(context).getBoolean(locationModeOff, true)) {
                       /* Log.i(TAG,"locationModeOn");
                        Toast.makeText(context,"GPS_ON",Toast.LENGTH_LONG).show();*/
                        addGeoFences(context);
                        CommonUtils.getSharedPreferences(context).edit().putBoolean(locationModeOff,false).apply();
                    }
                }
            }else{
                if(intent.getAction().equals("android.location.PROVIDERS_CHANGED")){
                    LocationManager locManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
                    if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                            ||locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                        addGeoFences(context);
                    }
                }
            }
            if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
                addGeoFences(context);
            }
        }
    }

    private void addGeoFences(Context context) {
        Intent intentToHandleCampaigns = new Intent(context,CampaignHandlingService.class);
        intentToHandleCampaigns.putExtra("action",Constants.Z_INTENT_EXTRA_CAMPAIGNS_ACTION_KEY_VALUE_UPDATE_CAMPAIGNS);
        intentToHandleCampaigns.putExtra("type",Constants.Z_CAMPAIGN_TYPE_GEOCAMPAIGN);
        context.startService(intentToHandleCampaigns);
    }
}
