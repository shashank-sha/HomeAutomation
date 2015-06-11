import android.content.Context;
import android.content.SharedPreferences;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Pair;

import com.zemoso.zinteract.ZinteractSampleApp.MainActivity;
import com.zemosolabs.zinteract.sdk.ZTarget;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowZoomButtonsController;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.tester.org.apache.http.HttpRequestInfo;
import org.robolectric.tester.org.apache.http.RequestMatcher;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by vedaprakash on 17/3/15.
 */
@Config(reportSdk = 18,emulateSdk = 18)
@RunWith(CustomRobolectricRunner.class)
public class TestForActivityResume {
    DbHelper sqLiteOpenHelper;

    Long sessionId;

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
        sqLiteOpenHelper = DbHelper.getDatabaseHelper(Robolectric.application);
        assertEquals(0, sqLiteOpenHelper.getEventCount());
        assertEquals(0, sqLiteOpenHelper.getUserProperties().length());


        // Confirm the values of deviceId, UserId, and firstTimeCheck are not stored in the
        // before the main activity gets created for the first time.

        sharedPreferences = Robolectric.application.
                getSharedPreferences(Constants.Z_SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        String endSessionId = sharedPreferences.getString(Constants.Z_PREFKEY_LAST_END_SESSION_ID,"notInit");
        String endSessionTime = sharedPreferences.getString(Constants.Z_PREFKEY_LAST_END_SESSION_TIME,"notInit");
        assertEquals("notInit",endSessionId);
        assertEquals("notInit",endSessionTime);


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

        Robolectric.addHttpResponseRule(Constants.Z_EVENT_LOG_URL,simpleSuccessResponse);
        Robolectric.addHttpResponseRule(Constants.Z_USER_PROPERTIES_LOG_URL,simpleSuccessResponse);
        Robolectric.addHttpResponseRule(Constants.Z_DATASTORE_SYNCH_URL,simpleSuccessResponse);
        Robolectric.addHttpResponseRule(Constants.Z_START_SESSION_EVENT_LOG_URL,simpleSuccessResponse);
        Robolectric.addHttpResponseRule(Constants.Z_INIT_LOG_URL,simpleSuccessResponse);
        assertTrue(Robolectric.getFakeHttpLayer().isInterceptingHttpRequests());
        assertTrue(Robolectric.getFakeHttpLayer().hasResponseRules());
    }

    @Test
    public void testSessionPerformance() throws Exception{
        test1();
        test2();
        test3();



        /*Button button= (Button)activity.findViewById(R.id.button);
        button.performClick();
        assertEquals(new Intent(activity, Activity2.class),Robolectric.shadowOf(activity).getNextStartedActivity());
        activity.onPause();
        Activity2 activity2 = Robolectric.buildActivity(Activity2.class).create().start().resume().get();
        assertTrue(Robolectric.httpRequestWasMade(Constants.Z_DATASTORE_SYNCH_URL));
        activity.finish();*/
    }

    private void test1(){
// first test simple resume activity and pause without logging any events. End Result should be two
// calls to Z_EVENT_LOG_URL and one
        MainActivity activity= Robolectric.buildActivity(MainActivity.class).create().start().resume().visible().get();
        executeEventsOnLogAndHttpWorkers();
        assertTrue(numberOfEventsInDb()==1);
        activity.onPause();
        executeEventsOnLogAndHttpWorkers();
        executePendingEventsOnLogAndHttpWorkers(Constants.Z_EVENT_UPLOAD_PERIOD_MILLIS + 1000);
        executeEventsOnLogAndHttpWorkers();
        executePendingEventsOnLogAndHttpWorkers(Constants.Z_MIN_TIME_BETWEEN_SESSIONS_MILLIS+1000);

        //No call to the event_log_url should be made.
        assertTrue(numberOfHttpRequestsMadeTo(Constants.Z_EVENT_LOG_URL)==2);

        //Updating of the shared Preference file should be done.
        assertFalse(endSessionInitialised());

        //The events are cleared from Db
        assertTrue(numberOfEventsInDb()==0);
        activity.finish();

        //Value for sessionId created
        assertTrue(sessionIdCreated());
    }

    private void test2(){
// second test: resume activity wait for 15 seconds and check if call to Z_EVENT_LOG_URL has been made
// and database cleaned and a value for sessionId created
        printTheServerCalls();
        Robolectric.getFakeHttpLayer().clearRequestInfos();
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().visible().get();
        executeEventsOnLogAndHttpWorkers();
        assertTrue(numberOfEventsInDb()==1);
        executePendingEventsOnLogAndHttpWorkers(Constants.Z_EVENT_UPLOAD_PERIOD_MILLIS+2);
        executeEventsOnLogAndHttpWorkers();

        //No call to the event_log_url should be made.
        assertTrue(numberOfHttpRequestsMadeTo(Constants.Z_EVENT_LOG_URL)==1);

        //Updating of the shared Preference file should be done.
        assertFalse(endSessionInitialised());

        //The events are cleared from Db
        assertTrue(numberOfEventsInDb()==0);
        Long prevOpenSessionId = sessionId;
        //Value for sessionId created
        assertTrue(sessionIdCreated());
        assertNotEquals(prevOpenSessionId, sessionId);
        activity.onPause();
        activity.finish();
        executeEventsOnLogAndHttpWorkers();
        executePendingEventsOnLogAndHttpWorkers(Constants.Z_MIN_TIME_BETWEEN_SESSIONS_MILLIS+1000);
        executeEventsOnLogAndHttpWorkers();
    }

