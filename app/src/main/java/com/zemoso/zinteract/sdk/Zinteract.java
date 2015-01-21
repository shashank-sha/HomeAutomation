package com.zemoso.zinteract.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import java.util.UUID;

/**
 * Created by praveen on 19/01/15.
 */
public class Zinteract {

    public static final String TAG = "com.zemoso.zinteract";
    private static Context context;
    private static String apiKey;
    private static String userId;
    private static String deviceId;
    private static String advertisingId;
    private static String versionName;
    private static String osName;
    private static String osVersion;
    private static String brand;
    private static String manufacturer;
    private static String model;
    private static String carrier;
    private static String language;

    private static Zinteract Zinteract = null;

    private Zinteract(){

    }

    public static void initializeWithContextAndKey(Context context, String apiKey) {
        initialize(context, apiKey, null);
    }

    public synchronized static void initialize(Context context, String apiKey, String userId) {
        if (context == null) {
            Log.e(TAG, "Application context cannot be null in initializeWithContextAndKey()");
            return;
        }
        if (TextUtils.isEmpty(apiKey) || apiKey == null) {
            Log.e(TAG, "Application apiKey cannot be null or blank in initializeWithContextAndKey()");
            return;
        }
        if (Zinteract == null) {
            Zinteract.setContext(context.getApplicationContext());
            Zinteract.setApiKey(apiKey);

            if(userId == null){
                userId = getUUID();
            }
            setUserId(userId);

        }
    }

    private static SharedPreferences getSharedPreferences(){
        SharedPreferences preferences = context.getSharedPreferences(
                Constants.Z_SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return  preferences;
    }

    private static void setUserId(String userId){
        Zinteract.userId = userId;
        getSharedPreferences().edit().putString(Constants.Z_USER_ID_KEY, userId).commit();
    }

    private static String getUUID(){
        return UUID.randomUUID().toString();//TODO cutomize it
    }

    private static void setContext(Context context){
        Zinteract.context = context;
    }

    private static void setApiKey(String apiKey){
        Zinteract.apiKey = apiKey;
    }
}
