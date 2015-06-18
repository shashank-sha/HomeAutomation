import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemoso.zinteract.ZinteractSampleApp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowImageView;
import org.robolectric.shadows.ShadowIntent;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by vedaprakash on 16/6/15.
 */
public class TestForInAppPromosStructureAndSuppressionLogicOnAck extends TestForCampaigns {
    HashMap<String,ArrayList<String>> activityCampaignIds = new HashMap<String,ArrayList<String>>();
    HashMap<String,Promotion> campaignIdPromotion = new HashMap<String,Promotion>();
    HashMap<String,SuppressionLogic> campaignIdSuppressionLogic = new HashMap<String,SuppressionLogic>();
    DialogFragment currentDialogFragment;

    final String ACTION_TYPE_LINK = "LINK";
    final String ACTION_TYPE_RATE = "RATE";
    final String ACTION_TYPE_SHARE = "SHARE";
    final String ACTION_TYPE_NONE = "NONE";

    final String SCREENLABEL1 = "com.zemoso.zinteract.ZinteractSampleApp.MainActivity";
    final String SCREENLABEL2 = "com.zemoso.zinteract.ZinteractSampleApp.Activity2";
    final String SCREENLABEL3 = "com.zemoso.zinteract.ZinteractSampleApp.Activity3";
    final String SCREENLABEL4 = "com.zemoso.zinteract.ZinteractSampleApp.Activity4";
    final String SCREENLABEL5 = "com.zemoso.zinteract.ZinteractSampleApp.Activity5";

    final String UI_STRUCTURE = "UI_STRUCTURAL_VALIDATION";
    final String UI_ACTION = "UI_ACTION_BUTTON_VALIDATION";
    final String UI_DONTASKAGAIN = "UI_NEUTRAL_BUTTON_VALIDATION";
    final String UI_IMAGE = "UI_IMAGE_CLICK_VALIDATION";

