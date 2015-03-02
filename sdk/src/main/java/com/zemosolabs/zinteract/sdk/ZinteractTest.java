package com.zemosolabs.zinteract.sdk;

/**
 * Created by praveen on 28/01/15.
 */
import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ZinteractTest extends ApplicationTestCase<Application> {
    public ZinteractTest() {
        super(Application.class);
    }

    private static final String TAG = "com.zemosolabs.zinteract.sdk.ZinteractTest";

    private DbHelper dbHelper;

    public void setUp(){
        createApplication();
        getContext().getSharedPreferences(Constants.Z_SHARED_PREFERENCE_DATASTORE_FILE_NAME,Context.MODE_PRIVATE).edit().clear().commit();

        getContext().getSharedPreferences(Constants.Z_SHARED_PREFERENCE_FILE_NAME,Context.MODE_PRIVATE).edit().clear().commit();
        dbHelper = DbHelper.getDatabaseHelper(getContext());
        Zinteract.initializeWithContextAndKey(getContext(),"TestAndroidAPIKey");
        Zinteract.enableDebugging();
    }

    public void tearDown(){
        getContext().deleteDatabase(Constants.Z_DB_NAME);
        terminateApplication();
    }

    public void testDefaultUserIdisSet(){
        assertNotNull(Zinteract.getUserId());
    }

    public void testCustomUserId(){
        String customUserId = "CustomUserId";
        Zinteract.setUserId(customUserId);
        assertEquals(customUserId,Zinteract.getUserId());
    }

    public void testUserProperty(){
        String value = "John";
        String key = "firstname";
        assertEquals("default", Zinteract.getUserProperty(key, "default"));
        Zinteract.setUserProperty("firstname", value);
        assertEquals(value,Zinteract.getUserProperty(key,"default"));
    }

    public void testLogEvent(){
        String eventName = "click";
        Zinteract.logEvent(eventName);

        try {
            Thread.sleep(1000);
            Pair<Long,JSONArray> p = dbHelper.getEvents(-1,-1);
            boolean clickEventFound = false;
            for(int i=0; i < p.second.length();i++){
                JSONObject json = p.second.getJSONObject(i);
                if(json.getString("eventName").equals(eventName)){
                    clickEventFound = true;
                    break;
                }
            }
            assertEquals(true,clickEventFound);
        }
        catch (Exception e){
            Log.e(TAG, "Exception :" + e);
        }

    }

    public void testDownloadPromotionsAndDataStore(){
        //TODO use mocked API response
        try {
            Zinteract.syncDataStore();
            Zinteract.checkPromotions();
            Thread.sleep(20000);
            JSONObject promotion = dbHelper.getPromotionforScreen("ViewController4");
            assertNotNull(promotion);
            assertEquals("app",Zinteract.getData("text","default"));
            assertEquals("55",Zinteract.getData("price","default"));
            assertEquals("66",Zinteract.getData("quantity","default"));
        }
        catch (Exception e){
            Log.e(TAG, "Exception :" + e);
        }
    }
}
