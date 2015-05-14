package com.zemosolabs.zinteract.sdk;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vedaprakash on 8/5/15.
 */
abstract class NotificationCampaign {
    private static final String TAG = "notificationCampaign";
    protected long campaignEndTime;
    protected long campaignStartTime;
    protected long uniqueId;
    protected String campaignId;
    protected String campaignType;
    protected NotificationManager notifier;
    protected JSONObject template;
    protected boolean checkServerBeforeNotifying;
    protected int notificationId;
    protected int maximumNumberOfTimesToShow;
    protected int minimumMinutesBeforeReshow;

    protected NotificationCampaign(JSONObject currentCampaign, int notificationId){
        //TODO: use the JSONObject to transfer all the data to fields inside the notificationCampaign
        try {
            campaignId = currentCampaign.getString("campaignId");
            campaignType = currentCampaign.getString("type");
            campaignStartTime = currentCampaign.getLong("campaignStartTime");
            campaignEndTime = currentCampaign.getLong("campaignEndTime");
            uniqueId = currentCampaign.getInt("rowIdInTable");
            template = currentCampaign.getJSONObject("template");
            JSONObject suppressionLogic = currentCampaign.getJSONObject("suppressionLogic");
            maximumNumberOfTimesToShow = suppressionLogic.getInt("maximumNumberOfTimesToShow");
            minimumMinutesBeforeReshow = suppressionLogic.getInt("minimumDurationInMinutesBeforeReshow");
            checkServerBeforeNotifying = currentCampaign.getBoolean("checkServerBeforeNotifying");
        }catch (JSONException e){
            Log.e(TAG,"campaign json inflation error", e);
        }
        this.notificationId = notificationId;
    }

    void show(Context context,String details,long timeStamp){
        long lastShownTime = DbHelper.getDatabaseHelper(context).getLastShownTime(campaignId);
        if(lastShownTime>0){
            Log.i(TAG, timeStamp+" "+(lastShownTime+minimumMinutesBeforeReshow*6000));
            if(timeStamp<lastShownTime+ minimumMinutesBeforeReshow *60000){
                Log.i("NotificationCampaign", "not yet to be shown");
                return;
            }
        }

        notifier = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        int appIconId = context.getApplicationInfo().icon;
        String title=null,message=null;
        try {
            title = template.getString("title");
            message = template.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(title!=null&&message!=null) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context).setSmallIcon(appIconId)
                    .setContentTitle(title).setContentText(message).setAutoCancel(true);
            Intent launchIntent = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if(launchIntent!=null){
                launchIntent = addExtrasToIntent(launchIntent,details);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,launchIntent,0);
            notificationBuilder.setContentIntent(pendingIntent);
            notifier.notify(notificationId,notificationBuilder.build());
            DbHelper.getDatabaseHelper(context).updateCampaign(campaignId,timeStamp);
        }
    }

    abstract protected Intent addExtrasToIntent(Intent intent, String details);

    public boolean valid(Context context,long timeStamp) {
        Log.i(TAG," "+campaignId+": "+timeStamp+" "+campaignEndTime);
        if(timeStamp>campaignEndTime) {
            return false;
        }
        int numberOfTimesShown = DbHelper.getDatabaseHelper(context).getNumberOfTimesShown(campaignId);
        Log.i(TAG," "+ campaignId + ": " + numberOfTimesShown + " " + maximumNumberOfTimesToShow);
        if(numberOfTimesShown>=maximumNumberOfTimesToShow){
            return false;
        }
        return true;
    }
}
