import org.json.JSONObject;

/**
 * Created by vedaprakash on 16/6/15.
 */
public class SuppressionLogic {
    int maxTimesToShow,minDurationInMinutesBeforeReshow;
    private long lastShownTime;
    private int numberOfTimesShown;

    public SuppressionLogic(JSONObject suppressionLogic){
        minDurationInMinutesBeforeReshow = suppressionLogic.optInt("minimumDurationInMinutesBeforeReshow");
        maxTimesToShow = suppressionLogic.optInt("maximumNumberOfTimesToShow");
    }

    void updatePromotionSeen(long timeOfShow){
        lastShownTime = timeOfShow;
        numberOfTimesShown++;
        System.out.println("Updating the promotion as seen; "+"maxTimesToShow: "+maxTimesToShow+"; numberOfTimesShown: "+numberOfTimesShown);
    }

    boolean isValidNow(){
        if(System.currentTimeMillis() > lastShownTime+minDurationInMinutesBeforeReshow*60*1000){
            if(numberOfTimesShown < maxTimesToShow){
                return true;
            }else{
                System.out.println("Promotion not valid: Has been shown max no. of times");
                return false;
            }
        }else{
            System.out.println("Promotion not valid: Not enough time passed since last show");
            return false;
        }
    }

    long getWaitTime(){
        if(maxTimesToShow<=numberOfTimesShown){
            System.out.println("maxtimes exceeded inside suppression logic");
            return -1;
        }
        return minDurationInMinutesBeforeReshow*60*1000 - (System.currentTimeMillis()-lastShownTime);
    }


}
