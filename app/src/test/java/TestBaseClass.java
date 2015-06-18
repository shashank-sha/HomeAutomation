import android.content.ComponentName;
import android.content.Intent;
import android.os.HandlerThread;

import com.zemoso.zinteract.ZinteractSampleApp.Activity2;
import com.zemoso.zinteract.ZinteractSampleApp.Activity3;
import com.zemoso.zinteract.ZinteractSampleApp.Activity4;
import com.zemoso.zinteract.ZinteractSampleApp.Activity5;
import com.zemoso.zinteract.ZinteractSampleApp.MainActivity;
import com.zemosolabs.zinteract.sdk.CampaignHandlingService;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.util.ActivityController;
import org.robolectric.util.ServiceController;

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
    protected HandlerThread logWorker,httpWorker,campaignWorker,fetcher,triggerHandler,delayer,delayerForGeofenceReceiver;
    protected ShadowLooper logWorkingLooper,httpWorkingLooper,campaignWorkLooper,fetchLooper,triggerHandlingLooper,delayingLooper, delayForGeofenceReceiverLooper;
    protected ActivityController<MainActivity> mainActivityActivityController;
    protected ActivityController<Activity2> activity2ActivityController;
    protected ActivityController<Activity3> activity3ActivityController;
    protected ActivityController<Activity4> activity4ActivityController;
    protected ActivityController<Activity5> activity5ActivityController;

    @Before
    public void setup(){
        //ZeTarget.robolectricTesting = true;
        System.out.println("Checking if the fakeHttpLayer is functioning properly");
        FakeHttpLayer fakeHttpLayer = Robolectric.getFakeHttpLayer();
        assertFalse(fakeHttpLayer.hasPendingResponses());
        assertFalse(fakeHttpLayer.hasRequestInfos());
        assertFalse(fakeHttpLayer.hasResponseRules());
        assertNull(fakeHttpLayer.getDefaultResponse());

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

    @After
    public void closeUP(){
        //ZeTarget.robolectricTesting = false;
    }

    protected void instantiateZeTargetWorkers(){
        if(logWorker==null||httpWorker==null||logWorkingLooper==null||
                httpWorkingLooper==null||campaignWorker==null||campaignWorkLooper==null) {
            logWorker = (HandlerThread) getThreadByName("logWorker");
            httpWorker = (HandlerThread) getThreadByName("httpWorker");
            campaignWorker = (HandlerThread) getThreadByName("campaignWorker");
            logWorkingLooper = Robolectric.shadowOf(logWorker.getLooper());
            httpWorkingLooper = Robolectric.shadowOf(httpWorker.getLooper());
            campaignWorkLooper = Robolectric.shadowOf(campaignWorker.getLooper());
        }
    }

    protected void instantiateCampaignHandlerWorkers(){
        if(fetcher==null||triggerHandler==null||fetchLooper==null||
                triggerHandlingLooper==null) {
            fetcher = (HandlerThread) getThreadByName("campaignFetcher");
            triggerHandler = (HandlerThread) getThreadByName("campaignTriggerHandler");
            fetchLooper = Robolectric.shadowOf(fetcher.getLooper());
            triggerHandlingLooper = Robolectric.shadowOf(triggerHandler.getLooper());
        }
    }

    protected void instantiateDelayWorkerForShakeListener(){
        if(delayer==null||delayingLooper==null){
            delayer = (HandlerThread) getThreadByName("delayer");
            delayingLooper = Robolectric.shadowOf(delayer.getLooper());
        }
    }

    protected void instantiateDelayWorkerForGeofenceReceiver(){
        if(delayerForGeofenceReceiver == null||delayForGeofenceReceiverLooper==null){
            delayerForGeofenceReceiver = (HandlerThread) getThreadByName("delayForGeofenceReceiver");
            delayForGeofenceReceiverLooper = Robolectric.shadowOf(delayerForGeofenceReceiver.getLooper());
        }
    }


    protected void executeImmediateTasksOnZeTargetWorkers(){
        executeImmediateTasksOnWorker(logWorkingLooper);
        executeImmediateTasksOnWorker(httpWorkingLooper);
        executeImmediateTasksOnWorker(campaignWorkLooper);
        executeImmediateTasksOnWorker(logWorkingLooper);
        executeImmediateTasksOnWorker(httpWorkingLooper);
        executeImmediateTasksOnWorker(campaignWorkLooper);
    }

    protected void executeImmediateTasksOnCampaignHandlerWorkers(){
        executeImmediateTasksOnWorker(fetchLooper);
        executeImmediateTasksOnWorker(triggerHandlingLooper);
    }

    protected void executePendingTasksOnZeTargetWorkers(long millis){
        executePendingTasksOnWorker(logWorkingLooper, millis);
        executePendingTasksOnWorker(httpWorkingLooper, millis);
        executePendingTasksOnWorker(campaignWorkLooper, millis);
        executePendingTasksOnWorker(logWorkingLooper, millis);
        executePendingTasksOnWorker(httpWorkingLooper, millis);
        executePendingTasksOnWorker(campaignWorkLooper, millis);
    }

    protected void executePendingTasksOnCampaignHandlerWorkers(long millis) {
        executePendingTasksOnWorker(fetchLooper,millis);
        executePendingTasksOnWorker(triggerHandlingLooper,millis);
    }

    protected void executeImmediateTasksOnWorker(ShadowLooper looper){
        looper.idle();
    }
    protected void executePendingTasksOnWorker(ShadowLooper looper,long millis){
        looper.idle(millis);
    }

    protected void executeWorkerTasksForLogEvent(){
        instantiateZeTargetWorkers();
        executeImmediateTasksOnZeTargetWorkers();
        executeWorkerTasksOnCampaignHandler();
        instantiateZeTargetWorkers();
        executeImmediateTasksOnZeTargetWorkers();
        executeWorkerTasksOnCampaignHandler();
        /*intentForService = shadowApplication.getNextStartedService();
        if(intentForService==null){
            return;
        }
        compName = intentForService.getComponent();
        System.out.println("TEST: service started- " + compName.getClassName());
        if (compName != null && compName.getClassName().equals("com.zemosolabs.zinteract.sdk.CampaignHandlingService")) {
            ServiceController<CampaignHandlingService> campaignHandler = ServiceController.of(CampaignHandlingService.class);
            campaignHandler.attach().create().withIntent(intentForService).startCommand(0, 1);
            instantiateCampaignHandlerWorkers();
            executeImmediateTasksOnCampaignHandlerWorkers();
        }*/
    }

    protected void executeWorkerTasksForInAppPromos(){
        executeImmediateTasksOnWorker(logWorkingLooper);
        Robolectric.runUiThreadTasks();
        executeImmediateTasksOnWorker(httpWorkingLooper);
        executeImmediateTasksOnWorker(campaignWorkLooper);
        executeImmediateTasksOnWorker(logWorkingLooper);
        executeImmediateTasksOnWorker(httpWorkingLooper);
        executeImmediateTasksOnWorker(campaignWorkLooper);
    }


    protected void executeWorkerTasksOnCampaignHandler() {
        ShadowApplication shadowApplication = Robolectric.shadowOf(Robolectric.application);
        Intent intentForService = shadowApplication.getNextStartedService();
        if(intentForService==null){
            return;
        }
        ComponentName compName = intentForService.getComponent();
        System.out.println("TEST: service started- "+compName.getClassName());
        if (compName != null && compName.getClassName().equals("com.zemosolabs.zinteract.sdk.CampaignHandlingService")) {
            ServiceController<CampaignHandlingService> campaignHandler = ServiceController.of(CampaignHandlingService.class);
            campaignHandler.attach().create().withIntent(intentForService).startCommand(0, 1);
            instantiateCampaignHandlerWorkers();
            executeImmediateTasksOnCampaignHandlerWorkers();
        }
    }


    private String getCurrentDateTime(long timestamp){
        return new SimpleDateFormat(Constants.Z_DATE_TIME_FORMAT).format(new Date(timestamp));
    }
    protected String readFile( String file ){
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
