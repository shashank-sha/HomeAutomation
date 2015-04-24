package com.zemosolabs.zinteract.sdk;

/**
 * Created by praveen on 21/01/15.
 */
import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.util.Pair;

class DbHelper extends SQLiteOpenHelper {

    private static DbHelper instance;
    private static final String TAG = "com.zemoso.zint.DbHelpr";

    private static final String EVENT_TABLE_NAME = Constants.Z_DB_EVENT_TABLE_NAME;
    private static final String PROMOTION_TABLE_NAME = Constants.Z_DB_PROMOTION_TABLE_NAME;
    private static final String A_B_TEST_TABLE_NAME = Constants.Z_DB_A_B_TEST_TABLE_NAME;

    private static final String ID_FIELD = Constants.Z_DB_EVENT_ID_FIELD_NAME;
    private static final String EVENT_FIELD = Constants.Z_DB_EVENT_EVENTS_FIELD_NAME;

    private static final String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS "
            + EVENT_TABLE_NAME + " ("
            + Constants.Z_DB_EVENT_ID_FIELD_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Constants.Z_DB_EVENT_EVENTS_FIELD_NAME + " TEXT);";

    private static final String CREATE_PROMOTIONS_TABLE = "CREATE TABLE IF NOT EXISTS "
            + PROMOTION_TABLE_NAME + " ("
            + Constants.Z_DB_PROMOTION_ID_FIELD_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, campaign_id TEXT, screen_id TEXT, status INTEGER DEFAULT 0,"
            + Constants.Z_DB_PROMOTION_PROMOTION_FIELD_NAME + " TEXT);";

