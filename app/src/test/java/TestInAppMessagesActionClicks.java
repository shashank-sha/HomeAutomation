

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;



/**
 * Created by vedaprakash on 19/3/15.
 */
@Config(reportSdk = 18,emulateSdk = 18)
@RunWith(CustomRobolectricRunner.class)
public class TestInAppMessagesActionClicks extends TestInAppMessagesBaseClass {


    @Test
    public void test() {

        setupPromotionsInitially();
        while (inAppPromosPending()) {
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("action", screenLabel1);
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("action", screenLabel2);
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("action", screenLabel3);
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("action", screenLabel4);
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("action", screenLabel5);
        }
    }

}
