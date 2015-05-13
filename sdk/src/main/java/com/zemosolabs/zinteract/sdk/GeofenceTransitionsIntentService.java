package com.zemosolabs.zinteract.sdk;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.zemosolabs.zinteract.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = "zint.GeofenceIntentSrvc";

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent!=null){
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                Log.e(TAG, Integer.valueOf(geofencingEvent.getErrorCode()).toString());
                return;
            }

            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            for(Object geofence: triggeringGeofences){
                String requestId = ((Geofence)geofence).getRequestId();
                Intent intentToCampaignHandlingService = new Intent(this,CampaignHandlingService.class);
                intentToCampaignHandlingService.putExtra("action",Constants.Z_INTENT_EXTRA_CAMPAIGNS_ACTION_KEY_VALUE_HANDLE_GEO_TRIGGERS);
                intentToCampaignHandlingService.putExtra("reqId",requestId);
                startService(intentToCampaignHandlingService);
            }
        }
    }

    private String getJSONStringFromFile() {
        String jsonString = "";
        String line;
        try {
            InputStream iS = getAssets().open("geofencesRelated.json");
            InputStreamReader iSReader = new InputStreamReader(iS);
            BufferedReader bufferedReader = new BufferedReader(iSReader);
            while((line=bufferedReader.readLine())!=null){
                jsonString=jsonString+"\n"+line;
            }
            return jsonString;
        } catch (IOException e) {
            Log.e(TAG, "Read failed");
            return null;
        }
    }


}
