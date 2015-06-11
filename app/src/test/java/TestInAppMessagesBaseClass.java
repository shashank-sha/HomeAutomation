import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.HandlerThread;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemoso.zinteract.ZinteractSampleApp.Activity2;

import com.zemoso.zinteract.ZinteractSampleApp.Activity3;
import com.zemoso.zinteract.ZinteractSampleApp.Activity4;
import com.zemoso.zinteract.ZinteractSampleApp.Activity5;
import com.zemoso.zinteract.ZinteractSampleApp.MainActivity;
import com.zemoso.zinteract.ZinteractSampleApp.R;
import com.zemosolabs.zinteract.sdk.ZTarget;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowImageView;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.util.ActivityController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;



@Config(reportSdk = 18,emulateSdk = 18)
@RunWith(CustomRobolectricRunner.class)
abstract public class TestInAppMessagesBaseClass {
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

    JSONArray allPromotions;

    ArrayList<JSONObject> promotionsForMainActivity = new ArrayList<JSONObject>();
    ArrayList<JSONObject> promotionsForActivity2 = new ArrayList<JSONObject>();
    ArrayList<JSONObject> promotionsForActivity3 = new ArrayList<JSONObject>();
    ArrayList<JSONObject> promotionsForActivity4 = new ArrayList<JSONObject>();
    ArrayList<JSONObject> promotionsForActivity5 = new ArrayList<JSONObject>();

    ActivityController mainActivityController;
    ActivityController activity2Controller;
    ActivityController activity3Controller;
    ActivityController activity4Controller;
    ActivityController activity5Controller;

    ProtocolVersion httpProtocolVersion;
    HttpResponse simpleSuccessResponse;
    static HttpResponse promotionsResponse;

    SharedPreferences sharedPreferences;
    String lastCampaignSyncTime;

    HandlerThread logWorker,httpWorker;
    ShadowLooper logWorkingLooper,httpWorkingLooper;

    private static String promotionsForScreensJSON;

