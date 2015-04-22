package com.zemosolabs.zinteract.sdk;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.zemosolabs.zinteract.R;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    public static int notificationCount=0;

    public GcmIntentService() {
        super("GcmIntentService");
        //Log.i("log info ","GCMINTENT service --> gcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("GcmIntentService","Notified of push");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        //Log.i("log info ","GCMINTENT service --> onHandleintent");
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                /*sendNotification("Send error: " + extras.toString());*/
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                /*sendNotification("Deleted messages on server: " +
                        extras.toString());*/
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras);

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle bundle) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
/*        Intent notificationIntent = new Intent(this,SplashScreen.class);
        notificationIntent.putExtra("setFragment", "Notification");

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        */
        notificationCount++;
        Integer iconId = getResources().getIdentifier("ic_launcher","drawable","com.zemoso.zinteract.ZinteractSampleApp");

        if(iconId==0){
            iconId= R.drawable.ic_launcher;
            Log.i("Icon for notification","Not found in the app Resources");
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this).setSmallIcon(iconId)
        .setContentText(bundle.getString("message"));
        mBuilder.setContentTitle(bundle.getString("title"));
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}