
import android.content.Context;

import android.content.SharedPreferences;

import android.os.HandlerThread;

import android.util.Log;
import android.widget.TextView;

import com.zemoso.zinteract.ZinteractSampleApp.Activity2;
import com.zemoso.zinteract.ZinteractSampleApp.MainActivity;
import com.zemoso.zinteract.ZinteractSampleApp.R;
import com.zemosolabs.zinteract.sdk.Zinteract;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import org.robolectric.shadows.ShadowLooper;

import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.tester.org.apache.http.HttpRequestInfo;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by vedaprakash on 16/3/15.
 */
@Config(reportSdk = 18,emulateSdk = 18)
@RunWith (CustomRobolectricRunner.class)

public class TesterClass {

    DbHelper sqLiteOpenHelper;

    ProtocolVersion httpProtocolVersion;
    HttpResponse simpleSuccessResponse;

    SharedPreferences sharedPreferences;

    String simpleSuccessHttpResponse ="{\"status\":\"success\"}";

    @Before
    public void setUp() {

        // Confirm the FakeHttpLayer is at its default state without and ResponseRules set.

        FakeHttpLayer fakeHttpLayer = Robolectric.getFakeHttpLayer();
        assertFalse(fakeHttpLayer.hasPendingResponses());
        assertFalse(fakeHttpLayer.hasRequestInfos());
        assertFalse(fakeHttpLayer.hasResponseRules());
        assertNull(fakeHttpLayer.getDefaultResponse());

        //Getting the database assert there is no database initially
        assertTrue(Robolectric.application.databaseList().length==0);
        sqLiteOpenHelper = DbHelper.getDatabaseHelper(Robolectric.application);
        assertEquals(0, sqLiteOpenHelper.getEventCount());
        assertEquals(0, sqLiteOpenHelper.getUserProperties().length());


        // Confirm the values of deviceId, UserId, and firstTimeCheck are not stored in the
        // before the main activity gets created for the first time.

        sharedPreferences = Robolectric.application.
                getSharedPreferences(Constants.Z_SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        Boolean firstTimeCheck = sharedPreferences.getBoolean(Constants.Z_PREFKEY_FIRSTTIME_FLAG,true);
        String deviceId = sharedPreferences.getString(Constants.Z_PREFKEY_DEVICE_ID,"notInit");
        String userId = sharedPreferences.getString(Constants.Z_PREFKEY_USER_ID,"notInit");
        assertTrue(firstTimeCheck);
        assertEquals("notInit",deviceId);
        assertEquals("notInit",userId);


        // Set up the HttpResponses for the Z_INIT_LOG_URL

        JSONObject simpleSuccess = null;
        try {
            simpleSuccess = new JSONObject(simpleSuccessHttpResponse);
        } catch (JSONException e) {
            Log.e("Testing", e.toString());
        }

        httpProtocolVersion = new ProtocolVersion("HTTP", 1, 1);
        simpleSuccessResponse = new BasicHttpResponse(httpProtocolVersion, 200, "OK");

        try {
            simpleSuccessResponse.setEntity(new StringEntity(simpleSuccess.toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Setting up and confirming Robolectric's FakeHttpLayer is in effect

        Robolectric.addHttpResponseRule(Constants.Z_INIT_LOG_URL,simpleSuccessResponse);
        Robolectric.addHttpResponseRule(Constants.Z_USER_PROPERTIES_LOG_URL,simpleSuccessResponse);
        assertTrue(Robolectric.getFakeHttpLayer().isInterceptingHttpRequests());
        assertTrue(Robolectric.getFakeHttpLayer().hasResponseRules());
    }



    @Test
    public void shouldInitialiseProperly() throws Exception {
        // Creating the MainActivity-entry to the Application

        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();

        // Checking that the deviceId, userId are initialized and the firstTimeCheck is disabled.

        String deviceId = sharedPreferences.getString(Constants.Z_PREFKEY_DEVICE_ID,"notInit");
        String userId = sharedPreferences.getString(Constants.Z_PREFKEY_USER_ID,"notInit");
        boolean firstTimeCheck = sharedPreferences.getBoolean(Constants.Z_PREFKEY_FIRSTTIME_FLAG,true);
        assertNotEquals("notInit",deviceId);
        assertNotEquals("notInit",userId);
        assertFalse(firstTimeCheck);

        //Checking if the httpWorker and logWorker Threads were invoked.
        HandlerThread httpWorker = (HandlerThread)getThreadByName("httpWorker");
        HandlerThread logWorker = (HandlerThread)getThreadByName("logWorker");
        assertTrue(logWorker.isAlive());
        assertTrue(httpWorker.isAlive());

        //Using ShadowLooper to execute tasks in logWorker and httpWorker which are not set with a delay
        ShadowLooper logWorkingLooper = Robolectric.shadowOf(logWorker.getLooper());
        assertFalse(logWorkingLooper.hasQuit());
        logWorkingLooper.idle();
        ShadowLooper httpWorkingLooper = Robolectric.shadowOf(httpWorker.getLooper());
        assertFalse(httpWorkingLooper.hasQuit());
        httpWorkingLooper.idle();

        //Checking if the Initialisation URL was called the first time the App was started
        List<HttpRequestInfo> sentRequests =
                Robolectric.getFakeHttpLayer().getSentHttpRequestInfos();
        assertTrue((Boolean) Robolectric.httpRequestWasMade(Constants.Z_INIT_LOG_URL));


        assertTrue(sentRequests.size() == 1);

        activity.finish();

        //Removing HttpRequestForInitialization.
        Robolectric.getFakeHttpLayer().clearRequestInfos();
        sentRequests = Robolectric.getFakeHttpLayer().getSentHttpRequestInfos();
        assertTrue(sentRequests.size() == 0);

        //Check the update of UserProperties to the server scheduled after a given period of time.
        // The activity ending should not have any effect.
        assertTrue(logWorker.isAlive());
        assertTrue(httpWorker.isAlive());
        assertFalse(logWorkingLooper.hasQuit());
        logWorkingLooper.idle(Constants.Z_USER_PROPS_UPLOAD_PERIOD_MILLIS+2);
        assertFalse(httpWorkingLooper.hasQuit());
        httpWorkingLooper.idle(Constants.Z_USER_PROPS_UPLOAD_PERIOD_MILLIS+2);

        sentRequests = Robolectric.getFakeHttpLayer().getSentHttpRequestInfos();
        assertTrue((Boolean) Robolectric.httpRequestWasMade(Constants.Z_USER_PROPERTIES_LOG_URL));

        assertTrue(sentRequests.size()==1);

        // Checking if the database is updated with userProperties
        JSONObject userProps = sqLiteOpenHelper.getUserProperties();
        assertEquals(3, userProps.length());
        assertEquals(0,sqLiteOpenHelper.getEventCount());
        assertEquals("John",userProps.getString("fname"));
        assertEquals("Doe",userProps.getString("lname"));
        assertEquals("39",userProps.getString("age"));


//Re-creating Activity

        //Checking if the device id and user id persist even after Activity lifecycles and not re initialized.
        //Also Z_INIT_LOG_URL must not be called only Z_USER_PROPERTIES_LOG_URL.


        firstTimeCheck = sharedPreferences.getBoolean(Constants.Z_PREFKEY_FIRSTTIME_FLAG,true);
        assertNotEquals("notInit",deviceId);
        assertNotEquals("notInit",userId);
        assertFalse(firstTimeCheck);

        activity = Robolectric.buildActivity(MainActivity.class).create().get();

        Zinteract.setUserProperty("fname","Jane");
        String deviceId2 = sharedPreferences.getString(Constants.Z_PREFKEY_DEVICE_ID,"notInit");
        String userId2 = sharedPreferences.getString(Constants.Z_PREFKEY_USER_ID,"notInit");
        firstTimeCheck = sharedPreferences.getBoolean(Constants.Z_PREFKEY_FIRSTTIME_FLAG,true);
        assertFalse(firstTimeCheck);
        assertEquals(deviceId, deviceId2);
        assertEquals(userId,userId2);
        logWorkingLooper.idle();
        httpWorkingLooper.idle();
        assertTrue(Robolectric.httpRequestWasMade(Constants.Z_USER_PROPERTIES_LOG_URL));
        assertFalse(Robolectric.httpRequestWasMade(Constants.Z_INIT_LOG_URL));
        assertTrue(Robolectric.getFakeHttpLayer().getSentHttpRequestInfos().size()==1);
        logWorkingLooper.idle(Constants.Z_EVENT_UPLOAD_PERIOD_MILLIS+2);
        httpWorkingLooper.idle(Constants.Z_EVENT_UPLOAD_PERIOD_MILLIS+2);
        assertTrue(Robolectric.httpRequestWasMade(Constants.Z_USER_PROPERTIES_LOG_URL));
        assertFalse(Robolectric.httpRequestWasMade(Constants.Z_INIT_LOG_URL));
        assertTrue(Robolectric.getFakeHttpLayer().getSentHttpRequestInfos().size()==2);


        //Verifying if the change to the userProperty reflected in db
        userProps = sqLiteOpenHelper.getUserProperties();
        assertEquals(3, userProps.length());
        assertEquals(0,sqLiteOpenHelper.getEventCount());
        assertEquals("Jane",userProps.getString("fname"));          //change
        assertEquals("Doe",userProps.getString("lname"));
        assertEquals("39",userProps.getString("age"));

        //Verifying if the change to the userProperty is updated to the new call to the Z_USER_PROPERTIES_LOG_URL
        HttpRequest requestToSetUserProps = findLatestHttpRequestTo(Constants.Z_USER_PROPERTIES_LOG_URL);
        assertTrue(requestToSetUserProps.getRequestLine().getMethod().equalsIgnoreCase("post"));
        HttpPost postRequestToUserProps = (HttpPost)requestToSetUserProps;
        JSONObject postParamsToUserProps = new JSONObject(EntityUtils.toString(postRequestToUserProps.getEntity())).getJSONObject("userProperties");
//        System.out.println(EntityUtils.toString(postRequestToUserProps.getEntity()));
        assertEquals("Jane", postParamsToUserProps.getString("fname"));
        assertEquals("Doe",postParamsToUserProps.getString("lname"));
        assertEquals("39",postParamsToUserProps.getString("age"));
        activity.finish();

        //Verifying if the change to the userProperty is updated to the UI in Activity2
        Activity2 activity2 = Robolectric.buildActivity(Activity2.class).create().start().resume().visible().get();
        TextView tV= (TextView)activity2.findViewById(R.id.userSpecificText);
        assertTrue(tV.getText().equals("Hello Jane Doe!!! How are you doing?"));
        activity2.finish();
        assertTrue(defaultParametersOkAllPostRequest());
    }
    public Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return t;
        }
        return null;
    }
    HttpRequest findLatestHttpRequestTo(String url){
        List <HttpRequestInfo> allRequests = Robolectric.getFakeHttpLayer().getSentHttpRequestInfos();
        HttpRequest requestHttp;
        HttpRequestInfo requiredInfo = null;
        for(HttpRequestInfo request:allRequests){
            if(request.getHttpRequest().getRequestLine().getUri().equalsIgnoreCase(url)){
                requiredInfo = request;
            }
//           System.out.println(url);
        }
        return requiredInfo.getHttpRequest();
    }
    boolean defaultParametersOkAllPostRequest(){
        List <HttpRequestInfo> allRequests = Robolectric.getFakeHttpLayer().getSentHttpRequestInfos();
        for(HttpRequestInfo requestInfo:allRequests){
            HttpRequest requestHttp = requestInfo.getHttpRequest();
            if(requestHttp.getRequestLine().getMethod().equalsIgnoreCase("post")){
                try {
                    JSONObject postParams = new JSONObject(EntityUtils.toString(((HttpPost) requestHttp).getEntity()));
                    if(!(postParams.has("apiKey") && postParams.has("deviceId") && postParams.has("userId")
                            && postParams.has("sdkId") && postParams.has("appVersion"))){
                        if(postParams.get("apiKey") == JSONObject.NULL || postParams.get("deviceId") == JSONObject.NULL
                                || postParams.get("userId") == JSONObject.NULL || postParams.get("sdkId") == JSONObject.NULL
                                || postParams.get("appVersion")==JSONObject.NULL){
                            return false;
                        }
                        return false;
                    }
                } catch (Exception e) {
                    System.out.println("Error in parsing JSON in line 282 while parsing for the request to"+
                            ((HttpPost) requestHttp).getURI());
                }
            }
        }
        return true;
    }

}
