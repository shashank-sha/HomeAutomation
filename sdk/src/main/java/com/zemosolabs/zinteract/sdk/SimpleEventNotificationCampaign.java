package com.zemosolabs.zinteract.sdk;

import android.content.Intent;

import org.json.JSONObject;

/**
 * Created by vedaprakash on 8/5/15.
 */
public class SimpleEventNotificationCampaign extends NotificationCampaign {

    protected SimpleEventNotificationCampaign(String campaignId, long notBefore, long notAfter, long uniqueId, String campaignType, JSONObject template, int notificationId) {
        super(campaignId, notBefore, notAfter, uniqueId, campaignType, template, notificationId);
    }

    @Override
    protected Intent addExtrasToIntent(Intent launchIntent, String details) {
        String campaignId = details;
        String[] detailsForActivity = new String[4];
        detailsForActivity[0] = "eventName";
        detailsForActivity[1] = "Notification Viewed";
        detailsForActivity[2] = "campaignId";
        detailsForActivity[3] = campaignId;
        launchIntent.putExtra(Constants.Z_INTENT_EXTRA_DETAILS_FOR_LOGGING,detailsForActivity);
        return launchIntent;
    }
}
