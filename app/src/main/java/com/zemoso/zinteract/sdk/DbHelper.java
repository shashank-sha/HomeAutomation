package com.zemoso.zinteract.sdk;

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
    private static final String TAG = "com.zemoso.zinteract.DbHelper";

    private static final String EVENT_TABLE_NAME = Constants.Z_DB_EVENT_TABLE_NAME;
    private static final String PROMOTION_TABLE_NAME = Constants.Z_DB_PROMOTION_TABLE_NAME;

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

    private static final String CREATE_UNIQUE_INDEX_ON_CAMPAIGN_ID = "CREATE UNIQUE INDEX campaign_id_idx" +
            " on "+ PROMOTION_TABLE_NAME+" (campaign_id);";

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
        db.execSQL(CREATE_UNIQUE_INDEX_ON_CAMPAIGN_ID);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PROMOTION_TABLE_NAME);
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_PROMOTIONS_TABLE);
        db.execSQL(CREATE_UNIQUE_INDEX_ON_CAMPAIGN_ID);
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

    private void delete() {
        try {
            close();
            file.delete();
        } catch (SecurityException e) {
            Log.e(TAG, "delete failed", e);
        }
    }

}