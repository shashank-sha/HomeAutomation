package com.zemoso.zinteract.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.zemoso.zinteract.sampleapp.BuildConfig;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by praveen on 19/01/15.
 */
public class Zinteract {

    public static final String TAG = "com.zemoso.zinteract.sdk.zinteract";
    private static Context context;
    private static String apiKey;
    private static String userId;
    private static String deviceId;

    private static DataStore dataStore = DataStore.getDataStore();



    private static DeviceDetails deviceDetails;

    private static long sessionId = -1;
    private static boolean isSessionOpen = false;
    private static long sessionTimeoutMillis = Constants.Z_SESSION_TIMEOUT;
    private static Runnable endSessionRunnable;

    private static AtomicBoolean updateScheduled = new AtomicBoolean(false);
    private static AtomicBoolean uploadingCurrently = new AtomicBoolean(false);
    private static AtomicBoolean synchingDataStoreCurrently = new AtomicBoolean(false);

    public static final String START_SESSION_EVENT = Constants.Z_SESSION_START_EVENT;
    public static final String END_SESSION_EVENT = Constants.Z_SESSION_END_EVENT;

    private static Zinteract Zinteract = null;
    protected static Worker logWorker = new Worker("logWorker");
    protected static Worker httpWorker = new Worker("httpWorker");

    static {
        logWorker.start();
        httpWorker.start();
    }

    private Zinteract(){

    }


    public static void initializeWithContextAndKey(Context context, String apiKey) {
        if(BuildConfig.DEBUG){
            Log.d(TAG,"initializeWithContextAndKey() called");
        }
        initialize(context, apiKey, null);
    }

    public synchronized static void initialize(Context context, String apiKey, String userId) {
        if (context == null) {
            Log.e(TAG, "Application context cannot be null in initializeWithContextAndKey()");
            return;
        }
        if (TextUtils.isEmpty(apiKey) || apiKey == null) {
            Log.e(TAG, "Application apiKey cannot be null or blank in initializeWithContextAndKey()");
            return;
        }
        if (Zinteract == null) {

            Zinteract.setContext(context.getApplicationContext());
            Zinteract.setApiKey(apiKey);
            initializeDeviceDetails();

            if(userId == null){
                userId = getUUID();
            }
            setUserId(userId);

        }
    }

    public static void startSession() {
        if(BuildConfig.DEBUG){
            Log.d(TAG,"startSession() called");
        }
        if (!isContextAndApiKeySet("startSession()")) {
            return;
        }
        final long now = System.currentTimeMillis();

        runOnLogWorker(new Runnable() {
            @Override
            public void run() {
                logWorker.removeCallbacks(endSessionRunnable);
                long previousEndSessionId = getEndSessionId();
                long lastEndSessionTime = getEndSessionTime();
                if (previousEndSessionId != -1
                        && now - lastEndSessionTime < Constants.Z_MIN_TIME_BETWEEN_SESSIONS_MILLIS) {
                    DbHelper dbHelper = DbHelper.getDatabaseHelper(context);
                    dbHelper.removeEvent(previousEndSessionId);
                }
                startNewSessionIfNeeded(now);
                openSession();

                // Update last event time
                setLastEventTime(now);
                syncDataStore();
                uploadEvents();
            }
        });
    }

    public static void endSession() {
        if(BuildConfig.DEBUG){
            Log.d(TAG,"endSession() called");
        }
        if (!isContextAndApiKeySet("endSession()")) {
            return;
        }
        final long timestamp = System.currentTimeMillis();
        runOnLogWorker(new Runnable() {
            @Override
            public void run() {
                JSONObject apiProperties = new JSONObject();
                try {
                    apiProperties.put("special", END_SESSION_EVENT);
                } catch (JSONException e) {
                }
                if (isSessionOpen) {
                    long eventId = logEvent(END_SESSION_EVENT, null, apiProperties, timestamp,
                            false);

                    SharedPreferences preferences = CommonUtils.getSharedPreferences(context);
                    preferences.edit().putLong(Constants.Z_PREFKEY_LAST_END_SESSION_ID, eventId)
                            .putLong(Constants.Z_PREFKEY_LAST_END_SESSION_TIME, timestamp)
                            .commit();
                }
                closeSession();
            }
        });
        // Queue up upload events 16 seconds later
        logWorker.removeCallbacks(endSessionRunnable);
        endSessionRunnable = new Runnable() {
            @Override
            public void run() {
                clearEndSession();
                uploadEvents();
            }
        };
        logWorker
                .postDelayed(endSessionRunnable, Constants.Z_MIN_TIME_BETWEEN_SESSIONS_MILLIS + 1000);
    }

