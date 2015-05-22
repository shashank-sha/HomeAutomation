import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import com.zemoso.zinteract.ZinteractSampleApp.Activity2;
import com.zemoso.zinteract.ZinteractSampleApp.Activity4;
import com.zemoso.zinteract.ZinteractSampleApp.R;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;
import org.robolectric.shadows.ShadowPendingIntent;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by vedaprakash on 20/5/15.
 */
public class TestForSimpleEventTriggeredCampaigns extends TestSessionId {
    JSONObject response = null;
    @Override
    public void setup(){
        super.setup();

        try {
            response = new JSONObject(readFile("app/src/test/java/campaignSimpleEvent.JSON"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProtocolVersion httpProtocolVersion = new ProtocolVersion("HTTP", 1, 1);
        HttpResponse fetchPromosSuccessResponse = new BasicHttpResponse(httpProtocolVersion, 200, "OK");

        try {
            fetchPromosSuccessResponse.setEntity(new StringEntity(response.toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Robolectric.addHttpResponseRule(Constants.Z_PROMOTION_URL,fetchPromosSuccessResponse);
    }
    @Override
    public void test() {
        List<Notification> listOfNotifications;
        int numberOfNotifications = 0;
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next", "MainActivity");
        Robolectric.clearHttpResponseRules();
        int maxNumberOfTimesToShow = 0;
        int minutesBeforeReshow = 0;
        try {
            maxNumberOfTimesToShow  = response.getJSONArray("promotions").getJSONObject(0).getJSONObject("suppressionLogic").getInt("maximumNumberOfTimesToShow");
            minutesBeforeReshow = response.getJSONArray("promotions").getJSONObject(0).getJSONObject("suppressionLogic").getInt("minimumDurationInMinutesBeforeReshow");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //First time show notification test if its present
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next", "Activity2");
        listOfNotifications = goFromActivity2To4AndMakeAPurchase();
        assertTrue(notificationForSimpleEventExists(listOfNotifications,numberOfNotifications));
        numberOfNotifications = listOfNotifications.size();
        activity4ActivityController.pause();

        //Attempt to show second time before the time lapse test if no notification
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next", "Activity2");
        listOfNotifications = goFromActivity2To4AndMakeAPurchase();
        assertTrue(listOfNotifications.size()==numberOfNotifications);
        activity4ActivityController.pause();

        //Test if the notification is showed the number of times specified in the maxNumberOfTimesToShow
        for(int i=1;i<=maxNumberOfTimesToShow-1;i++){
            try {
                Thread.sleep(minutesBeforeReshow*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next", "Activity2");
            listOfNotifications = goFromActivity2To4AndMakeAPurchase();
            assertTrue(notificationForSimpleEventExists(listOfNotifications,numberOfNotifications));
            numberOfNotifications = listOfNotifications.size();
            activity4ActivityController.pause();
        }
        try {
            Thread.sleep(minutesBeforeReshow*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Test that the notification should not be shown anymore than the maxNumberOfTimesToShow
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next", "Activity2");
        listOfNotifications = goFromActivity2To4AndMakeAPurchase();
        assertTrue(listOfNotifications.size()==numberOfNotifications);
        activity4ActivityController.pause();

    }

    private List<Notification> goFromActivity2To4AndMakeAPurchase(){
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next", "Activity3");
        if(activity4ActivityController==null){
            activity4ActivityController = Robolectric.buildActivity(Activity4.class).attach().create().start().resume().visible();
        }
        else{
            activity4ActivityController.resume().visible();
        }
        executeWorkerTasksForLogEvent();
        currentActivity = (Activity4) activity4ActivityController.get();
        System.out.println(currentActivity.getLocalClassName());

        // Click on Add to cart button
        Button addToCart = (Button)currentActivity.findViewById(R.id.buttonAddToCart);
        addToCart.performClick();

        executeWorkerTasksForLogEvent();

        // Click on Buy button to make the purchase
        Button buy = (Button)currentActivity.findViewById(R.id.buttonBuy);
        buy.performClick();

        executeWorkerTasksForLogEvent();

        ShadowNotificationManager shadowNotificationManager = Robolectric.shadowOf((NotificationManager)currentActivity.getSystemService(Context.NOTIFICATION_SERVICE));
        List<Notification> listOfNotifications = shadowNotificationManager.getAllNotifications();
        System.out.println("No.of notifications = " + listOfNotifications.size());
        return listOfNotifications;
    }

    private boolean notificationForSimpleEventExists(List<Notification> list,int count){
        Notification notification = list.get(count);
            ShadowNotification shadowNotification= Robolectric.shadowOf(notification);
            try {
                String title = response.getJSONArray("promotions").getJSONObject(0).getJSONObject("template").getString("title");
                String message = response.getJSONArray("promotions").getJSONObject(0).getJSONObject("template").getString("message");
                String intentToOpen = response.getJSONArray("promotions").getJSONObject(0).getJSONObject("template")
                        .getJSONObject("contentClick").getJSONObject("nextScreen").getString("deepLink");
                Intent intentExpected = new Intent(Robolectric.application, Class.forName(intentToOpen));
                intentExpected.putExtra("campaignId", response.getJSONArray("promotions").getJSONObject(0).getString("campaignId"));
                intentExpected.putExtra(Constants.Z_CAMPAIGN_TYPE, Constants.Z_CAMPAIGN_TYPE_SIMPLE_EVENT_CAMPAIGN);
                intentExpected.putExtra(Constants.Z_EVENT_TYPE, Constants.Z_CAMPAIGN_VIEWED_EVENT);
                ShadowPendingIntent shadowPendingIntent = Robolectric.shadowOf(notification.contentIntent);
                Intent intentActual  = shadowPendingIntent.getSavedIntent();
                if (shadowNotification.getContentTitle().equals(title)) {
                    System.out.println("TEST: notification title matches");
                    if (shadowNotification.getContentText().equals(message)) {
                        System.out.println("TEST: notification message matches");
                        assertEquals(intentExpected, intentActual);
                        System.out.println("TEST: notification intent matches");
                        return true;
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        return false;
    }
}
