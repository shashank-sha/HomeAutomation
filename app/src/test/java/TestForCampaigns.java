import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

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

/**
 * Created by vedaprakash on 22/5/15.
 */
public class TestForCampaigns extends TestSessionId {
    JSONObject response = null;
    @Override
    public void setup(){
        super.setup();

        try {
            response = new JSONObject(readFile("app/src/test/java/campaigns.JSON"));
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
        Robolectric.addHttpResponseRule(Constants.Z_PROMOTION_URL, fetchPromosSuccessResponse);
    }

    protected boolean notificationForCampaignExists(List<Notification> list, int count, int campaignNoInPromos){
        Notification notification = list.get(count);
        ShadowNotification shadowNotification= Robolectric.shadowOf(notification);
        try {
            JSONObject campaign = response.getJSONArray("promotions").getJSONObject(campaignNoInPromos);
            String campaignType = campaign.getString("type");
            JSONObject template = campaign.getJSONObject("template");
            String title = template.getString("title");
            String message = template.getString("message");
            String intentToOpen = template.getJSONObject("contentClick").getJSONObject("nextScreen").getString("deepLink");
            Intent intentExpected = new Intent(Robolectric.application, Class.forName(intentToOpen));
            intentExpected.putExtra("campaignId", campaign.getString("campaignId"));
            if(campaignType.equals("SIMPLE_EVENT")) {
                intentExpected.putExtra(Constants.Z_CAMPAIGN_TYPE, Constants.Z_CAMPAIGN_TYPE_SIMPLE_EVENT_CAMPAIGN);
            }else if(campaignType.equals("GEO")){
                intentExpected.putExtra(Constants.Z_CAMPAIGN_TYPE, Constants.Z_CAMPAIGN_TYPE_GEOCAMPAIGN);
                intentExpected.putExtra("geofenceId",campaign.getJSONObject("geo").getJSONArray("geoFences").getJSONObject(0).getString("geofenceId"));
            }
            intentExpected.putExtra(Constants.Z_EVENT_TYPE, Constants.Z_CAMPAIGN_VIEWED_EVENT);
            ShadowPendingIntent shadowPendingIntent = Robolectric.shadowOf(notification.contentIntent);
            Intent intentActual  = shadowPendingIntent.getSavedIntent();
            System.out.println("Success before if"+ shadowNotification.getContentTitle());

            if (shadowNotification.getContentTitle().equals(title)) {
                System.out.println("Success before message check");
                System.out.println("TEST: notification title matches");
                if (shadowNotification.getContentText().equals(message)) {
                    System.out.println("Success after message check");
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

    protected List<Notification> getNotifications(){
        ShadowNotificationManager shadowNotificationManager = Robolectric.shadowOf((NotificationManager)currentActivity.getSystemService(Context.NOTIFICATION_SERVICE));
        List<Notification> listOfNotifications = shadowNotificationManager.getAllNotifications();
        System.out.println("No.of notifications = " + listOfNotifications.size());
        return listOfNotifications;
    }


}
