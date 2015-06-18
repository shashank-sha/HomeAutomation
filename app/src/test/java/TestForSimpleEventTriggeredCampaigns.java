import android.app.Notification;
import android.widget.Button;

import com.zemoso.zinteract.ZinteractSampleApp.Activity4;
import com.zemoso.zinteract.ZinteractSampleApp.R;

import org.json.JSONException;
import org.robolectric.Robolectric;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by vedaprakash on 20/5/15.
 */
public class TestForSimpleEventTriggeredCampaigns extends TestForCampaigns {

    @Override
    public void test() {
        List<Notification> listOfNotifications;
        int numberOfNotifications = 0;
        resumeActivityDoActionInAppAndClickOnButtonForNextActivity("next", "MainActivity");
        Robolectric.clearHttpResponseRules();
        int maxNumberOfTimesToShow = 0;
        int minutesBeforeReshow = 0;
        try {
            maxNumberOfTimesToShow  = response.getJSONArray("promotions").getJSONObject(0).getJSONObject("suppressionLogic").getInt("maximumNumberOfTimesToShow");
            minutesBeforeReshow = response.getJSONArray("promotions").getJSONObject(0).getJSONObject("suppressionLogic").getInt("minimumDurationInMinutesBeforeReshow");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //First time show notification test if its present
        resumeActivityDoActionInAppAndClickOnButtonForNextActivity("next", "Activity2");
        listOfNotifications = goFromActivity2To4AndMakeAPurchase();
        assertTrue(notificationForCampaignExists(listOfNotifications, numberOfNotifications,0));
        numberOfNotifications = listOfNotifications.size();
        activity4ActivityController.pause();

        //Attempt to show second time before the time lapse test if no notification
        resumeActivityDoActionInAppAndClickOnButtonForNextActivity("next", "Activity2");
        listOfNotifications = goFromActivity2To4AndMakeAPurchase();
        assertTrue(listOfNotifications.size()==numberOfNotifications);
        activity4ActivityController.pause();

        //Test if the notification is showed the number of times specified in the maxNumberOfTimesToShow
        for(int i=1;i<=maxNumberOfTimesToShow-1;i++){
            try {
                Thread.sleep(minutesBeforeReshow*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resumeActivityDoActionInAppAndClickOnButtonForNextActivity("next", "Activity2");
            listOfNotifications = goFromActivity2To4AndMakeAPurchase();
            assertTrue(notificationForCampaignExists(listOfNotifications, numberOfNotifications,0));
            numberOfNotifications = listOfNotifications.size();
            activity4ActivityController.pause();
        }
        try {
            Thread.sleep(minutesBeforeReshow*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Test that the notification should not be shown anymore than the maxNumberOfTimesToShow
        resumeActivityDoActionInAppAndClickOnButtonForNextActivity("next", "Activity2");
        listOfNotifications = goFromActivity2To4AndMakeAPurchase();
        assertTrue(listOfNotifications.size()==numberOfNotifications);
        activity4ActivityController.pause();

    }

    private List<Notification> goFromActivity2To4AndMakeAPurchase(){
        resumeActivityDoActionInAppAndClickOnButtonForNextActivity("next", "Activity3");
        if(activity4ActivityController==null){
            activity4ActivityController = Robolectric.buildActivity(Activity4.class).attach().create().start().resume().visible();
        }
        else{
            activity4ActivityController.resume().visible();
        }
        executeWorkerTasksForLogEvent();
        currentActivity = (Activity4) activity4ActivityController.get();
        System.out.println(currentActivity.getLocalClassName());

        // Click on Add to cart button
        Button addToCart = (Button)currentActivity.findViewById(R.id.buttonAddToCart);
        addToCart.performClick();

        executeWorkerTasksForLogEvent();

        // Click on Buy button to make the purchase
        Button buy = (Button)currentActivity.findViewById(R.id.buttonBuy);
        buy.performClick();

        executeWorkerTasksForLogEvent();

        return getNotifications();
    }


}
