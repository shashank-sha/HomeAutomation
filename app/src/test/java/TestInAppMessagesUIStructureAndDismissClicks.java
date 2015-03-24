import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

/**
 * Created by vedaprakash on 19/3/15.
 */
@Config(reportSdk = 18,emulateSdk = 18)
@RunWith(CustomRobolectricRunner.class)
public class TestInAppMessagesUIStructureAndDismissClicks extends TestInAppMessagesBaseClass {

    @Test
    public void test(){

        setupPromotionsInitially();
        while (inAppPromosPending()) {
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("UIdismiss", screenLabel1);
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("UIdismiss", screenLabel2);
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("UIdismiss", screenLabel3);
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("UIdismiss", screenLabel4);
            resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("UIdismiss", screenLabel5);
        }
    }
}