    public static void setUserProperty(String key, String value){

        dataStore.setUserProperty(context, key, value);
    }

    public static String getUserProperty(String key, String defaultValue){
        return dataStore.getUserProperty(context,key,defaultValue);
    }

    public static String getData(String key, String defaultValue){
        return dataStore.getData(context, key, defaultValue);
    }

    private static void closeSession() {
        // Close the session. Events within the next MIN_TIME_BETWEEN_SESSIONS_MILLIS seconds
        // will stay in the session.
        // A startSession call within the next MIN_TIME_BETWEEN_SESSIONS_MILLIS seconds
        // will reopen the session.
        isSessionOpen = false;
    }


    private static void initializeDeviceDetails() {
        deviceDetails = new DeviceDetails(context);
        runOnLogWorker(new Runnable() {

            @Override
            public void run() {
                deviceId = initializeDeviceId();
                deviceDetails.getadditionalDetails();
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"Device details initialization finished");
                }
            }
        });
    }

    private static boolean isValidDeviceId(String deviceId){
        //Filter invalid device ids like empty string etc
        return true;
    }

    private static String initializeDeviceId() {

        SharedPreferences preferences = CommonUtils.getSharedPreferences(context);
        String deviceId = preferences.getString(Constants.Z_PREFKEY_USER_ID, null);
        if (!(TextUtils.isEmpty(deviceId) || isValidDeviceId(deviceId))) {
            return deviceId;
        }

        //TODO check if we can use advertizer id
        String randomId = deviceDetails.generateUUID();
        preferences.edit().putString(Constants.Z_PREFKEY_USER_ID, randomId).commit();
        return randomId;

    }

    private static void startNewSessionIfNeeded(long timestamp) {
        if (!isSessionOpen) {
            long lastEndSessionTime = getEndSessionTime();
            if (timestamp - lastEndSessionTime < Constants.Z_MIN_TIME_BETWEEN_SESSIONS_MILLIS) {
                // Sessions close enough, set sessionId to previous sessionId

                SharedPreferences preferences = CommonUtils.getSharedPreferences(context);
                long previousSessionId = preferences.getLong(Constants.Z_PREFKEY_LAST_END_SESSION_ID,
                        -1);

                if (previousSessionId == -1) {
                    // Invalid session Id, create new sessionId


                    startNewSession(timestamp);
                } else {
                    sessionId = previousSessionId;
                    if(BuildConfig.DEBUG){
                        Log.d(TAG,"starting new session is not required as very close previous session already exists");
                    }
                }
            } else {
                // Sessions not close enough, create new sessionId
                startNewSession(timestamp);
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"starting new session as previous session was not close enough");
                }
            }
        } else {
            long lastEventTime = getLastEventTime();
            if (timestamp - lastEventTime > sessionTimeoutMillis || sessionId == -1) {
                startNewSession(timestamp);
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"starting new session as session timedout");
                }
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"Already previous session is open so not starting another session");
                }
            }
        }
    }

    private static void startNewSession(long timestamp) {
        // Log session start in events
        openSession();
        sessionId = timestamp;
        SharedPreferences preferences = CommonUtils.getSharedPreferences(context);
        preferences.edit().putLong(Constants.Z_PREFKEY_LAST_END_SESSION_ID, sessionId).commit();
        JSONObject apiProperties = new JSONObject();
        try {
            apiProperties.put("special", START_SESSION_EVENT);
        } catch (JSONException e) {
        }
        logEvent(START_SESSION_EVENT, null, apiProperties, timestamp, false);
    }

    public static void uploadEvents() {
        if (!isContextAndApiKeySet("uploadEvents()")) {
            return;
        }

        logWorker.post(new Runnable() {
            @Override
            public void run() {
                updateScheduled.set(false);
                updateServer();
            }
        });

//        if(!updateScheduled.getAndSet(true)){
//            if(BuildConfig.DEBUG){
//                Log.d(TAG,"Upload server is not scheduled ");
//            }
//            logWorker.post(new Runnable() {
//                @Override
//                public void run() {
//                    updateScheduled.set(false);
//                    updateServer();
//                }
//            });
//        }
    }

    public static void syncDataStore(){
        if(BuildConfig.DEBUG){
            Log.d(TAG,"syncDataStore() called, asking logWorker to sync");
        }
        if (!isContextAndApiKeySet("syncDataStore()")) {
            return;
        }

        logWorker.post(new Runnable() {
            @Override
            public void run() {
                _syncDataStore();
            }
        });
    }

    private static void checkAndUpdateDataStore(){
        if(BuildConfig.DEBUG){
            Log.d(TAG,"httpWorker is now making server request to update DataStore");
        }
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();

        postParams.add(new BasicNameValuePair("apiKey", apiKey));
        postParams.add(new BasicNameValuePair("userId", userId));
        postParams.add(new BasicNameValuePair("deviceId", deviceId));
        postParams.add(new BasicNameValuePair("lastDataStoreSynchedTime", dataStore.getDataStoreVersion(context)));

        boolean syncSuccess = false;
        try {
            HttpResponse response = HttpHelper.getHttpHelper().doPost(Constants.Z_DATASTORE_SYNCH_URL,postParams);
            String stringResponse = EntityUtils.toString(response.getEntity());
            final JSONObject jsonResponse = new JSONObject(stringResponse);
            if (jsonResponse.getString("status").equals("OUT_OF_SYNCH")) {
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"DataStore is out of sync, asking logWorker to update local data store");
                }
                syncSuccess = true;
                synchingDataStoreCurrently.set(false);
                logWorker.post(new Runnable() {
                    @Override
                    public void run() {
                        updateDataStore(jsonResponse);
                    }
                });
            }
            else {
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"DataStore already latest version, not updating local DataStore");
                }
            }
        } catch (Exception e) {
            // Just log any other exception so things don't crash on upload
            Log.e(TAG, "Exception:", e);
        } finally {
        }

        if (!syncSuccess) {
            synchingDataStoreCurrently.set(false);
        }

    }

    private static void updateDataStore(JSONObject newDataStore){
        if(BuildConfig.DEBUG){
            Log.d(TAG,"logWorker id updating local data store with the fetched data store");
        }
        try {
            JSONObject variables = newDataStore.getJSONObject("variables");
            for(int i = 0; i<variables.names().length(); i++){
                setData(variables.names().getString(i),variables.getString(variables.names().getString(i)));
            }
            dataStore.setDataStoreVersion(context,newDataStore.getString("lastDataStoreSynchedTime"));
        } catch (Exception e){
            Log.e(TAG, "Exception:", e);
        }
        if(BuildConfig.DEBUG){
            Log.d(TAG,"DataStore update done, we have latest version now.");
        }

        synchingDataStoreCurrently.set(false);
    }

    private static void setData(String key, String value){
        dataStore.setData(context, key, value);
    }

    private static void makeEventUploadPostRequest(String url, String events, final long maxId) {
        if(BuildConfig.DEBUG){
            Log.d(TAG,"httpWorker is uploading events now - "+events);
        }
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();

        //postParams.add(new BasicNameValuePair("v", apiVersionString));
        postParams.add(new BasicNameValuePair("apiKey", apiKey));
        postParams.add(new BasicNameValuePair("eventList", events));
        postParams.add(new BasicNameValuePair("sdkId", Constants.Z_VERSION));
        postParams.add(new BasicNameValuePair("appVersion", deviceDetails.getVersionName()));//
        postParams.add(new BasicNameValuePair("appName", deviceDetails.getVersionName()));
        postParams.add(new BasicNameValuePair("userId", userId));
        postParams.add(new BasicNameValuePair("deviceId", deviceId));

        boolean uploadSuccess = false;
        try {
            HttpResponse response = HttpHelper.getHttpHelper().doPost(url,postParams);
            String stringResponse = EntityUtils.toString(response.getEntity());
            JSONObject jsonResponse = new JSONObject(stringResponse);
            if (jsonResponse.getString("status").equals("success")) {
                uploadSuccess = true;
                logWorker.post(new Runnable() {
                    @Override
                    public void run() {
                        if(BuildConfig.DEBUG){
                            Log.d(TAG,"Events upload successful, trying to delete uploaded events");
                        }
                        DbHelper dbHelper = DbHelper.getDatabaseHelper(context);
                        dbHelper.removeEvents(maxId);
                        uploadingCurrently.set(false);
                        if (dbHelper.getEventCount() > Constants.Z_EVENT_UPLOAD_THRESHOLD) {
                            if(BuildConfig.DEBUG){
                                Log.d(TAG,"Still lot of events exist i.e greater than Z_EVENT_UPLOAD_THRESHOLD, asking logWorker to upload again");
                            }
                            logWorker.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateServer(false);
                                }
                            });
                        }
                    }
                });
            }
        } catch (Exception e) {
            // Just log any other exception so things don't crash on upload
            Log.e(TAG, "Exception:", e);
        } finally {
        }

        if (!uploadSuccess) {
            uploadingCurrently.set(false);
        }

    }

    private static void updateServer(boolean limit) {
        if (!uploadingCurrently.getAndSet(true)) {
            DbHelper dbHelper = DbHelper.getDatabaseHelper(context);
            try {
                long endSessionId = getEndSessionId();
                Pair<Long, JSONArray> pair = dbHelper.getEvents(endSessionId,
                        limit ? Constants.Z_EVENT_UPLOAD_MAX_BATCH_SIZE : -1);
                final long maxId = pair.first;
                final JSONArray events = pair.second;
                if(events.length() == 0){
                    if(BuildConfig.DEBUG){
                        Log.d(TAG,"httpWorker tried uploading events but found zero event, hence not making server request");
                    }
                    uploadingCurrently.set(false);
                    return;
                }
                httpWorker.post(new Runnable() {
                    @Override
                    public void run() {
                        if(BuildConfig.DEBUG){
                            Log.d(TAG,"Asking httpWorker to upload "+events.length()+" events");
                        }
                        makeEventUploadPostRequest(Constants.Z_EVENT_LOG_URL, events.toString(),
                                maxId);
                    }
                });
            } catch (JSONException e) {
                uploadingCurrently.set(false);
                Log.e(TAG, e.toString());
            }
        }
        else {
            if(BuildConfig.DEBUG){
                Log.d(TAG,"Already uploading events to the server hence not uploading now");
            }
        }
    }

    private static void _syncDataStore() {
        if (!synchingDataStoreCurrently.getAndSet(true)) {
            if(BuildConfig.DEBUG){
                Log.d(TAG,"Asking httpWorker to sync datastore");
            }
                httpWorker.post(new Runnable() {
                    @Override
                    public void run() {
                        checkAndUpdateDataStore();
                    }
                });
        }
        else {
            if(BuildConfig.DEBUG){
                Log.d(TAG,"sync datastore is already going on, so not syncing now");
            }
        }
    }



    private static void updateServer() {
        updateServer(true);
    }

    private static void updateServerLater(long delayMillis) {
        if (!updateScheduled.getAndSet(true)) {

            logWorker.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateScheduled.set(false);
                    updateServer();
                }
            }, delayMillis);
        }
    }

    private static void openSession() {
        clearEndSession();
        isSessionOpen = true;
    }

    public static void logEvent(String eventType) {
        logEvent(eventType, null);
    }

    public static void logEvent(String eventType, JSONObject eventProperties) {
        checkedLogEvent(eventType, eventProperties, null, System.currentTimeMillis(), true);
    }

    private static void checkedLogEvent(final String eventType, final JSONObject eventProperties,
                                        final JSONObject apiProperties, final long timestamp, final boolean checkSession) {
        if (TextUtils.isEmpty(eventType)) {
            Log.e(TAG, "Argument eventType cannot be null or blank in logEvent()");
            return;
        }
        if (!isContextAndApiKeySet("logEvent()")) {
            return;
        }
        runOnLogWorker(new Runnable() {
            @Override
            public void run() {
                logEvent(eventType, eventProperties, apiProperties, timestamp, checkSession);
            }
        });
    }

    private static long logEvent(String eventType, JSONObject eventProperties,
                                 JSONObject apiProperties, long timestamp, boolean checkSession) {
        if (checkSession) {
            startNewSessionIfNeeded(timestamp);
        }
        setLastEventTime(timestamp);

        JSONObject event = new JSONObject();
        try {
            event.put("event_type", CommonUtils.replaceWithJSONNull(eventType));

            event.put("timestamp", timestamp);
            event.put("user_id", (userId == null) ? CommonUtils.replaceWithJSONNull(deviceId)
                    : CommonUtils.replaceWithJSONNull(userId));
            event.put("device_id", CommonUtils.replaceWithJSONNull(deviceId));
            event.put("session_id", sessionId);
            event.put("version_name", CommonUtils.replaceWithJSONNull(deviceDetails.getVersionName()));
            event.put("os_name", CommonUtils.replaceWithJSONNull(deviceDetails.getOSName()));
            event.put("os_version", CommonUtils.replaceWithJSONNull(deviceDetails.getOSVersion()));
            event.put("device_brand", CommonUtils.replaceWithJSONNull(deviceDetails.getBrand()));
            event.put("device_manufacturer", CommonUtils.replaceWithJSONNull(deviceDetails.getManufacturer()));
            event.put("device_model", CommonUtils.replaceWithJSONNull(deviceDetails.getModel()));
            event.put("carrier", CommonUtils.replaceWithJSONNull(deviceDetails.getCarrier()));
            event.put("country", CommonUtils.replaceWithJSONNull(deviceDetails.getCountry()));
            event.put("language", CommonUtils.replaceWithJSONNull(deviceDetails.getLanguage()));
            event.put("platform", Constants.Z_PLATFORM);

            JSONObject library = new JSONObject();
            //library.put("name", Constants.LIBRARY);
            library.put("version", Constants.Z_VERSION);
            event.put("library", library);

            apiProperties = (apiProperties == null) ? new JSONObject() : apiProperties;
            Location location = deviceDetails.getMostRecentLocation();
            if (location != null) {
                JSONObject locationJSON = new JSONObject();
                locationJSON.put("lat", location.getLatitude());
                locationJSON.put("lng", location.getLongitude());
                apiProperties.put("location", locationJSON);
            }
            if (deviceDetails.getAdvertisingId() != null) {
                apiProperties.put("androidADID", deviceDetails.getAdvertisingId());
            }

            event.put("api_properties", apiProperties);
            event.put("event_properties", (eventProperties == null) ? new JSONObject()
                    : eventProperties);
            //event.put("user_properties", (userProperties == null) ? new JSONObject()
                    //: userProperties);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        return logEvent(event);
    }

    

    private static long logEvent(JSONObject event) {
        DbHelper dbHelper = DbHelper.getDatabaseHelper(context);
        long eventId = dbHelper.addEvent(event.toString());

        if (dbHelper.getEventCount() >= Constants.Z_EVENT_MAX_COUNT) {
            dbHelper.removeEvents(dbHelper.getNthEventId(Constants.Z_EVENT_REMOVE_BATCH_SIZE));
        }

        if (dbHelper.getEventCount() >= Constants.Z_EVENT_UPLOAD_THRESHOLD) {
            updateServer();
        } else {
            updateServerLater(Constants.Z_EVENT_UPLOAD_PERIOD_MILLIS);
        }
        return eventId;
    }

    private static void runOnLogWorker(Runnable r) {
        if (Thread.currentThread() != logWorker) {
            logWorker.post(r);
        } else {
            r.run();
        }
    }

    

    private static void setUserId(String userId){
        Zinteract.userId = userId;
        CommonUtils.getSharedPreferences(context).edit().putString(Constants.Z_PREFKEY_USER_ID, userId).commit();
    }

    private synchronized static boolean isContextAndApiKeySet(String methodName) {
        if (context == null) {
            Log.e(TAG, "context cannot be null, set context with initialize() before calling "
                    + methodName);
            return false;
        }
        if (TextUtils.isEmpty(apiKey)) {
            Log.e(TAG,
                    "apiKey cannot be null or empty, set apiKey with initialize() before calling "
                            + methodName);
            return false;
        }
        return true;
    }

    private static String getUUID(){
        return UUID.randomUUID().toString();//TODO customize it
    }

    private static void setContext(Context context){
        Zinteract.context = context;
    }

    private static void setApiKey(String apiKey){
        Zinteract.apiKey = apiKey;
    }

    private static long getLastEventTime() {
        return getSharedPreferenceValueByKey(Constants.Z_PREFKEY_LAST_SESSION_TIME);


    }

    private static void setLastEventTime(long timestamp) {
        SharedPreferences preferences = CommonUtils.getSharedPreferences(context);
        preferences.edit().putLong(Constants.Z_PREFKEY_LAST_SESSION_TIME, timestamp).commit();
    }

    private static void clearEndSession() {
        SharedPreferences preferences = CommonUtils.getSharedPreferences(context);
        preferences.edit().remove(Constants.Z_PREFKEY_LAST_END_SESSION_TIME)
                .remove(Constants.Z_PREFKEY_LAST_END_SESSION_ID).commit();
    }

    private static long getEndSessionTime() {
        return getSharedPreferenceValueByKey(Constants.Z_PREFKEY_LAST_END_SESSION_TIME);

    }

    private static long getEndSessionId() {
        return getSharedPreferenceValueByKey(Constants.Z_PREFKEY_LAST_END_SESSION_ID);
    }

    private static long getSharedPreferenceValueByKey(String key){
        SharedPreferences preferences = CommonUtils.getSharedPreferences(context);
        return preferences.getLong(key, -1);
    }


}