    @Before
    public void setUp() {
        ZTarget.robolectricTesting = true;
        //Load Campaign String from file

        promotionsForScreensJSON = readFile("app/src/test/java/CampaignTestCase.txt");

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

    @Test
    abstract public void test();

    @After
    public void closeUP(){
        ZTarget.robolectricTesting = false;
    }

    //Test methods
    private void printSizesOfArrayLists(){
        System.out.println("MainActivity: " + promotionsForMainActivity.size());
        System.out.println("Activity2: " + promotionsForActivity2.size());
        System.out.println("Activity3: "+promotionsForActivity3.size());
        System.out.println("Activity4: "+promotionsForActivity4.size());
        System.out.println("Activity5: "+promotionsForActivity5.size());
        Cursor cursor =null;
        try {
            SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
            cursor = db.query(Constants.Z_DB_PROMOTION_TABLE_NAME, null,"screen_id = ? AND status = ?", new String[]{screenLabel1,"0"}, null,
                    null, Constants.Z_DB_PROMOTION_ID_FIELD_NAME + " DESC", null);

            while (cursor.moveToNext()) {
                String p = cursor.getString(4);
                System.out.println(p);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            sqLiteOpenHelper.close();
        }

    }

    void setupPromotionsInitially(){
        setUpPromotionsScreenWise();
        if(mainActivityController==null) {
            mainActivityController = Robolectric.buildActivity(MainActivity.class).attach().create().start().resume();
        }
        else{
            mainActivityController.destroy();
            mainActivityController = Robolectric.buildActivity(MainActivity.class).attach().create().start().resume();
        }

        instantiateLogAndHttpWorkers();
        executeAfterResumeLogAndHttpTasks();

        //DB storage is consistent
        validateDb();

        Robolectric.addHttpResponseRule(Constants.Z_PROMOTION_URL, promotionsResponse);

        Robolectric.runUiThreadTasks();

        mainActivityController.pause();

    }
    private void validateDb(){
        try {
            if(promotionsForMainActivity.size()>0){
                assertNotNull(sqLiteOpenHelper.getPromotionforScreen(screenLabel1));
            }else{
                assertEquals(sqLiteOpenHelper.getPromotionforScreen(screenLabel1).toString(), new JSONObject().toString());
            }
            if(promotionsForActivity2.size()>0){
                assertNotNull(sqLiteOpenHelper.getPromotionforScreen(screenLabel2));
            }else{
                assertEquals(sqLiteOpenHelper.getPromotionforScreen(screenLabel2).toString(), new JSONObject().toString());
            }
            if(promotionsForActivity3.size()>0){
                assertNotNull(sqLiteOpenHelper.getPromotionforScreen(screenLabel3));
            }else{
                assertEquals(sqLiteOpenHelper.getPromotionforScreen(screenLabel3).toString(), new JSONObject().toString());
            }
            if(promotionsForActivity4.size()>0){
                assertNotNull(sqLiteOpenHelper.getPromotionforScreen(screenLabel4));
            }else{
                assertEquals(sqLiteOpenHelper.getPromotionforScreen(screenLabel4).toString(), new JSONObject().toString());
            }
            if(promotionsForActivity5.size()>0){
                assertNotNull(sqLiteOpenHelper.getPromotionforScreen(screenLabel5));
            }else{
                assertEquals(sqLiteOpenHelper.getPromotionforScreen(screenLabel5).toString(), new JSONObject().toString());
            }
            System.out.println("DB updation validated");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    boolean inAppPromosPending(){
        if(promotionsForMainActivity.size()>0 || promotionsForActivity2.size()>0 ||
                promotionsForActivity3.size()>0 || promotionsForActivity4.size()>0 ||
                promotionsForActivity5.size()>0){
            return true;
        }
        return false;
    }

    void resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity(String action, String screenLabel){
        int nextButtonId;
        Class nextActivity;
        JSONObject promotion;
        System.out.println("ResumeActivityDoAction: "+"Creating the corresponding activity");
        switch(screenLabel){
            case screenLabel1:
                if(mainActivityController==null){
                    mainActivityController = Robolectric.buildActivity(MainActivity.class).attach().create().start().resume().visible();
                }
                else{
                    mainActivityController.resume().visible();

                }
                currentActivity  = (MainActivity)mainActivityController.get();

                nextActivity = Activity2.class;
                nextButtonId = R.id.button1to2;
                if(promotionsForMainActivity.isEmpty()){
                    System.out.println("ResumeActivityDoAction: "+"No Promotions for MainActivity");
                    return;
                }
                currentlyUsedArrayListOfPromotions = promotionsForMainActivity;
                promotion = promotionsForMainActivity.get(0);
                break;
            case screenLabel2:
                if(activity2Controller==null){
                    activity2Controller = Robolectric.buildActivity(Activity2.class).attach().create().start().resume().visible();
                }
                else{
                    activity2Controller.resume().visible();
                }
                currentActivity  = (Activity2)activity2Controller.get();
                nextActivity = Activity3.class;
                nextButtonId = R.id.button2to3;
                if(promotionsForActivity2.isEmpty()){
                    System.out.println("ResumeActivityDoAction: "+"No Promotions for Activity2");
                    return;
                }
                currentlyUsedArrayListOfPromotions = promotionsForActivity2;
                promotion = promotionsForActivity2.get(0);
                break;
            case screenLabel3:
                if(activity3Controller==null){
                    activity3Controller = Robolectric.buildActivity(Activity3.class).attach().create().start().resume().visible();
                }
                else{
                    activity3Controller.resume().visible();
                }
                currentActivity  = (Activity3)activity3Controller.get();

                nextActivity = Activity4.class;
                nextButtonId = R.id.button3to4;
                if(promotionsForActivity3.isEmpty()){
                    System.out.println("ResumeActivityDoAction: "+"No Promotions for Activity3");
                    return;
                }
                currentlyUsedArrayListOfPromotions = promotionsForActivity3;
                promotion = promotionsForActivity3.get(0);
                break;
            case screenLabel4:
                if(activity4Controller==null){
                    activity4Controller = Robolectric.buildActivity(Activity4.class).attach().create().start().resume().visible();
                }
                else{
                    activity4Controller.resume().visible();
                }
                currentActivity  = (Activity4)activity4Controller.get();

                nextActivity = Activity5.class;
                nextButtonId = R.id.button4to5;
                if(promotionsForActivity4.isEmpty()){
                    System.out.println("ResumeActivityDoAction: "+"No Promotions for Activity4");
                    return;
                }
                currentlyUsedArrayListOfPromotions = promotionsForActivity4;
                promotion = promotionsForActivity4.get(0);
                break;
            case screenLabel5:
                if(activity5Controller==null){
                    activity5Controller = Robolectric.buildActivity(Activity5.class).attach().create().start().resume().visible();
                }
                else{
                    activity5Controller.resume().visible();
                }
                currentActivity  = (Activity5)activity5Controller.get();

                nextActivity = MainActivity.class;
                nextButtonId = R.id.button5to1;
                if(promotionsForActivity5.isEmpty()){
                    System.out.println("ResumeActivityDoAction: "+"No Promotions for Activity5");
                    return;
                }
                currentlyUsedArrayListOfPromotions = promotionsForActivity5;
                promotion = promotionsForActivity5.get(0);
                break;
            default:
                assertTrue(false);
                return;
        }
        assertNotNull(currentActivity);
        System.out.println("ResumeActivityDoAction: "+"Forcing logWorker and httpWorker to finish their queued tasks");
        executeAfterResumeLogAndHttpTasks();

        //DB storage is consistent
        System.out.println("ResumeActivityDoAction: "+"Consolidating the count in DB and ArrayList of promotions being maintained to verify the results");
        validateDb();

        System.out.println("ResumeActivityDoAction: "+"Force the queued tasks on UI thread to execute");
        Robolectric.runUiThreadTasks();

        System.out.println("ResumeActivityDoAction: "+"Verify the InAppPromos for the corresponding action:"+action);
        currentDialogFragment = (DialogFragment)currentActivity.getFragmentManager().findFragmentByTag("dialog");
        assertNotNull(currentDialogFragment);
        //validate required action
        switch(action){
            case "UIdismiss":
                validateUIStructurefor(promotion);
                break;
            case "action":
                validateActionClicks(promotion);
                break;
            case "neutral":
                validateNeutralClicks(promotion);
                break;
            case "imageClick":
                validateImageClicks(promotion);
                break;
            default:
                assertTrue(false);
                return;
        }


        assertNull(currentActivity.getFragmentManager().findFragmentByTag("dialog"));

        currentActivity.findViewById(nextButtonId).performClick();
        intent = Robolectric.shadowOf(Robolectric.shadowOf(currentActivity).getNextStartedActivity());
        assertEquals(intent.getIntentClass(),nextActivity);

    }

    private void validateUIStructurefor(JSONObject promotion){
        int messageTextId,messageTitleId,imageViewId,actionButtonId,dismissButtonId;
        try {
            JSONObject template = promotion.getJSONObject("template");

            switch (template.getJSONObject("definition").getString("actionType").toLowerCase()) {
                case "link":
                    messageTextId = R.id.message_action;
                    messageTitleId = R.id.title_action;
                    imageViewId = R.id.img_container_action;
                    actionButtonId = R.id.action_button_action;
                    dismissButtonId = R.id.dismiss_button_action;
                    break;
                case "share":
                    messageTextId = R.id.message_action;
                    messageTitleId = R.id.title_action;
                    imageViewId = R.id.img_container_action;
                    actionButtonId = R.id.action_button_action;
                    dismissButtonId = R.id.dismiss_button_action;
                    break;
                case "rate":
                    messageTextId = R.id.message_rate_me;
                    messageTitleId = R.id.title_rate_me;
                    imageViewId = R.id.img_container_rate_me;
                    actionButtonId = R.id.action_button_rate_me;
                    dismissButtonId = R.id.dismiss_button_rate_me;
                    int neutralButtonId = R.id.neutral_button_rate_me;
                    String neutralButtonTextExpected;
                    if (template.getJSONObject("definition").has("remindLaterButtonText") && template.getJSONObject("definition").get("remindLaterButtonText") != JSONObject.NULL) {
                        neutralButtonTextExpected = template.getJSONObject("definition").getString("remindLaterButtonText");
                    } else {
                        neutralButtonTextExpected = "GO";
                    }
                    String neutralButtonTextActual = ((Button) currentDialogFragment.getView().findViewById(neutralButtonId)).getText().toString();
                    assertEquals(neutralButtonTextExpected, neutralButtonTextActual);
                    break;
                default:
                    messageTextId = R.id.message_regular;
                    messageTitleId = R.id.title_regular;
                    imageViewId = R.id.img_container_regular;
                    actionButtonId = -1;
                    dismissButtonId = R.id.dismiss_button_regular;
                    break;
            }
            String messageExpected = template.getString("message");
            assertNotNull(currentDialogFragment.getView());
            assertNotNull(currentDialogFragment.getView().findViewById(messageTextId));
            String messageActual = ((TextView)currentDialogFragment.getView().findViewById(messageTextId)).getText().toString();
            assertEquals(messageExpected,messageActual);

            if(template.has("title")&&(template.get("title")!=JSONObject.NULL)){
                String titleExpected = template.getString("title");
                assertNotNull(currentDialogFragment.getView().findViewById(messageTitleId));
                String titleActual = ((TextView) currentDialogFragment.getView().findViewById(messageTitleId)).getText().toString();
                assertEquals(titleExpected, titleActual);
            }

            if(template.has("imageUrl")&&(template.get("imageUrl")!=JSONObject.NULL)){
                assertNotNull(currentDialogFragment.getView().findViewById(imageViewId));
            }
            JSONObject definition = new JSONObject();
            if(template.has("definition")&&template.get("definition")!=JSONObject.NULL){
                definition = template.getJSONObject("definition");
            }

            if(definition.has("actionButton")&&definition.get("actionButton")!=JSONObject.NULL) {
                JSONObject jsonActionButton = definition.getJSONObject("actionButton");
                System.out.println(jsonActionButton.toString());

                String actionButtonTextExpected;
                if (jsonActionButton.has("buttonText") && jsonActionButton.get("buttonText") != JSONObject.NULL) {
                    actionButtonTextExpected = jsonActionButton.getString("buttonText");
                } else {
                    actionButtonTextExpected = "GO";
                }
                String actionButtonTextActual = ((Button) currentDialogFragment.getView().findViewById(actionButtonId)).getText().toString();
                assertEquals(actionButtonTextExpected, actionButtonTextActual);
            }

            String dismissButtonTextExpected;
            if(definition.has("dismissButtonText")&&definition.get("dismissButtonText")!=JSONObject.NULL){
                dismissButtonTextExpected = definition.getString("dismissButtonText");
            }else{
                dismissButtonTextExpected = "CANCEL";
            }
            String dismissButtonTextActual = ((Button)currentDialogFragment.getView().findViewById(dismissButtonId)).getText().toString();
            assertEquals(dismissButtonTextExpected,dismissButtonTextActual);

            currentDialogFragment.getView().findViewById(dismissButtonId).performClick();
            currentlyUsedArrayListOfPromotions.remove(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void validateActionClicks(JSONObject promotion){
        int actionButtonId,dismissButtonId;
        try {
            JSONObject template = promotion.getJSONObject("template");
            Uri actionUri;
            String shareText;
            String action;
            switch (template.getJSONObject("definition").getString("actionType").toLowerCase()) {
                case "link":
                    actionButtonId = R.id.action_button_action;
                    actionUri = Uri.parse(template.getJSONObject("definition").getJSONObject("actionButton").getString("url"));
                    action = Intent.ACTION_VIEW;
                    shareText =null;
                    break;
                case "share":
                    actionButtonId = R.id.action_button_action;
                    shareText = template.getJSONObject("definition").getJSONObject("actionButton").getString("shareText");
                    action = Intent.ACTION_SEND;
                    actionUri = null;
                    break;
                case "rate":
                    actionButtonId = R.id.action_button_rate_me;
                    actionUri = Uri.parse("market://details?id=?" + Robolectric.application.getPackageName());
                    action = Intent.ACTION_VIEW;
                    shareText = null;
                    break;
                default:
                    dismissButtonId = R.id.dismiss_button_regular;
                    currentDialogFragment.getView().findViewById(dismissButtonId).performClick();
                    currentlyUsedArrayListOfPromotions.remove(0);
                    return;
            }
            currentDialogFragment.getView().findViewById(actionButtonId).performClick();
            currentlyUsedArrayListOfPromotions.remove(0);
            intent = Robolectric.shadowOf(Robolectric.shadowOf(currentActivity).getNextStartedActivity());
            assertEquals(intent.getAction(), action);
            if(template.getJSONObject("definition").getString("actionType").toLowerCase()=="share"){
                assertEquals(intent.getExtras().getString(Intent.EXTRA_TEXT), shareText);
            }
            else {
                assertEquals(intent.getData(),actionUri);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void validateNeutralClicks(JSONObject promotion){
        int neutralButtonId,dismissButtonId;
        try {
            JSONObject template = promotion.getJSONObject("template");
            Uri actionUri;
            String action;
            switch (template.getJSONObject("definition").getString("actionType").toLowerCase()) {
                case "rate":
                    neutralButtonId = R.id.neutral_button_rate_me;
                    currentDialogFragment.getView().findViewById(neutralButtonId).performClick();
                    return;
                case "link":

                case "share":
                    dismissButtonId = R.id.dismiss_button_action;
                    break;
                case "none":
                    dismissButtonId = R.id.dismiss_button_regular;
                    break;
                default:
                    assertFalse(true);
                    return;
            }
            assertNotNull(currentDialogFragment.getView());
            currentDialogFragment.getView().findViewById(dismissButtonId).performClick();
            currentlyUsedArrayListOfPromotions.remove(0);
            return;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void validateImageClicks(JSONObject promotion){
        int imageViewId;
        Uri onClickUrl,imageUrl;
        try {
            JSONObject template = promotion.getJSONObject("template");
            onClickUrl = Uri.parse(template.getString("onClickUrl"));
            imageUrl = Uri.parse(template.getString("imageUrl"));
            switch (template.getJSONObject("definition").getString("actionType").toLowerCase()) {
                case "rate":
                    imageViewId = R.id.img_container_rate_me;
                    break;
                case "link":
                    imageViewId = R.id.img_container_action;
                    break;
                case "share":
                    imageViewId = R.id.img_container_action;
                    break;
                case "none":
                    imageViewId = R.id.img_container_regular;
                    break;
                default:
                    assertFalse(true);
                    return;
            }
            /*Bitmap bitmap = BitmapFactory.decodeFile(imageUrl.toString());
            Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Rect rect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
            Rect bottomRect = new Rect(0,bitmap.getHeight()/2,bitmap.getWidth(),bitmap.getHeight());
            RectF rectF = new RectF(rect);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Canvas canvas = new Canvas(resultBitmap);
            float cornerRadius = 10;
            canvas.drawARGB(0,0,0,0);
            canvas.drawRoundRect(rectF,cornerRadius,cornerRadius,paint);
            canvas.drawRect(bottomRect,paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap,rect,rect,paint);*/

            if(template.has("onClickUrl")&&(template.get("onClickUrl")!=JSONObject.NULL)){
                String onClickUrlExpected = template.getString("onClickUrl");
                assertNotNull(currentDialogFragment.getView().findViewById(imageViewId));
                ImageView imageView = (ImageView) currentDialogFragment.getView().findViewById(imageViewId);
                ShadowImageView shadowImageView = Robolectric.shadowOf(imageView);
                //assertTrue(shadowImageView.getDrawingCache().sameAs(resultBitmap));
                shadowImageView.checkedPerformClick();
                Intent intent = Robolectric.shadowOf(currentActivity).getNextStartedActivity();
                //System.out.println(intent.getData().toString());
                assertEquals(onClickUrlExpected,intent.getData().toString());

                //assertNotNull(currentActivity.getFragmentManager().findFragmentByTag("dialog"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//set up methods

    private void executeAfterResumeLogAndHttpTasks(){
        executeTasksOnLogWorker();
        executeTasksOnHttpWorker();
        executeTasksOnLogWorker();
    }
    private void setUpPromotionsScreenWise(){
        try{
            allPromotions = new JSONArray(promotionsForScreensJSON);
            promotionsForMainActivity.clear();
            promotionsForActivity2.clear();
            promotionsForActivity3.clear();
            promotionsForActivity4.clear();
            promotionsForActivity5.clear();
            for(int i=0;i<allPromotions.length();i++){
                JSONObject jsonObject = allPromotions.getJSONObject(i);
                switch(jsonObject.getString("screenId")){
                    case screenLabel1:
                        promotionsForMainActivity.add(0,jsonObject);
                        break;
                    case screenLabel2:
                        promotionsForActivity2.add(0,jsonObject);
                        break;
                    case screenLabel3:
                        promotionsForActivity3.add(0,jsonObject);
                        break;
                    case screenLabel4:
                        promotionsForActivity4.add(0,jsonObject);
                        break;
                    case screenLabel5:
                        promotionsForActivity5.add(0,jsonObject);
                        break;
                    default:
                        assertTrue(false);
                }
                System.out.println("Screenwise Promotions Array set up complete.");
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }


    //Helper Methods
    private Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return t;
        }
        return null;
    }

    private void instantiateLogAndHttpWorkers(){
        logWorker = (HandlerThread)getThreadByName("logWorker");
        httpWorker = (HandlerThread)getThreadByName("httpWorker");
        logWorkingLooper = Robolectric.shadowOf(logWorker.getLooper());
        httpWorkingLooper = Robolectric.shadowOf(httpWorker.getLooper());
    }

    private void executeTasksOnLogWorker(){
        logWorkingLooper.idle();
    }

    private void executeTasksOnHttpWorker(){
        httpWorkingLooper.idle();
    }

    private String getCurrentDateTime(long timestamp){
        return new SimpleDateFormat(Constants.Z_DATE_TIME_FORMAT).format(new Date(timestamp));
    }
    private String readFile( String file ){
        StringBuilder stringBuilder = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }catch(IOException e){
            e.printStackTrace();
            assertTrue(false);
        }
        return stringBuilder.toString();
    }
}
