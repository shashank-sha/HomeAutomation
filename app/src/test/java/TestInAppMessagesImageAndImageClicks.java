import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@Config(reportSdk = 18,emulateSdk = 18)
@RunWith(CustomRobolectricRunner.class)
public class TestInAppMessagesImageAndImageClicks extends TestInAppMessagesBaseClass{

    @Test
    public void test(){
        setupPromotionsInitially();
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("imageClick", screenLabel1);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("imageClick", screenLabel2);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("imageClick", screenLabel3);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("imageClick", screenLabel4);
        resumeActivityDoActionInAppMessageAndClickOnButtonForNextActivity("imageClick", screenLabel5);
    }

}
