import android.app.Activity;
import android.widget.Button;

import com.zemoso.zinteract.ZinteractSampleApp.Activity2;
import com.zemoso.zinteract.ZinteractSampleApp.Activity3;
import com.zemoso.zinteract.ZinteractSampleApp.Activity4;
import com.zemoso.zinteract.ZinteractSampleApp.Activity5;
import com.zemoso.zinteract.ZinteractSampleApp.MainActivity;
import com.zemoso.zinteract.ZinteractSampleApp.R;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.tester.org.apache.http.HttpRequestInfo;
import org.robolectric.util.ActivityController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


/**
 * Created by vedaprakash on 13/4/15.
 */
@Config(reportSdk = 18,emulateSdk = 18)
@RunWith(CustomRobolectricRunner.class)
public class TestSessionId extends TestBaseClass{
    Activity currentActivity;
    ArrayList<String> sessionIds;

    @Test
    public void test(){
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next","MainActivity");
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next","Activity2");
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next","Activity3");
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next","Activity4");
        doRepetitiveSetOfActionsContinuouslyFor(2);
        sessionIds = new ArrayList<>();
        fetchSessionInfo();
        assertTrue(sessionIds.size() == 1);
        System.out.println(sessionIds);
        doRepetitiveSetOfActionsWithAGapExceedingThresholdForSessionFor(3, (Constants.Z_MIN_TIME_BETWEEN_SESSIONS_MILLIS + 1001));
        sessionIds = new ArrayList<>();
        fetchSessionInfo();
        assertTrue(sessionIds.size() == 3);
        System.out.println(sessionIds);
        doRepetitiveSetOfActionsWithAGapExceedingThresholdForSessionFor(3,(Constants.Z_MIN_TIME_BETWEEN_SESSIONS_MILLIS/2));
        sessionIds = new ArrayList<>();
        fetchSessionInfo();
        assertTrue(sessionIds.size()==4);
        System.out.println(sessionIds);
    }

