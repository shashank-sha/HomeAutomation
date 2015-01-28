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

    protected static String getUserProperty(Context context,String key, String defaultValue){
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    protected static void setUserProperty(Context context,String key, String value){
        getSharedPreferences(context).edit().putString(key, value).apply();
    }

    protected static Map<String,?> getAllUserProperties(Context context,String key, String value){
        return getSharedPreferences(context).getAll();
    }

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(
                Constants.Z_SHARED_PREFERENCE_USER_PROPERTIES_FILE_NAME, Context.MODE_PRIVATE);
    }
}
