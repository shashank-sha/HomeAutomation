package com.zemoso.zinteract.sdk;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by praveen on 21/01/15.
 */
public class UserProperties {

//    private static UserProperties userProperties;
//
//    private UserProperties(){
//
//    }
//
//    public synchronized static UserProperties getUserProperties(){
//        if(userProperties == null){
//            userProperties = new UserProperties();
//        }
//
//        return userProperties;
//    }

    public String getUserProperty(Context context,String key, String defaultValue){
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(key, defaultValue);
    }

    public void setUserProperty(Context context,String key, String value){
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(key, value).apply();
    }

    public Map<String,?> getAllUserProperties(Context context,String key, String value){
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getAll();
    }

    private SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(
                Constants.Z_SHARED_PREFERENCE_USER_PROPERTIES_FILE_NAME, Context.MODE_PRIVATE);
    }
}
