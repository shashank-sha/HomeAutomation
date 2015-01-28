package com.zemoso.zinteract.sdk;

/**
 * Created by praveen on 28/01/15.
 */
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.ApplicationTestCase;
import android.test.mock.MockContext;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ZinteractTest extends ApplicationTestCase<Application> {
    public ZinteractTest() {
        super(Application.class);
    }

    private static final String TAG = "com.zemoso.zinteract.sdk.ZinteractTest";

    public void setUp(){
        createApplication();
        getContext().getSharedPreferences(Constants.Z_SHARED_PREFERENCE_USER_PROPERTIES_FILE_NAME,Context.MODE_PRIVATE).edit().clear().commit();
        getContext().getSharedPreferences(Constants.Z_SHARED_PREFERENCE_DATASTORE_FILE_NAME,Context.MODE_PRIVATE).edit().clear().commit();

        getContext().getSharedPreferences(Constants.Z_SHARED_PREFERENCE_FILE_NAME,Context.MODE_PRIVATE).edit().clear().commit();

        Zinteract.initializeWithContextAndKey(getContext(),"TestAndroidAPIKey");
    }

    public void tearDown(){
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
        assertEquals("default",Zinteract.getUserProperty(key,"default"));
        Zinteract.setUserProperty("firstname", value);
        assertEquals(value,Zinteract.getUserProperty(key,"default"));
    }

    public void testLogEvent(){
        String eventName = "click";
        Zinteract.logEvent(eventName);
        DbHelper dbHelper = DbHelper.getDatabaseHelper(getContext());
        try {
            Pair<Long,JSONArray> p = dbHelper.getEvents(10,-1);
            assertEquals(1,p.second.length());
            //assertEquals(eventName,p.second.getJSONObject(0).getString("eventName"));
        }
        catch (Exception e){
            Log.e(TAG, "Exception :" + e);
        }

    }
}