    private void doRepetitiveSetOfActionsWithAGapExceedingThresholdForSessionFor(int i,long millis) {
        for(int j=0;j<i;j++) {
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("prev", "Activity5");
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("prev", "Activity4");
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next", "Activity3");
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next", "Activity4");
            executePendingTasksOnWorkers(millis);
            System.out.println("Waiting for "+millis/1000+" seconds");
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doRepetitiveSetOfActionsContinuouslyFor(int i) {
        for(int j=0;j<i;j++){
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("prev","Activity5");
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("prev","Activity4");
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next","Activity3");
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next","Activity4");
        }
    }

    private boolean fetchSessionInfo() {
        List<HttpRequestInfo> requestInfos = Robolectric.getFakeHttpLayer().getSentHttpRequestInfos();
        System.out.println(requestInfos.size());
        for (HttpRequestInfo requestInfo : requestInfos) {
            HttpRequest request = requestInfo.getHttpRequest();
            if(request instanceof HttpPost) {
                HttpPost postRequest = (HttpPost) request;
                assertNotNull(postRequest);
                if (postRequest.getURI().toString().equals(Constants.Z_EVENT_LOG_URL)) {
                    try {
                        JSONObject jsonRequest = new JSONObject(EntityUtils.toString(postRequest.getEntity()));
                        //System.out.println()
                        checkJSONForNumberOfSessions(jsonRequest);
                    } catch (IOException|JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //System.out.println(postRequest.getURI().toString());
                }
            }else{
                //System.out.println(request.getClass().getSimpleName());
            }
        }
        return true;
    }

    private void checkJSONForNumberOfSessions(JSONObject jsonObject) {
        int count =0;
        try {
            JSONArray events = jsonObject.getJSONArray("eventList");
            for(int i=0;i<events.length();i++){
                //System.out.println("EventsArray Length:"+events.length());
                JSONObject event = events.getJSONObject(i);
                if(event.has("sessionId")){
                    String sessionId = event.getString("sessionId");
                    if(sessionIds.contains(sessionId)){
                        continue;
                    }else{
                        sessionIds.add(0,sessionId);
                        count++;
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    void resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity(String action, String activityName){
        int nextButtonId,prevButtonId;
        Class nextActivity,prevActivity;
        ActivityController<Activity> currentActivityController;
        System.out.println("ResumeActivityDoAction: "+"Creating/Resuming the activity : "+activityName);
        switch(activityName){
            case "MainActivity":
                if(mainActivityActivityController==null){
                    mainActivityActivityController = Robolectric.buildActivity(MainActivity.class).attach().create().start().resume().visible();
                }
                else{
                    mainActivityActivityController.resume().visible();
                }
                currentActivity  = (MainActivity)mainActivityActivityController.get();
                currentActivityController = ActivityController.of((Activity)currentActivity);
                prevActivity = null;
                nextActivity = Activity2.class;
                nextButtonId = R.id.button1to2;
                prevButtonId = -1;
                break;
            case "Activity2":
                if(activity2ActivityController==null){
                    activity2ActivityController = Robolectric.buildActivity(Activity2.class).attach().create().start().resume().visible();
                }
                else{
                    activity2ActivityController.resume().visible();
                }
                currentActivity  = (Activity2)activity2ActivityController.get();
                currentActivityController = ActivityController.of((Activity)currentActivity);
                prevActivity = MainActivity.class;
                nextActivity = Activity3.class;
                nextButtonId = R.id.button2to3;
                prevButtonId = R.id.button2to1;
                break;
            case "Activity3":
                if(activity3ActivityController==null){
                    activity3ActivityController = Robolectric.buildActivity(Activity3.class).attach().create().start().resume().visible();
                }
                else{
                    activity3ActivityController.resume().visible();
                }
                currentActivity  = (Activity3)activity3ActivityController.get();
                currentActivityController = ActivityController.of((Activity)currentActivity);
                prevActivity = Activity2.class;
                nextActivity = Activity4.class;
                nextButtonId = R.id.button3to4;
                prevButtonId = R.id.button3to2;
                break;
            case "Activity4":
                if(activity4ActivityController==null){
                    activity4ActivityController = Robolectric.buildActivity(Activity4.class).attach().create().start().resume().visible();
                }
                else{
                    activity4ActivityController.resume().visible();
                }
                currentActivity  = (Activity4)activity4ActivityController.get();
                currentActivityController = ActivityController.of((Activity)currentActivity);
                nextActivity = Activity5.class;
                prevActivity = Activity3.class;
                nextButtonId = R.id.button4to5;
                prevButtonId = R.id.button4to3;
                break;
            case "Activity5":
                if(activity5ActivityController==null){
                    activity5ActivityController = Robolectric.buildActivity(Activity5.class).attach().create().start().resume().visible();
                }
                else{
                    activity5ActivityController.resume().visible();
                }
                currentActivity  = (Activity5)activity5ActivityController.get();
                currentActivityController = ActivityController.of((Activity)currentActivity);
                prevActivity = Activity4.class;
                nextActivity = MainActivity.class;
                nextButtonId = R.id.button5to1;
                prevButtonId = R.id.button5to4;
                break;
            default:
                assertTrue(false);
                return;
        }
        assertNotNull(currentActivity);
        instantiateLogAndHttpWorkers();
        executeImmediateTasksOnLogWorker();
        //validate required action
        switch(action){
            case "next":
                System.out.println("ResumeActivityDoAction: "+"Clicking on the button for next activity : "+nextActivity.getClass().getSimpleName());
                ((Button)currentActivity.findViewById(nextButtonId)).performClick();
                ShadowIntent intent = Robolectric.shadowOf(Robolectric.shadowOf(currentActivity).getNextStartedActivity());
                assertEquals(intent.getIntentClass(),nextActivity);
                break;
            case "prev":
                System.out.println("ResumeActivityDoAction: "+"Clicking on the button for prev activity : "+prevActivity.getClass().getSimpleName());
                ((Button)currentActivity.findViewById(prevButtonId)).performClick();
                ShadowIntent intent2 = Robolectric.shadowOf(Robolectric.shadowOf(currentActivity).getNextStartedActivity());
                assertEquals(intent2.getIntentClass(),prevActivity);
                break;
            default:
                assertTrue(false);
                return;
        }
        System.out.println("ResumeActivityDoAction: "+"Pausing the activity : "+activityName);
        currentActivityController.pause();
    }
}