    private void test3(){
// test to check that as soon as the threshold limit of Db events is reached, an upload is performed
        printTheServerCalls();
        Robolectric.getFakeHttpLayer().clearRequestInfos();
        MainActivity activity= Robolectric.buildActivity(MainActivity.class).create().start().resume().visible().get();
        executeEventsOnLogAndHttpWorkers();
        assertTrue(numberOfEventsInDb()==1);
        int count = numberOfEventsInDb();

        do{
            ZTarget.logEvent("Event No." + Integer.valueOf(count));
            count++;
            System.out.println("count="+count);
            executeEventsOnLogAndHttpWorkers();
            assertTrue(numberOfEventsInDb()==count);
        }while(count<Constants.Z_EVENT_UPLOAD_THRESHOLD);

        assertTrue(numberOfEventsInDb()==Constants.Z_EVENT_UPLOAD_THRESHOLD-1);

        assertTrue(numberOfEventsInDb()==0);
        System.out.println(Constants.Z_EVENT_UPLOAD_THRESHOLD+","+count);
        assertTrue(count==Constants.Z_EVENT_UPLOAD_THRESHOLD);
        activity.finish();
    }

    private Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return t;
        }
        return null;
    }

    private boolean sessionIdCreated(){
        sessionId = sharedPreferences.getLong(Constants.Z_PREFKEY_LAST_SESSION_TIME,-1);
        if(sessionId==-1){
            return false;
        }
        return true;
    }

    private int numberOfHttpRequestsMadeTo(String url){
        int count=0;
        List <HttpRequestInfo> requestInfos  = Robolectric.getFakeHttpLayer().getSentHttpRequestInfos();
        for(HttpRequestInfo requestInfo:requestInfos){
            if(requestInfo.getHttpRequest().getRequestLine().getUri().equalsIgnoreCase(url)){
                count++;
            }
        }
        //System.out.println(count);
        return count;
    }


    private boolean endSessionInitialised(){
        Long endSessionId = sharedPreferences.getLong(Constants.Z_PREFKEY_LAST_END_SESSION_ID,-1);
        Long endSessionTime = sharedPreferences.getLong(Constants.Z_PREFKEY_LAST_END_SESSION_TIME, -1);
        if(endSessionId==-1||endSessionTime==-1){
            return false;
        }
        System.out.println(endSessionId+", "+endSessionTime);
        return true;
    }

    private void executeEventsOnLogAndHttpWorkers(){
        HandlerThread httpWorker = (HandlerThread)getThreadByName("httpWorker");
        HandlerThread logWorker = (HandlerThread)getThreadByName("logWorker");
        assertTrue(logWorker.isAlive());
        assertTrue(httpWorker.isAlive());
        ShadowLooper logWorkingLooper = Robolectric.shadowOf(logWorker.getLooper());
        assertFalse(logWorkingLooper.hasQuit());
        logWorkingLooper.idle();
        ShadowLooper httpWorkingLooper = Robolectric.shadowOf(httpWorker.getLooper());
        assertFalse(httpWorkingLooper.hasQuit());
        httpWorkingLooper.idle();
    }

    private void executePendingEventsOnLogAndHttpWorkers(long millis){
        HandlerThread httpWorker = (HandlerThread)getThreadByName("httpWorker");
        HandlerThread logWorker = (HandlerThread)getThreadByName("logWorker");
        assertTrue(logWorker.isAlive());
        assertTrue(httpWorker.isAlive());
        ShadowLooper logWorkingLooper = Robolectric.shadowOf(logWorker.getLooper());
        assertFalse(logWorkingLooper.hasQuit());
        logWorkingLooper.idle(millis + 2);
        ShadowLooper httpWorkingLooper = Robolectric.shadowOf(httpWorker.getLooper());
        assertFalse(httpWorkingLooper.hasQuit());
        httpWorkingLooper.idle(millis + 2);
    }

    private void printTheServerCalls(){
        List<HttpRequestInfo> requestInfos = Robolectric.getFakeHttpLayer().getSentHttpRequestInfos();
        for(HttpRequestInfo requestInfo:requestInfos){
            System.out.println(requestInfo.getHttpRequest().getRequestLine().getUri());
        }
    }

    private void printEventsInDb() {
        Pair eventPair = null;
        try {
            eventPair = sqLiteOpenHelper.getEvents(-1,-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray events = (JSONArray)eventPair.second;
        System.out.println(events.length());
        System.out.println(events.toString());
    }

    private int numberOfEventsInDb(){
        Pair eventPair = null;
        try {
            eventPair = sqLiteOpenHelper.getEvents(-1,-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray events = (JSONArray)eventPair.second;
        System.out.println(events.toString());
        System.out.println(events.length());
        return  events.length();
    }

}

