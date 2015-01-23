package com.zemoso.zinteract.sdk;

/**
 * Created by praveen on 19/01/15.
 */
public class Constants {
    public static final String Z_PLATFORM = "Android";
    public static final String Z_VERSION = "0.1";
    public static final String Z_API_VERSION = "0.1";
    public static final String Z_NAMESPACE = Constants.class.getPackage().getName();
    public static final String Z_SHARED_PREFERENCE_FILE_NAME = Z_NAMESPACE;
    public static final String Z_SHARED_PREFERENCE_USER_PROPERTIES_FILE_NAME = Z_NAMESPACE+".userproperties";
    public static final String Z_SHARED_PREFERENCE_DATASTORE_FILE_NAME = Z_NAMESPACE+".datastore";
    public static final String Z_PREFKEY_USER_ID = Z_NAMESPACE+"userId";

    //DataStore
    public static final String Z_PREFKEY_LAST_DATASTORE_SYNC_TIME = Z_NAMESPACE+".last_datastore_sync_time";
    public static final String Z_PREFKEY_LAST_DATASTORE_VERSION = Z_NAMESPACE+".last_datastore_version";
    public static final String Z_DATASTORE_SYNCH_URL = "http://private-anon-1d8d31179-dummysdkapi.apiary-mock.com/fetchDatastore";



    //session related
    public static final long Z_SESSION_TIMEOUT = 30*1000;//in milliseconds
    public static final String Z_SESSION_START_EVENT = "session_start";
    public static final String Z_INIT_EVENT = "init";
    public static final String Z_SESSION_END_EVENT = "session_end";
    public static final long Z_MIN_TIME_BETWEEN_SESSIONS_MILLIS = 15 * 1000; // 15s
    public static final String Z_PREFKEY_LAST_SESSION_TIME = Z_NAMESPACE+".last_session_time";
    public static final String Z_PREFKEY_LAST_END_SESSION_TIME = Z_NAMESPACE+".last_end_session_time";
    public static final String Z_PREFKEY_LAST_END_SESSION_ID = Z_NAMESPACE+".last_end_session_id";


    //DB related constants
    public static final String Z_DB_NAME = Z_NAMESPACE;
    public static final int Z_DB_VERSION = 1;
    public static final String Z_DB_EVENT_TABLE_NAME = "events";
    public static final String Z_DB_EVENT_ID_FIELD_NAME = "id";
    public static final String Z_DB_EVENT_EVENTS_FIELD_NAME = "event";

    public static final String Z_DB_PROMOTION_TABLE_NAME = "promotions";
    public static final String Z_DB_PROMOTION_ID_FIELD_NAME = "id";
    public static final String Z_DB_PROMOTION_PROMOTION_FIELD_NAME = "promotion";


    //Events related
    public static final long Z_EVENT_UPLOAD_THRESHOLD = 10;
    public static final int Z_EVENT_UPLOAD_MAX_BATCH_SIZE = 15;
    public static final String Z_EVENT_LOG_URL = "http://private-anon-1d8d31179-dummysdkapi.apiary-mock.com/sendEvent";
    public static final String Z_START_SESSION_EVENT_LOG_URL = "http://private-anon-1d8d31179-dummysdkapi.apiary-mock.com/sessionStart";
    public static final String Z_INIT_LOG_URL = "http://private-anon-1d8d31179-dummysdkapi.apiary-mock.com/init";

    //Promotions related
    public static final String Z_PROMOTION_URL = "http://private-anon-1d8d31179-dummysdkapi.apiary-mock.com/fetchPromo";


    public static final int Z_EVENT_MAX_COUNT = 100;
    public static final long Z_EVENT_REMOVE_BATCH_SIZE = 100;
    public static final long Z_EVENT_UPLOAD_PERIOD_MILLIS = 15*1000;

}
