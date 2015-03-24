import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

/**
 * Created by vedaprakash on 19/3/15.
 */
@Config(reportSdk = 18,emulateSdk = 18)
@RunWith(CustomRobolectricRunner.class)
public class TestInAppMessagesNeutralClicks extends TestInAppMessagesBaseClass {

    @Test
    public void test(){
        setupPromotionsInitially();
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("neutral",screenLabel1);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("neutral",screenLabel2);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("neutral",screenLabel3);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("neutral",screenLabel4);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("neutral",screenLabel5);
    }
}
