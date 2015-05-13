package com.zemosolabs.zinteract.sdk;

import android.content.Intent;
import org.json.JSONObject;

/**
 * Created by vedaprakash on 8/5/15.
 */
class GeoNotificationCampaign extends NotificationCampaign {

    protected GeoNotificationCampaign(String campaignId, long notBefore, long notAfter, long uniqueId,
                                      String campaignType, JSONObject template,int numberOfTimesToShow, int notificationId) {
        //TODO: use the JSONObject to transfer all the data to fields inside the notificationCampaign
        super(campaignId, notBefore, notAfter, uniqueId, campaignType, template, numberOfTimesToShow, notificationId);
    }

    protected Intent addExtrasToIntent(Intent launchIntent,String details) {
        String[] requestId = details.split("_",2);
        String campaignId = requestId[0];
        String geoFenceId = requestId[1];
        launchIntent.putExtra("campaignId",campaignId);
        launchIntent.putExtra("geofenceId",geoFenceId);
        launchIntent.putExtra(Constants.Z_CAMPAIGN_TYPE,Constants.Z_CAMPAIGN_TYPE_GEOCAMPAIGN);
        launchIntent.putExtra(Constants.Z_EVENT_TYPE,Constants.Z_CAMPAIGN_VIEWED_EVENT);
        return launchIntent;
    }
}
