import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import com.zemosolabs.zinteract.sdk.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowLocationManager;

import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * Created by vedaprakash on 22/5/15.
 */
public class TestForGeoTriggeredCampaign extends TestForCampaigns {
    private static boolean toggle;

    @Override
    public void test() {
        List<Notification> listOfNotifications;
        int numberOfNotifications = 0;
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("next", "MainActivity");
        Robolectric.clearHttpResponseRules();
        int maxNumberOfTimesToShow = 0;
        int minutesBeforeReshow = 0;
        try {
            maxNumberOfTimesToShow = response.getJSONArray("promotions").getJSONObject(1).getJSONObject("suppressionLogic").getInt("maximumNumberOfTimesToShow");
            minutesBeforeReshow = response.getJSONArray("promotions").getJSONObject(1).getJSONObject("suppressionLogic").getInt("minimumDurationInMinutesBeforeReshow");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        simulateLocationAndWaitForDWELL();
       // assertTrue(Robolectric.shadowOf(Robolectric.application).getNextStartedService().getComponent().getClassName().equals("com.zemosolabs.zinteract.sdk.GeofenceTransitionsIntentService"));

        //first time should show notification
        simulateTriggeringCampaignHandlerViaGeoTrigger();

        executeWorkerTasksOnCampaignHandler();
        listOfNotifications = getNotifications();
        assertTrue(notificationForCampaignExists(listOfNotifications, numberOfNotifications, 1));
        numberOfNotifications = listOfNotifications.size();

        //second time before minimum duration elapse should not show
        simulateTriggeringCampaignHandlerViaGeoTrigger();

        executeWorkerTasksOnCampaignHandler();
        listOfNotifications = getNotifications();
        assertTrue(listOfNotifications.size()==numberOfNotifications);

        for(int i=1;i<=maxNumberOfTimesToShow-1;i++){
            try {
                Thread.sleep(minutesBeforeReshow*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            simulateTriggeringCampaignHandlerViaGeoTrigger();

            executeWorkerTasksOnCampaignHandler();
            listOfNotifications = getNotifications();
            assertTrue(notificationForCampaignExists(listOfNotifications, numberOfNotifications,1));
            numberOfNotifications = listOfNotifications.size();
        }
        try {
            Thread.sleep(minutesBeforeReshow*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Test that the notification should not be shown anymore than the maxNumberOfTimesToShow
        simulateTriggeringCampaignHandlerViaGeoTrigger();

        executeWorkerTasksOnCampaignHandler();
        listOfNotifications = getNotifications();
        assertTrue(listOfNotifications.size() == numberOfNotifications);


    }

    private void simulateTriggeringCampaignHandlerViaGeoTrigger() {
        Intent intentToCampaignHandlingService = new Intent(Robolectric.application,CampaignHandlingService.class);
        intentToCampaignHandlingService.putExtra("action", Constants.Z_INTENT_EXTRA_CAMPAIGNS_ACTION_KEY_VALUE_HANDLE_GEO_TRIGGERS);
        try {
            JSONObject campaign = response.getJSONArray("promotions").getJSONObject(1);
            String campaignId = campaign.getString("campaignId");
            String geofenceId = campaign.getJSONObject("geo").getJSONArray("geoFences").getJSONObject(0).getString("geofenceId");;
            /*if(!toggle) {
                geofenceId = campaign.getJSONObject("geo").getJSONArray("geoFences").getJSONObject(0).getString("geofenceId");
            }else{
                geofenceId = campaign.getJSONObject("geo").getJSONArray("geoFences").getJSONObject(1).getString("geofenceId");
            }*/
            intentToCampaignHandlingService.putExtra("reqId", campaignId + "_" + geofenceId);
            Robolectric.application.startService(intentToCampaignHandlingService);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void simulateLocationAndWaitForDWELL() {
        ShadowLocationManager shadowLocationManager = Robolectric.shadowOf((LocationManager)Robolectric.application.getSystemService(Context.LOCATION_SERVICE));
        shadowLocationManager.simulateLocation(getGoodLocation());
        try {
            long dwell;
            if(response.getJSONArray("promotions").getJSONObject(1).getJSONObject("geo").getString("transitionType").equals("DWELL")){
                dwell = response.getJSONArray("promotions").getJSONObject(1).getJSONObject("geo").getLong("timeDelay");
                Thread.sleep(dwell);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Location getGoodLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        try {
            JSONObject geofence0 = response.getJSONArray("promotions").getJSONObject(1).getJSONObject("geo").getJSONArray("geoFences").getJSONObject(0);
            location.setLatitude(geofence0.getDouble("latitude"));
            location.setLongitude(geofence0.getDouble("longitude"));
            location.setTime(System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return location;
    }
}
