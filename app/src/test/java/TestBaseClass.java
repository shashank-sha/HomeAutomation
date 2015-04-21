import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.HandlerThread;

import com.zemoso.zinteract.ZinteractSampleApp.Activity2;
import com.zemoso.zinteract.ZinteractSampleApp.Activity3;
import com.zemoso.zinteract.ZinteractSampleApp.Activity4;
import com.zemoso.zinteract.ZinteractSampleApp.Activity5;
import com.zemoso.zinteract.ZinteractSampleApp.MainActivity;

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
import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.util.ActivityController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by vedaprakash on 13/4/15.
 */
@Config(reportSdk = 18,emulateSdk = 18)
@RunWith(CustomRobolectricRunner.class)
public abstract class TestBaseClass {
    protected DbHelper sqLiteOpenHelper;
    protected SharedPreferences sharedPreferences;
    protected HandlerThread logWorker,httpWorker;
    protected ShadowLooper logWorkingLooper,httpWorkingLooper;
    protected ActivityController<MainActivity> mainActivityActivityController;
    protected ActivityController<Activity2> activity2ActivityController;
    protected ActivityController<Activity3> activity3ActivityController;
    protected ActivityController<Activity4> activity4ActivityController;
    protected ActivityController<Activity5> activity5ActivityController;

    @Before
    public void setup(){
        System.out.println("Checking if the fakeHttpLayer is functioning properly");
        FakeHttpLayer fakeHttpLayer = Robolectric.getFakeHttpLayer();
        assertFalse(fakeHttpLayer.hasPendingResponses());
        assertFalse(fakeHttpLayer.hasRequestInfos());
        assertFalse(fakeHttpLayer.hasResponseRules());
        assertNull(fakeHttpLayer.getDefaultResponse());

        System.out.println("Confirming that the database table for events is empty");
        sqLiteOpenHelper = DbHelper.getDatabaseHelper(Robolectric.application);
        assertEquals(0, sqLiteOpenHelper.getEventCount());

        System.out.println("Checking the SharedPreference file for an empty value for session Id");
        sharedPreferences = Robolectric.application.
                getSharedPreferences(Constants.Z_SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        assertEquals(sharedPreferences.getString(Constants.Z_PREFKEY_LAST_END_SESSION_ID, ""), "");

        System.out.println("Setting up the HttpResponse for HttpRequest to uploadEvents page");

        JSONObject success = new JSONObject();

        //Robolectric.getFakeHttpLayer().interceptHttpRequests(true);
        ProtocolVersion httpProtocolVersion = new ProtocolVersion("HTTP", 1, 1);
        HttpResponse simpleSuccessResponse = new BasicHttpResponse(httpProtocolVersion, 200, "OK");

        try {
            success.put("status", "success");
            simpleSuccessResponse.setEntity(new StringEntity(success.toString()));
        } catch (UnsupportedEncodingException|JSONException e) {
            e.printStackTrace();
        }

        //Setting up and confirming Robolectric's FakeHttpLayer is in effect
        System.out.println("Setting default responses for all other HttpRequests");
        Robolectric.addHttpResponseRule(Constants.Z_EVENT_LOG_URL,simpleSuccessResponse);
    }

    @Test
    public abstract void test();

    private Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return t;
        }
        return null;
    }

    protected void instantiateLogAndHttpWorkers(){
        if(logWorker==null||httpWorker==null||logWorkingLooper==null||httpWorkingLooper==null) {
            logWorker = (HandlerThread) getThreadByName("logWorker");
            httpWorker = (HandlerThread) getThreadByName("httpWorker");
            logWorkingLooper = Robolectric.shadowOf(logWorker.getLooper());
            httpWorkingLooper = Robolectric.shadowOf(httpWorker.getLooper());
        }
    }
    protected void executeImmediateTasksOnLogWorker(){
        executeTasksOnLogWorker();
        executeTasksOnHttpWorker();
        executeTasksOnLogWorker();
        executeTasksOnHttpWorker();
    }

    protected void executePendingTasksOnWorkers(long millis){
        logWorkingLooper.idle(millis);
        httpWorkingLooper.idle(millis);
        logWorkingLooper.idle(millis);
        httpWorkingLooper.idle(millis);
    }

    private void executeTasksOnLogWorker(){
        logWorkingLooper.idle();
    }

    private void executeTasksOnHttpWorker(){
        httpWorkingLooper.idle();
    }

    private String getCurrentDateTime(long timestamp){
        return new SimpleDateFormat(Constants.Z_DATE_TIME_FORMAT).format(new Date(timestamp));
    }
    private String readFile( String file ){
        StringBuilder stringBuilder = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }catch(IOException e){
            e.printStackTrace();
            assertTrue(false);
        }
        return stringBuilder.toString();
    }
}