    private static final String CREATE_USERPROPERTIES_TABLE = "CREATE TABLE IF NOT EXISTS "
            + Constants.Z_DB_USER_PROPERTIES_TABLE_NAME + " ("
            + Constants.Z_DB_USER_PROPERTIES_ID_FIELD_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, propname TEXT, propvalue TEXT, synched INTEGER DEFAULT 0 );";

    private static final String CREATE_UNIQUE_INDEX_ON_CAMPAIGN_ID = "CREATE UNIQUE INDEX campaign_id_idx" +
            " on "+ PROMOTION_TABLE_NAME+" (campaign_id);";

    private static final String CREATE_UNIQUE_INDEX_ON_PROPNAME = "CREATE UNIQUE INDEX propname_idx" +
            " on "+ Constants.Z_DB_USER_PROPERTIES_TABLE_NAME+" (propname);";

    private static final String CREATE_ABTEST_TABLE = "CREATE TABLE IF NOT EXISTS "
            +A_B_TEST_TABLE_NAME + "(" + Constants.Z_DB_A_B_TEST_ID_FIELD_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, campaign_id TEXT, screen_id TEXT, "
            +Constants.Z_DB_A_B_TEST_A_B_TESTS_FIELD_NAME + " MEDIUMTEXT);";

    private static final String CREATE_UNIQUE_INDEX_ON_SCREEN_ID = "CREATE UNIQUE INDEX screen_idx" +
            " on "+ Constants.Z_DB_A_B_TEST_TABLE_NAME+" (screen_id);";

    private File file;

    static DbHelper getDatabaseHelper(Context context) {
        if (instance == null) {
            instance = new DbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DbHelper(Context context) {
        super(context, Constants.Z_DB_NAME, null, Constants.Z_DB_VERSION);
        file = context.getDatabasePath(Constants.Z_DB_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // INTEGER PRIMARY KEY AUTOINCREMENT guarantees that all generated values
        // for the field will be monotonically increasing and unique over the
        // lifetime of the table, even if rows get removed
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_PROMOTIONS_TABLE);
        db.execSQL(CREATE_USERPROPERTIES_TABLE);
        db.execSQL(CREATE_ABTEST_TABLE);
        db.execSQL(CREATE_UNIQUE_INDEX_ON_CAMPAIGN_ID);
        db.execSQL(CREATE_UNIQUE_INDEX_ON_PROPNAME);
        db.execSQL(CREATE_UNIQUE_INDEX_ON_SCREEN_ID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PROMOTION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.Z_DB_USER_PROPERTIES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + A_B_TEST_TABLE_NAME);
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_PROMOTIONS_TABLE);
        db.execSQL(CREATE_USERPROPERTIES_TABLE);
        db.execSQL(CREATE_ABTEST_TABLE);
        db.execSQL(CREATE_UNIQUE_INDEX_ON_CAMPAIGN_ID);
        db.execSQL(CREATE_UNIQUE_INDEX_ON_PROPNAME);
        db.execSQL(CREATE_UNIQUE_INDEX_ON_SCREEN_ID);
    }


    //Events related
    synchronized long addEvent(String event) {
        long result = -1;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(EVENT_FIELD, event);
            result = db.insert(EVENT_TABLE_NAME, null, contentValues);
            if (result == -1) {
                Log.w(TAG, "Insert failed");
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "addEvent failed", e);
            // Not much we can do, just start fresh
            delete();
        } finally {
            close();
        }
        return result;
    }

    synchronized Pair<Long, JSONArray> getEvents(long lessThanId, int limit) throws JSONException {
        long maxId = -1;
        JSONArray events = new JSONArray();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(EVENT_TABLE_NAME, new String[] { ID_FIELD, EVENT_FIELD },
                    lessThanId >= 0 ? ID_FIELD + " < " + lessThanId : null, null, null,
                    null, ID_FIELD + " ASC", limit >= 0 ? "" + limit : null);

            while (cursor.moveToNext()) {
                long eventId = cursor.getLong(0);
                String event = cursor.getString(1);

                JSONObject obj = new JSONObject(event);
                obj.put("event_id", eventId);
                events.put(obj);

                maxId = eventId;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getEvents failed", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return new Pair<Long, JSONArray>(maxId, events);
    }

    synchronized long getEventCount() {
        long numberRows = 0;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + EVENT_TABLE_NAME;
            SQLiteStatement statement = db.compileStatement(query);
            numberRows = statement.simpleQueryForLong();
        } catch (SQLiteException e) {
            Log.e(TAG, "getNumberRows failed", e);
        } finally {
            close();
        }
        return numberRows;
    }

    synchronized long getNthEventId(long n) {
        long nthEventId = -1;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT " + ID_FIELD + " FROM " + EVENT_TABLE_NAME
                    + " LIMIT 1 OFFSET " + (n - 1);
            SQLiteStatement statement = db.compileStatement(query);
            nthEventId = -1;
            try {
                nthEventId = statement.simpleQueryForLong();
            } catch (SQLiteDoneException e) {
                Log.w(TAG, e);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getNthEventId failed", e);
        } finally {
            close();
        }
        return nthEventId;
    }

    synchronized void removeEvents(long maxId) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(EVENT_TABLE_NAME, ID_FIELD + " <= " + maxId, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "removeEvents failed", e);
        } finally {
            close();
        }
    }

    synchronized void removeEvent(long id) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(EVENT_TABLE_NAME, ID_FIELD + " = " + id, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "removeEvent failed", e);
        } finally {
            close();
        }
    }

    //Promotions related
    synchronized long addPromotion(String promotion, String campaign_id, String screen_id) {
        long result = -1;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.Z_DB_PROMOTION_PROMOTION_FIELD_NAME, promotion);
            contentValues.put("campaign_id", campaign_id);
            contentValues.put("screen_id", screen_id);

            result = db.insertWithOnConflict(PROMOTION_TABLE_NAME, null, contentValues,5);
            if (result == -1) {
                Log.w(TAG, "Insert failed");
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "addPromotion failed", e);
            // Not much we can do, just start fresh
            delete();
        } finally {
            close();
        }
        return result;
    }

    synchronized void removeSeenPromotions() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(PROMOTION_TABLE_NAME, "status = 1", null);

        } catch (SQLiteException e) {
            Log.e(TAG, "removePromotion failed", e);
        } finally {
            close();
        }
    }

    synchronized void markPromotionAsSeen(String campaign_id) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("status", 1);
            db.update(PROMOTION_TABLE_NAME,contentValues,"campaign_id = ?",new String[]{campaign_id});
        } catch (SQLiteException e) {
            Log.e(TAG, "markPromotionAsSeen failed", e);
        } finally {
            close();
        }
    }

    synchronized JSONObject getPromotionforScreen(String screen_id){
        JSONObject promotion = new JSONObject();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(PROMOTION_TABLE_NAME, null,"screen_id = ? AND status = ?", new String[]{screen_id,"0"}, null,
                    null, Constants.Z_DB_PROMOTION_ID_FIELD_NAME + " DESC", "1");

            while (cursor.moveToNext()) {
                String p = cursor.getString(4);

                promotion = new JSONObject(p);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getPromotionforScreen failed", e);
        } catch (Exception e){
            Log.e(TAG, " getPromotionforScreen falied", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return promotion;
    }

    //User Properties related

    synchronized long addUserProperties(JSONObject userProperties) {
        long result = -1;
        try {
            SQLiteDatabase db = getWritableDatabase();

            for(int i=0; i < userProperties.length(); i++){
                ContentValues contentValues = new ContentValues();
                contentValues.put("propname", userProperties.names().getString(i));
                contentValues.put("propvalue", userProperties.getString(userProperties.names().getString(i)));
                result = db.insertWithOnConflict(Constants.Z_DB_USER_PROPERTIES_TABLE_NAME, null, contentValues,5);
                if (result == -1) {
                    Log.w(TAG, "addUserProperty Insert failed");
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "addUserProperty failed", e);
            // Not much we can do, just start fresh
            delete();
        } catch(Exception e){
            Log.e(TAG,"Exception : "+e);
        } finally {
            close();
        }
        return result;
    }

    synchronized String getUserProperty(String propname){
        String value = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(Constants.Z_DB_USER_PROPERTIES_TABLE_NAME, null,"propname = ?", new String[]{propname}, null,
                    null, Constants.Z_DB_USER_PROPERTIES_ID_FIELD_NAME + " DESC", "1");

            while (cursor.moveToNext()) {
                value = cursor.getString(2);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getUserProperty failed", e);
        } catch (Exception e){
            Log.e(TAG, " getUserProperty falied", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return value;
    }

    synchronized JSONObject getUserProperties(){
        JSONObject userProperties = new JSONObject();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(Constants.Z_DB_USER_PROPERTIES_TABLE_NAME, null,null, null, null,
                    null, Constants.Z_DB_USER_PROPERTIES_ID_FIELD_NAME + " DESC", null);

            while (cursor.moveToNext()) {
                String key = cursor.getString(1);
                String value = cursor.getString(2);
                userProperties.put(key,value);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getUserProperties failed", e);
        } catch (Exception e){
            Log.e(TAG, " getUserProperties falied", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return userProperties;
    }

    private void delete() {
        try {
            close();
            file.delete();
        } catch (SecurityException e) {
            Log.e(TAG, "delete failed", e);
        }
    }

    synchronized long addABTest(String change, String campaign_id, String screen_id) {
        long result = -1;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.Z_DB_A_B_TEST_A_B_TESTS_FIELD_NAME, change);
            contentValues.put("campaign_id", campaign_id);
            contentValues.put("screen_id", screen_id);

            result = db.insertWithOnConflict(A_B_TEST_TABLE_NAME, null, contentValues,5);
            if (result == -1) {
                Log.w(TAG, "Insert failed");
            }

        } catch (SQLiteException e) {
            Log.e(TAG, "addPromotion failed", e);
            // Not much we can do, just start fresh
            delete();
        } finally {
            close();
        }
        return result;
    }
    synchronized JSONObject getScreenFixesFor(String screen_id){
        JSONObject promotion = new JSONObject();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
             cursor = db.query(A_B_TEST_TABLE_NAME, null,"screen_id = ?", new String[]{screen_id}, null,
                    null, Constants.Z_DB_A_B_TEST_ID_FIELD_NAME + " DESC", "1");

            while (cursor.moveToNext()) {
                String p = cursor.getString(3);

                promotion = new JSONObject(p);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getScreenFixesFor() failed", e);
        } catch (Exception e){
            Log.e(TAG, " getScreenFixesFor() falied", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return promotion;
    }
}