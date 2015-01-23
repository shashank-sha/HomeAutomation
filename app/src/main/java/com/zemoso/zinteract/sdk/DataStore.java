package com.zemoso.zinteract.sdk;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by praveen on 21/01/15.
 */
public class DataStore extends UserProperties{
    private static DataStore dataStore;

    private DataStore(){

    }

    public synchronized static DataStore getDataStore(){
        if(dataStore == null){
            dataStore = new DataStore();
        }
        return dataStore;
    }

    public String getData(Context context,String key, String defaultValue){
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(key, defaultValue);
    }

    public void setData(Context context,String key, String value){
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(key, value).commit();
    }

    public void setLastSyncTime(Context context,String value){
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(Constants.Z_PREFKEY_LAST_DATASTORE_SYNC_TIME, value).commit();
    }

    public String getLastSyncTime(Context context){
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(Constants.Z_PREFKEY_LAST_DATASTORE_SYNC_TIME, null);
    }

    public void setDataStoreVersion(Context context,String value){
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(Constants.Z_PREFKEY_LAST_DATASTORE_VERSION, value).commit();
    }

    public String getDataStoreVersion(Context context){
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(Constants.Z_PREFKEY_LAST_DATASTORE_VERSION, null);
    }

    public Map<String,?> getAllData(Context context){
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getAll();
    }

    private SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(
                Constants.Z_SHARED_PREFERENCE_DATASTORE_FILE_NAME, Context.MODE_PRIVATE);
    }
}
