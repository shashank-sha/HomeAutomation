package com.zemosolabs.zinteract.sdk;

import android.content.Intent;
import org.json.JSONObject;

/**
 * Created by vedaprakash on 8/5/15.
 */
class GeoNotificationCampaign extends NotificationCampaign {

    protected GeoNotificationCampaign(String campaignId, long notBefore, long notAfter, long uniqueId, String campaignType, JSONObject template, int notificationId) {
        super(campaignId, notBefore, notAfter, uniqueId, campaignType, template,notificationId);
    }

    protected Intent addExtrasToIntent(Intent launchIntent,String details) {
        String[] requestId = details.split("_");
        String campaignId = requestId[0];
        String geoFenceId = requestId[1];
        String[] detailsForActivity = new String[6];
        detailsForActivity[0] = "eventName";
        detailsForActivity[1] = "Notification Viewed";
        detailsForActivity[2] = "campaignId";
        detailsForActivity[3] = campaignId;
        detailsForActivity[4] = "geofenceId";
        detailsForActivity[5] = geoFenceId;
        launchIntent.putExtra(Constants.Z_INTENT_EXTRA_DETAILS_FOR_LOGGING,detailsForActivity);
        return launchIntent;
    }
}
