/*
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.HandlerThread;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

*/
/**
 * Created by vedaprakash on 23/3/15.
 *//*

public class TestInAppMessagesUIStructureAndDismissClicks {
    DbHelper sqLiteOpenHelper;
    Activity currentActivity;
    DialogFragment currentDialogFragment;
    ShadowIntent intent;
    ArrayList<JSONObject> currentlyUsedArrayListOfPromotions;

    static final String screenLabel1 = "com.zemoso.zinteract.ZinteractSampleApp.MainActivity";
    static final String screenLabel2 = "com.zemoso.zinteract.ZinteractSampleApp.Activity2";
    static final String screenLabel3 = "com.zemoso.zinteract.ZinteractSampleApp.Activity3";
    static final String screenLabel4 = "com.zemoso.zinteract.ZinteractSampleApp.Activity4";
    static final String screenLabel5 = "com.zemoso.zinteract.ZinteractSampleApp.Activity5";

    static final int numOfScreens = 5;

    boolean firstTime = true;

    JSONArray allPromotions;

    ArrayList<JSONObject> promotionsForMainActivity = new ArrayList<JSONObject>();
    ArrayList<JSONObject> promotionsForActivity2 = new ArrayList<JSONObject>();
    ArrayList<JSONObject> promotionsForActivity3 = new ArrayList<JSONObject>();
    ArrayList<JSONObject> promotionsForActivity4 = new ArrayList<JSONObject>();
    ArrayList<JSONObject> promotionsForActivity5 = new ArrayList<JSONObject>();

    ArrayList<Activity> pausedActivities = new ArrayList<Activity>();


    ProtocolVersion httpProtocolVersion;
    HttpResponse simpleSuccessResponse;
    static HttpResponse promotionsResponse;

    SharedPreferences sharedPreferences;
    String lastCampaignSyncTime;

    HandlerThread logWorker,httpWorker;
    ShadowLooper logWorkingLooper,httpWorkingLooper;

    private final static String promotionsForScreensJSON = "[{\"campaignId\": \"campaign1\",\"screenId\": \"com.zemoso.zinteract.ZinteractSampleApp.MainActivity\",\"template\": {\"title\": \"Season sale! Heavy discounts!!!\", \"message\": \"Buy the stuff now and save a loot of money and use that money to buy some more stuff!!!\", \"imageUrl\": \"http://news.bbcimg.co.uk/media/images/81539000/jpg/_81539447_95ca831d-7a1d-4b02-b3ca-0c9968649937.jpg\",\"onClickUrl\": \"http://www.google.com\",\"templateType\": \"REGULAR\",\"definition\": { \"actionType\": \"LINK\", \"actionButton\": {\"url\": \"http://www.flipkart.com\",\"buttonText\": \"GO\"},\"dismissButtonText\": \"NO THANKS\"}}}," +
            "{\"campaignId\": \"campaign2\",\"screenId\": \"com.zemoso.zinteract.ZinteractSampleApp.Activity2\",\"template\": {\"title\": \"Season sale! Heavy discounts!!!\",\"message\": \"Buy the stuff now and save a loot of money and use that money to buy some more stuff!!! JUST RATE OUR APP\", \"imageUrl\": \"http://news.bbcimg.co.uk/media/images/81539000/jpg/_81539447_95ca831d-7a1d-4b02-b3ca-0c9968649937.jpg\", \"onClickUrl\": \"http://www.google.com\", \"templateType\": \"REGULAR\", \"definition\": {\"actionType\": \"RATE\", \"actionButton\": {\"buttonText\": \"RATE US\"},\"dismissButtonText\": \"DON'T ASK ME AGAIN\",\"remindLaterButtonText\": \"REMIND ME LATER\"}}}," +
            "{\"campaignId\": \"campaign3\",\"screenId\": \"com.zemoso.zinteract.ZinteractSampleApp.Activity3\",\"template\": {\"title\": \"Season sale! Heavy discounts!!!\",\"message\": \"Invite your friends now and save a loot of money and use that money to buy some more stuff!!!\",\"imageUrl\": \"http://news.bbcimg.co.uk/media/images/81539000/jpg/_81539447_95ca831d-7a1d-4b02-b3ca-0c9968649937.jpg\",\"onClickUrl\": \"http://www.google.com\",\"templateType\": \"REGULAR\",\"definition\": {\"actionType\": \"SHARE\",\"actionButton\": {\"shareText\": \"This app is awesome!!! Check it out! Use promocode:sdeidk\",\"buttonText\": \"INVITE\"},\"dismissButtonText\": \"NO THANKS\"}}}," +
            "{\"campaignId\": \"campaign4\",\"screenId\": \"com.zemoso.zinteract.ZinteractSampleApp.Activity4\",\"template\": {\"title\": \"Season sale! Heavy discounts!!!\",\"message\": \"Buy the stuff now and save a loot of money and use that money to buy some more stuff!!!\",\"imageUrl\": \"http://news.bbcimg.co.uk/media/images/81539000/jpg/_81539447_95ca831d-7a1d-4b02-b3ca-0c9968649937.jpg\",\"onClickUrl\": \"http://www.google.com\",\"templateType\": \"REGULAR\",\"definition\": {\"actionType\":\"NONE\",\"dismissButtonText\": \"GOT IT\"}}}]";

    @Before
    public void setUp() {

        // Confirm the FakeHttpLayer is at its default state without and ResponseRules set.
        System.out.println("Checking if the fakeHttpLayer is functioning properly");
        FakeHttpLayer fakeHttpLayer = Robolectric.getFakeHttpLayer();
        assertFalse(fakeHttpLayer.hasPendingResponses());
        assertFalse(fakeHttpLayer.hasRequestInfos());
        assertFalse(fakeHttpLayer.hasResponseRules());
        assertNull(fakeHttpLayer.getDefaultResponse());

        //Getting the database assert there is no database stored values of promotions initially
        System.out.println("Confirming that the database doesn't contain any inAppPromos prior to starting the test");
        sqLiteOpenHelper = DbHelper.getDatabaseHelper(Robolectric.application);
        assertEquals(new JSONObject().toString(), sqLiteOpenHelper.getPromotionforScreen(screenLabel1).toString());
        assertEquals(new JSONObject().toString(), sqLiteOpenHelper.getPromotionforScreen(screenLabel2).toString());
        assertEquals(new JSONObject().toString(), sqLiteOpenHelper.getPromotionforScreen(screenLabel3).toString());
        assertEquals(new JSONObject().toString(), sqLiteOpenHelper.getPromotionforScreen(screenLabel4).toString());
        assertEquals(new JSONObject().toString(), sqLiteOpenHelper.getPromotionforScreen(screenLabel5).toString());
        assertEquals(0, sqLiteOpenHelper.getUserProperties().length());

        //set up the promotions in Array for screens;
        System.out.println("Sorting promotions in arrays screenwise to consolidate with DB storage during test");
        // setUpPromotionsScreenWise();


        //get SharedPreference file
        System.out.println("Getting the SharedPreference file");
        sharedPreferences = Robolectric.application.
                getSharedPreferences(Constants.Z_SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        // Set up the HttpResponses for the Z_INIT_LOG_URL
        System.out.println("Setting up the HttpResponse for HttpRequest to promotions page");
        JSONArray promotions = null;
        JSONObject promotionsUpdate = new JSONObject();
        lastCampaignSyncTime = getCurrentDateTime(System.currentTimeMillis());

        try {
            promotions = new JSONArray(promotionsForScreensJSON);
            promotionsUpdate.put("promotions", promotions);
            promotionsUpdate.put("lastCampaignSynchedTime",lastCampaignSyncTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpProtocolVersion = new ProtocolVersion("HTTP", 1, 1);
        simpleSuccessResponse = new BasicHttpResponse(httpProtocolVersion, 200, "OK");
        promotionsResponse = new BasicHttpResponse(httpProtocolVersion,200,"OK");

        try {
            promotionsResponse.setEntity(new StringEntity(promotionsUpdate.toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Setting up and confirming Robolectric's FakeHttpLayer is in effect
        System.out.println("Setting default responses for all other HttpRequests");
        Robolectric.addHttpResponseRule(Constants.Z_INIT_LOG_URL, simpleSuccessResponse);
        Robolectric.addHttpResponseRule(Constants.Z_START_SESSION_EVENT_LOG_URL,simpleSuccessResponse);
        Robolectric.addHttpResponseRule(Constants.Z_EVENT_LOG_URL,simpleSuccessResponse);
        Robolectric.addHttpResponseRule(Constants.Z_DATASTORE_SYNCH_URL,simpleSuccessResponse);
        Robolectric.addHttpResponseRule(Constants.Z_USER_PROPERTIES_LOG_URL, simpleSuccessResponse);
        Robolectric.addHttpResponseRule(Constants.Z_PROMOTION_URL, promotionsResponse);
        assertTrue(Robolectric.getFakeHttpLayer().isInterceptingHttpRequests());
        assertTrue(Robolectric.getFakeHttpLayer().hasResponseRules());
    }

    public void test(TestInAppMessages testInAppMessages) {

    }
}
*/
