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
    protected long notBefore;
    protected long notAfter;
    protected long uniqueId;
    protected String campaignId;
    protected String campaignType;
    protected NotificationManager notifier;
    protected JSONObject template;
    protected int notificationId;
    protected int numberOfTimesToShow;

    protected NotificationCampaign(String campaignId, long notBefore, long notAfter, long uniqueId,
                                   String campaignType, JSONObject template, int numberOfTimesToShow, int notificationId){
        //TODO: use the JSONObject to transfer all the data to fields inside the notificationCampaign
        this.campaignId = campaignId;
        this.campaignType= campaignType;
        this.notAfter = notAfter;
        this.notBefore = notBefore;
        this.uniqueId = uniqueId;
        this.template = template;
        this.notificationId = notificationId;
        this.numberOfTimesToShow = numberOfTimesToShow;
    }

    void show(Context context,String details){
        if(System.currentTimeMillis()<notBefore){
            Log.i("NotificationCampaign", "not yet to be shown");
            return;
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
        }
    }

    abstract protected Intent addExtrasToIntent(Intent intent, String details);

    public boolean valid(long timeStamp) {
        if(timeStamp>notAfter) {
            return false;
        }
        if(numberOfTimesToShow==0){
            return false;
        }
        return true;
    }
}