    @Override
    public void setup() {
        super.setup();
        String promotionsForScreensJSON = readFile("app/src/test/java/campaigns.JSON");
        try {
            JSONObject fetchPromoResponse = new JSONObject(promotionsForScreensJSON);
            JSONArray promotionsJSONArray = fetchPromoResponse.getJSONArray("promotions");
            for(int i=0;i<promotionsJSONArray.length();i++){
                JSONObject promotionJSONObject = promotionsJSONArray.getJSONObject(i);
                if(promotionJSONObject.optString("type").equals("promotion")) {
                    Promotion promotionNew = new Promotion(promotionJSONObject);
                    String campaignId = promotionNew.getCampaignId();
                    campaignIdPromotion.put(campaignId, promotionNew);
                    ArrayList<String> listOfCampaignIds = activityCampaignIds.get(promotionNew.getActivity());
                    if (listOfCampaignIds == null) {
                        listOfCampaignIds = new ArrayList<String>();
                    }
                    listOfCampaignIds.add(0, campaignId);
                    activityCampaignIds.put(promotionNew.getActivity(), listOfCampaignIds);
                    campaignIdSuppressionLogic.put(campaignId, promotionNew.suppressionLogic);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void test() {

        // Initially opening the App for fetching promotions etc.
        initialStartOfTheApp();

        //Now that promotions are fetched we don't need the JSON of the promotions to be returned again.
        Robolectric.addHttpResponseRule(Constants.Z_PROMOTION_URL, "success");

        testPromotionsForActivity("MainActivity", UI_STRUCTURE);
        testPromotionsForActivity("Activity2", UI_STRUCTURE);
        testPromotionsForActivity("Activity3", UI_STRUCTURE);
        testPromotionsForActivity("Activity4",UI_STRUCTURE);
        testPromotionsForActivity("Activity5",UI_STRUCTURE);
    }

    protected void testPromotionsForActivity(String activity, String typeOfValidation) {
        while(true) {
            //We can test if InAppPromos are shown for MainActivity.
            resumeActivityAssertTheExistenceOfInAppPromoAndValidateAndPause(activity,UI_STRUCTURE);

            //Retrying to open MainActivity shouldn't show the Promotions because of suppressionLogic
            resumeActivityAssertTheExistenceOfInAppPromoAndValidateAndPause(activity,UI_STRUCTURE);
            long minWaitTime = getMinimumWaitTime();
            if (minWaitTime !=-1) {
                System.out.println("Wait Time is " + minWaitTime);
                if(minWaitTime>0) {
                    try {
                        Thread.sleep(minWaitTime+300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("Wait Time is " + minWaitTime);
                break;
            }
            resumeActivityAssertTheExistenceOfInAppPromoAndValidateAndPause(activity,UI_STRUCTURE);
        }

    }

    protected void initialStartOfTheApp() {
        resumeActivity("MainActivity");
        finishBackGroundTasks();
        pauseActivity();
        finishBackGroundTasks();
    }

    protected long getMinimumWaitTime() {
        List<String> campaignIds = activityCampaignIds.get(getActivityLabel());
        long finalWaitTime = -1;
        long tempWaitTime;
        for(int i = 0; i < campaignIds.size(); i++){
            Promotion promotionCurrent = campaignIdPromotion.get(campaignIds.get(i));
            if(promotionCurrent.campaignEndTime>System.currentTimeMillis()){
                if((tempWaitTime = promotionCurrent.suppressionLogic.getWaitTime())!=-1){
                    if(tempWaitTime<0){
                        finalWaitTime = 0;
                        return finalWaitTime;
                    }
                    if(finalWaitTime==-1||tempWaitTime<finalWaitTime){
                        finalWaitTime = tempWaitTime;
                    }
                }
            }
        }
        return finalWaitTime;
    }

    protected void resumeActivityAssertTheExistenceOfInAppPromoAndValidateAndPause(String activity,String typeOfValidation) {
        resumeActivity(activity);
        finishBackGroundTasks();
        boolean inAppPromotionDoesExist = inAppPromoExists();
        assertEquals(shouldThereBeAnyActivePromos(getActivityLabel()), inAppPromotionDoesExist);
        if(inAppPromotionDoesExist){
            Promotion promotionCurrent = fetchActivePromotionFor(getActivityLabel());
            switch(typeOfValidation){
                case UI_STRUCTURE:
                    validateUIStructure(promotionCurrent);
                    break;
                case UI_ACTION:
                    if(promotionCurrent.actionType.equals(ACTION_TYPE_NONE)){
                        validateUIStructure(promotionCurrent);
                    }else {
                        validateActionClicks(promotionCurrent);
                    }
                    break;
                case UI_DONTASKAGAIN:
                    if(promotionCurrent.actionType.equals(ACTION_TYPE_RATE)) {
                        validateDontAskMeClicks(promotionCurrent);
                    }else{
                        validateUIStructure(promotionCurrent);
                    }
                    break;
                case UI_IMAGE:
                    validateImage(promotionCurrent);
            }
        }
        pauseActivity();
        finishBackGroundTasks();
    }

    protected String getActivityLabel() {
        String screenId;
        switch(currentActivity.getLocalClassName()){
            case "MainActivity":
                screenId = SCREENLABEL1;
                break;
            case "Activity2":
                screenId = SCREENLABEL2;
                break;
            case "Activity3":
                screenId = SCREENLABEL3;
                break;
            case "Activity4":
                screenId = SCREENLABEL4;
                break;
            case "Activity5":
                screenId = SCREENLABEL5;
                break;
            default:
                System.out.println("Activity in foreground seems to be something UNKNOWN");
                return null;
        }
        return screenId;
    }

    protected boolean shouldThereBeAnyActivePromos(String screenId){
        if(fetchActivePromotionFor(screenId)==null){
            return false;
        }else{
            return true;
        }
    }


    protected void finishBackGroundTasks() {
        instantiateZeTargetWorkers();
        executeWorkerTasksForInAppPromos();
    }


    protected void validateImage(Promotion activePromotion) {
        int imageViewId;
        Uri onClickUrl,imageBase64;

        switch (activePromotion.actionType) {
            case ACTION_TYPE_RATE:
                imageViewId = R.id.img_container_rate_me;
                break;
            case ACTION_TYPE_LINK:

            case ACTION_TYPE_SHARE:
                imageViewId = R.id.img_container_action;
                break;
            case ACTION_TYPE_NONE:
                imageViewId = R.id.img_container_regular;
                break;
            default:
                Assert.assertFalse(true);
                return;
        }
        if(activePromotion.onClickUrl!=null&&!activePromotion.onClickUrl.isEmpty()){
            onClickUrl = Uri.parse(activePromotion.onClickUrl);
            assertNotNull(currentDialogFragment.getView().findViewById(imageViewId));
            ImageView imageView = (ImageView) currentDialogFragment.getView().findViewById(imageViewId);
            ShadowImageView shadowImageView = Robolectric.shadowOf(imageView);
            //assertTrue(shadowImageView.getDrawingCache().sameAs(resultBitmap));
            shadowImageView.checkedPerformClick();
            activePromotion.suppressionLogic.updatePromotionSeen(System.currentTimeMillis());
            Intent intent = Robolectric.shadowOf(currentActivity).getNextStartedActivity();
            //System.out.println(intent.getData().toString());
            assertEquals(onClickUrl,intent.getData());
        }
    }

    protected void validateDontAskMeClicks(Promotion activePromotion) {
        int dismissButtonId;
        switch (activePromotion.actionType) {
            case ACTION_TYPE_RATE:
                dismissButtonId = R.id.dismiss_button_rate_me;
                activityCampaignIds.get(getActivityLabel()).remove(activePromotion);
                break;
            case ACTION_TYPE_LINK:

            case ACTION_TYPE_SHARE:
                dismissButtonId = R.id.dismiss_button_action;
                break;
            case ACTION_TYPE_NONE:
                dismissButtonId = R.id.dismiss_button_regular;
                break;
            default:
                Assert.assertFalse(true);
                return;
        }
        assertNotNull(currentDialogFragment.getView());
        currentDialogFragment.getView().findViewById(dismissButtonId).performClick();
        return;
    }

    protected void validateActionClicks(Promotion activePromotion){
        int actionButtonId,dismissButtonId;
            Uri actionUri;
            String shareText;
            String action;
            switch (activePromotion.actionType) {
                case "link":
                    actionButtonId = R.id.action_button_action;
                    actionUri = Uri.parse(activePromotion.actionButtonUrl);
                    action = Intent.ACTION_VIEW;
                    shareText =null;
                    break;
                case "share":
                    actionButtonId = R.id.action_button_action;
                    shareText = activePromotion.shareText;
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
                    System.out.println("NO Action validation for simple messages");
                    return;
            }
            currentDialogFragment.getView().findViewById(actionButtonId).performClick();
            if(activePromotion.actionType.equals("RATE")){
                activityCampaignIds.get(getActivityLabel()).remove(activePromotion.campaignId);
            }else{
                activePromotion.suppressionLogic.updatePromotionSeen(System.currentTimeMillis());
            }
            ShadowIntent intent = Robolectric.shadowOf(Robolectric.shadowOf(currentActivity).getNextStartedActivity());
            assertEquals(intent.getAction(), action);
            if(activePromotion.shareText!=null&&!activePromotion.shareText.isEmpty()){
                assertEquals(intent.getExtras().getString(Intent.EXTRA_TEXT), shareText);
            }
            else {
                assertEquals(intent.getData(),actionUri);
            }
    }

    protected void validateUIStructure(Promotion activePromotion) {
        String actionType = activePromotion.actionType;
        int messageTextId = -1,messageTitleId = -1,imageViewId = -1,actionButtonId = -1,dismissButtonId = -1,neutralButtonId = -1;
        switch(actionType) {
            case ACTION_TYPE_LINK:
                messageTextId = R.id.message_action;
                messageTitleId = R.id.title_action;
                //imageViewId = R.id.img_container_action;
                actionButtonId = R.id.action_button_action;
                dismissButtonId = R.id.dismiss_button_action;
                break;
            case ACTION_TYPE_RATE:
                messageTextId = R.id.message_rate_me;
                messageTitleId = R.id.title_rate_me;
                //imageViewId = R.id.img_container_rate_me;
                actionButtonId = R.id.action_button_rate_me;
                dismissButtonId = R.id.dismiss_button_rate_me;
                neutralButtonId = R.id.neutral_button_rate_me;
                break;
            case ACTION_TYPE_SHARE:
                messageTextId = R.id.message_action;
                messageTitleId = R.id.title_action;
                imageViewId = R.id.img_container_action;
                actionButtonId = R.id.action_button_action;
                dismissButtonId = R.id.dismiss_button_action;
                break;
            case ACTION_TYPE_NONE:
                messageTextId = R.id.message_regular;
                messageTitleId = R.id.title_regular;
                //imageViewId = R.id.img_container_regular;
                actionButtonId = -1;
                dismissButtonId = R.id.dismiss_button_regular;
                break;
            default:
                System.out.println("ActionType of Promotion seems to be out of available options");
                break;
        }
        String messageExpected = activePromotion.message;
        assertNotNull(currentDialogFragment.getView());
        assertNotNull(currentDialogFragment.getView().findViewById(messageTextId));
        String messageActual = ((TextView)currentDialogFragment.getView().findViewById(messageTextId)).getText().toString();
        assertEquals(messageExpected, messageActual);

        String titleExpected = activePromotion.title;
        assertNotNull(currentDialogFragment.getView().findViewById(messageTitleId));
        String titleActual = ((TextView) currentDialogFragment.getView().findViewById(messageTitleId)).getText().toString();
        assertEquals(titleExpected, titleActual);

        String actionButtonTextExpected = activePromotion.actionButtonText;
        if(actionButtonTextExpected!=null&&!actionButtonTextExpected.isEmpty()) {
            String actionButtonTextActual = ((Button) currentDialogFragment.getView().findViewById(actionButtonId)).getText().toString();
            assertEquals(actionButtonTextExpected, actionButtonTextActual);
        }

        String dismissButtonTextExpected = activePromotion.dismissButtonText;
        if(dismissButtonTextExpected!=null) {
            String dismissButtonTextActual = ((Button) currentDialogFragment.getView().findViewById(dismissButtonId)).getText().toString();
            assertEquals(dismissButtonTextExpected, dismissButtonTextActual);
        }

        String neutralButtonTextExpected = activePromotion.neutralButtonText;
        if(neutralButtonTextExpected!=null&&!neutralButtonTextExpected.isEmpty()) {
            String neutralButtonTextActual = ((Button)currentDialogFragment.getView().findViewById(neutralButtonId)).getText().toString();
            assertEquals(neutralButtonTextExpected, neutralButtonTextActual);
        }

        if(activePromotion.actionType.equals(ACTION_TYPE_RATE)) {
            System.out.println("Remind me later clicked");
            ((Button) currentDialogFragment.getView().findViewById(neutralButtonId)).performClick();
        }else {
            ((Button) currentDialogFragment.getView().findViewById(dismissButtonId)).performClick();
        }
        activePromotion.suppressionLogic.updatePromotionSeen(System.currentTimeMillis());
        assertNull(currentDialogFragment = (DialogFragment) currentActivity.getFragmentManager().findFragmentByTag("dialog"));
        System.out.println("Validated "+activePromotion.campaignId);
    }




    protected Promotion fetchActivePromotionFor(String screenId) {
        List<String> campaignIds = activityCampaignIds.get(screenId);
        for(int i = 0;i<campaignIds.size();i++){
            Promotion promotionCurrent = campaignIdPromotion.get(campaignIds.get(i));
            if(promotionCurrent.campaignEndTime>System.currentTimeMillis()) {
                if (promotionCurrent.suppressionLogic.isValidNow()) {
                    return promotionCurrent;
                }
            }
        }
        return null;
    }

    protected boolean inAppPromoExists() {
        currentDialogFragment = (DialogFragment)currentActivity.getFragmentManager().findFragmentByTag("dialog");
        if(currentDialogFragment==null) {
            return false;
        }else{
            System.out.println("Promotion is being shown");
            return true;
        }
    }
}
