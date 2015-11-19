import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vedaprakash on 16/6/15.
 */
public class Promotion {
    String campaignId,screenId,type,title,message,onClickUrl,
            actionType,actionButtonUrl,actionButtonText,dismissButtonText,
            shareText,neutralButtonText;

    int appVersionFrom,appVersionTo;
    long campaignStartTime,campaignEndTime;
    SuppressionLogic suppressionLogic;

    public Promotion(JSONObject promotion){
        campaignId = promotion.optString("campaignId");
        screenId = promotion.optString("screenId");
        type = promotion.optString("type");

        appVersionFrom = promotion.optInt("appVersionFrom");
        appVersionTo = promotion.optInt("appVersionTo");
        campaignStartTime = promotion.optLong("campaignStartTime");
        campaignEndTime = promotion.optLong("campaignEndTime");

        JSONObject suppressionLogicJSON =  promotion.optJSONObject("suppressionLogic");

        suppressionLogic = new SuppressionLogic(suppressionLogicJSON);

        JSONObject template = promotion.optJSONObject("template");

        title = template.optString("title");
        System.out.println(title);
        message = template.optString("message");
        onClickUrl = template.optString("onClickUrl");

        JSONObject definition = template.optJSONObject("definition");
        actionType = definition.optString("actionType");
        dismissButtonText = definition.optString("dismissButtonText");
        neutralButtonText = definition.optString("remindLaterButtonText");

        JSONObject actionButton = definition.optJSONObject("actionButton");
        if(actionButton!=null) {
            actionButtonText = actionButton.optString("buttonText");
            actionButtonUrl = actionButton.optString("url");
            shareText = actionButton.optString("shareText");
        }

    }

    public String getCampaignId() {
        return campaignId;
    }


    public String getActivity() {
        return screenId;
    }
}
