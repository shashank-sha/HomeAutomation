import org.robolectric.Robolectric;

/**
 * Created by vedaprakash on 18/6/15.
 */
public class TestForInAppPromoImageClicks extends TestForInAppPromosStructureAndSuppressionLogicOnAck {
    @Override
    public void test() {
        // Initially opening the App for fetching promotions etc.
        initialStartOfTheApp();

        //Now that promotions are fetched we don't need the JSON of the promotions to be returned again.
        Robolectric.addHttpResponseRule(Constants.Z_PROMOTION_URL, "success");

        testPromotionsForActivity("MainActivity", UI_IMAGE);
        testPromotionsForActivity("Activity2", UI_IMAGE);
        testPromotionsForActivity("Activity3", UI_IMAGE);
        testPromotionsForActivity("Activity4", UI_IMAGE);
        testPromotionsForActivity("Activity5", UI_IMAGE);
    }
}
