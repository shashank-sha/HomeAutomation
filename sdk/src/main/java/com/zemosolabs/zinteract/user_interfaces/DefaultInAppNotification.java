package com.zemosolabs.zinteract.user_interfaces;

import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemosolabs.zinteract.R;
import com.zemosolabs.zinteract.sdk.Zinteract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by praveen on 30/01/15.
 *
 * <p>JSONObject template structure:</p>
 * <p>"template": { </p>
 * <p>     "title": "Season sale! Heavy discounts!!!", </p>
 * <p>     "message": "Buy the stuff now and save a loot of money and use that money to buy some more stuff!!!", </p>
 * <p>     "imageUrl": "http://news.bbcimg.co.uk/media/images/81539000/jpg/_81539447_95ca831d-7a1d-4b02-b3ca-0c9968649937.jpg", </p>
 * <p>     "onClickUrl": "http://www.google.com", </p>
 * <p>     "definition": { </p>
 * <p>         "actionType": "SHARE|LINK|RATE|NONE", </p>
 * <p>         "actionButton": { </p>
 * <p>             "url": "http://www.facebook.com" </p>
 * <p>             "shareText": "This app is awesome!!! Check it out!" </p>
 * <p>             "buttonText": "LIKE US|SHARE|RATE US" </p>
 * <p>         } </p>
 * <p>         "dismissButtonText": "GOT IT|CANCEL|DON'T ASK ME AGAIN", </p>
 * <p>         "remindLaterButtonText: "REMIND ME LATER", </p>
 * <p>    } </p>
 * <p>   } </p>
 */
public class DefaultInAppNotification extends com.zemosolabs.zinteract.sdk.ZinteractInAppNotification {

    private Context context;
    private String campaignId;
    private String title;
    private String message;
    private String templateType;
    private String imageUrl;
    private String imageBase64;
    private String onClickUrl;
    private String actionType;
    private String dismissButtonText;
    private String actionButtonUrl;
    private String actionButtonText;
    private String remindButtonText;
    private String shareText;
    private Bitmap bitmap,resultBitmap;
    private boolean DEBUGGING_MODE = false;

    private static final String TAG = "InAppNotification";

    public void customize(Context context, String campaignId, JSONObject template) {
        this.campaignId = campaignId;
        this.context = context;
        if (DEBUGGING_MODE) {
            /*templateType = "REGULAR";
            title = "Test Title";
            message = "<h2>Try me!</h2><p><b>Hello</b></p><p>textAngular is a super cool WYSIWYG Text Editor directive for AngularJS</p><p><b>Features:</b></p><ol><li>Automatic Seamless Two-Way-Binding</li><li> <font color=\"#0000EE\">Super Easy <b>Theming</b> Options</font></li><li>Simple Editor Instance Creation</li><li>Safely Parses Html for Custom Toolbar Icons</li><li>Doesn't Use an iFrame</li><li>Works with Firefox, Chrome, and IE8+</li></ol><p><b>Code at GitHub:</b> <a href=\"https://github.com/fraywing/textAngular\">Here</a> </p>";
            imageUrl = "http://news.bbcimg.co.uk/media/images/81539000/jpg/_81539447_95ca831d-7a1d-4b02-b3ca-0c9968649937.jpg";
            onClickUrl = "http://www.google.com";
            actionType = "SHARE";
            dismissButtonText = "CANCEL";
            actionButtonUrl = "http://www.facebook.com";
            shareText = "This App is awesome! Check it out";
            actionButtonText = "GO";
            remindButtonText = "REMIND ME LATER";
            byte[] decodedString = Base64.decode(getImage(), Base64.DEFAULT);
            //Bitmap bitmap = BitmapFactory.decodeStream(input);
            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            resultBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Rect rect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
            Rect bottomRect = new Rect(0,bitmap.getHeight()/2,bitmap.getWidth(),bitmap.getHeight());
            RectF rectF = new RectF(rect);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Canvas canvas = new Canvas(resultBitmap);
            float cornerRadius = 30.0f;
            canvas.drawARGB(0,0,0,0);
            canvas.drawRoundRect(rectF,cornerRadius,cornerRadius,paint);
            canvas.drawRect(bottomRect,paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap,rect,rect,paint);
//            imageBase64 = context.getString(R.string.base64ImageStringTest);
            return;*/
        }
        try {

            JSONObject definition;
            if(template.has("templateType") && template.get("templateType") != JSONObject.NULL) {
                templateType = template.getString("templateType");
            }else templateType = "DEFAULT";
            if(template.has("message") && template.get("message")!=JSONObject.NULL) {
                message = template.getString("message");
            }else message ="dMESSAGE";
            if (template.has("imageBase64") && template.get("imageBase64") != JSONObject.NULL) {
                imageBase64 = template.getString("imageBase64");
            } else{
                imageBase64 = null;
                Log.i(TAG,"imageBase64 is null");
            }

            if (template.has("onClickUrl") && template.get("onClickUrl") != JSONObject.NULL) {
                onClickUrl = template.getString("onClickUrl");
            } else onClickUrl = null;
            if (template.has("title") && template.get("title") != JSONObject.NULL) {
                title = template.getString("title");
            } else title = "dTITLE";
            if(template.has("definition") && template.get("definition") != JSONObject.NULL) {
                definition = template.getJSONObject("definition");
                if(definition.has("actionType") && definition.get("actionType") != JSONObject.NULL) {
                    actionType = definition.getString("actionType");
                }else actionType = "NONE";
                if(definition.has("dismissButtonText") && definition.get("dismissButtonText") != JSONObject.NULL) {
                    dismissButtonText = definition.getString("dismissButtonText");
                }else dismissButtonText = "CANCEL";
                JSONObject actionButton;
                if (definition.has("actionButton") && definition.get("actionButton") != JSONObject.NULL) {
                    actionButton = definition.getJSONObject("actionButton");
                    switch (actionType.toUpperCase()) {
                        case "LINK":
                            if (actionButton.has("url") && actionButton.get("url") != JSONObject.NULL) {
                                actionButtonUrl = actionButton.getString("url");
                            } else actionButtonUrl = null;

                            if (actionButton.has("text") && actionButton.get("text") != JSONObject.NULL) {
                                actionButtonText = actionButton.getString("text");
                            } else actionButtonText = "GO";
                            break;
                        case "RATE":
                            if (actionButton.has("buttonText") && actionButton.get("buttonText") != JSONObject.NULL) {
                                actionButtonText = actionButton.getString("buttonText");
                            } else actionButtonText = "dRate Us";
                            if (definition.has("remindLaterButtonText") && definition.get("remindLaterButtonText") != JSONObject.NULL) {
                                remindButtonText = definition.getString("remindLaterButtonText");
                            } else remindButtonText = "dRemind Me Later";
                            break;
                        case "SHARE":
                            if (actionButton.has("shareText") && actionButton.get("shareText") != JSONObject.NULL) {
                                shareText = actionButton.getString("shareText");
                            } else shareText = "dSHARE OUR APP WITH YOUR FRIENDS";
                            if (actionButton.has("buttonText") && actionButton.get("buttonText") != JSONObject.NULL) {
                                actionButtonText = actionButton.getString("buttonText");
                            } else actionButtonText = "dSHARE";
                            break;
                        default:
                            break;
                    }
                }
            }else{
                actionType = "NONE";
                dismissButtonText = "dCANCEL";
            }
        } catch (JSONException e) {
            Log.i("Exception: ", e.toString());
        }
        if(imageBase64!=null) {
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            //Bitmap bitmap = BitmapFactory.decodeStream(input);
            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            resultBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect bottomRect = new Rect(0, bitmap.getHeight() / 2, bitmap.getWidth(), bitmap.getHeight());
            RectF rectF = new RectF(rect);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Canvas canvas = new Canvas(resultBitmap);
            float cornerRadius = 30.0f;
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
            canvas.drawRect(bottomRect, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        }
    }

   /* private String getImage() {
        String string="";
        try{
            InputStream is = context.getResources().openRawResource(R.raw.base64image);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1);
            string = new String(buffer);

            Log.e(TAG, "The length of the string is: " + string.length());
        } catch (IOException e) {
            Log.e(TAG,"failed in getting String Base64 image",e);
        }
        Log.i(TAG, "base64 string is: " + string.subSequence(0, 50));
        return string;
    }*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setCancelable(false);
        Log.i("inside Notification","control came here");
        View v = null;
        View mv = null;
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if(actionType.toUpperCase(Locale.US).equals("NONE")){
            v = inflater.inflate(R.layout.regular_in_app_message, container, false);
            Button button = (Button)v.findViewById(R.id.dismiss_button_regular);
            Log.i("DismissButton","Updating");
            button.setText(dismissButtonText);
            button.setOnClickListener(closeHandler);
            View tv = v.findViewById(R.id.title_regular);
            if(title!=null){
                Log.i("Title","Updating");
                ((TextView)tv).setText(Html.fromHtml(title),TextView.BufferType.SPANNABLE);
            }
            else{
                Log.i("Title","Being Removed");
                tv.setVisibility(View.GONE);
            }
            ImageView imgView = (ImageView) v.findViewById(R.id.img_container_regular);
            if(imageBase64!=null){
                Log.i("Image","ImgView updating");
               // new UpdateImageViewAsyncTask(imageUrl,imgView).execute();
                imgView.setImageBitmap(resultBitmap);
                if(onClickUrl!=null){
                    imgView.setOnClickListener(imageClickHandler);
                }
            }
            else{
                Log.i("Image","ImgView made invisible");
                imgView.setVisibility(View.GONE);
            }

            mv = v.findViewById(R.id.message_regular);

        }

        else if(actionType.toUpperCase(Locale.US).equals("RATE")) {

            v = inflater.inflate(R.layout.rate_me_in_app_message, container, false);
            Button askLaterButton = (Button) v.findViewById(R.id.neutral_button_rate_me);
            askLaterButton.setOnClickListener(askMeLater);
            Button dontAskButton = (Button) v.findViewById(R.id.dismiss_button_rate_me);
            dontAskButton.setOnClickListener(dontAskMeAgain);
            Button rateItButton = (Button) v.findViewById(R.id.action_button_rate_me);
            rateItButton.setOnClickListener(rateMeEventHandler);
            Log.i("RateMeButton", "Updating");
            rateItButton.setText(actionButtonText);
            askLaterButton.setText(remindButtonText);
            dontAskButton.setText(dismissButtonText);
            View tv = v.findViewById(R.id.title_rate_me);
            if (title != null) {
                Log.i("Title", "Updating");
                ((TextView) tv).setText(Html.fromHtml(title),TextView.BufferType.SPANNABLE);
            } else {
                Log.i("Title", "Being Removed");
                tv.setVisibility(View.GONE);
            }
            ImageView imgView = (ImageView) v.findViewById(R.id.img_container_rate_me);
            if (imageBase64 != null) {
                Log.i("Image", "ImgView updating");
                //new UpdateImageViewAsyncTask(imageUrl, imgView).execute();
                imgView.setImageBitmap(resultBitmap);
                if(onClickUrl!=null){
                    imgView.setOnClickListener(imageClickHandler);
                }
            } else {
                Log.i("Image", "ImgView made invisible");
                imgView.setVisibility(View.GONE);
            }

            mv = v.findViewById(R.id.message_rate_me);

        }
        else if(actionType.toUpperCase(Locale.US).equals("SHARE")||actionType.toUpperCase(Locale.US).equals("LINK")) {
            v = inflater.inflate(R.layout.action_in_app_message, container, false);
            Button dismissButton = (Button) v.findViewById(R.id.dismiss_button_action);
            Log.i("DismissButton", "Updating");
            dismissButton.setText(dismissButtonText);
            dismissButton.setOnClickListener(closeHandler);
            Button actionButton = (Button) v.findViewById(R.id.action_button_action);

            Log.i("ActionButton", "Updating");
            actionButton.setText(actionButtonText);
            actionButton.setOnClickListener(actionEventHandler);
            View tv = v.findViewById(R.id.title_action);
            if (title != null) {
                Log.i("Title", "Updating");
                ((TextView) tv).setText(Html.fromHtml(title),TextView.BufferType.SPANNABLE);
            } else {
                Log.i("Title", "Being Removed");
                tv.setVisibility(View.GONE);
            }
            ImageView imgView = (ImageView) v.findViewById(R.id.img_container_action);
            if (imageBase64 != null) {
                Log.i("Image", "ImgView updating");
                imgView.setImageBitmap(resultBitmap);
                //new UpdateImageViewAsyncTask(imageUrl, imgView).execute();
                if(onClickUrl!=null){
                    imgView.setOnClickListener(imageClickHandler);
                }
            } else {
                Log.i("Image", "ImgView made invisible");
                imgView.setVisibility(View.GONE);
            }

            mv = v.findViewById(R.id.message_action);


        }
        ((TextView)mv).setText(Html.fromHtml(message),TextView.BufferType.SPANNABLE);
        Log.i("Message","Message Updated");
        return v;
    }

    View.OnClickListener imageClickHandler = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openImageLinkInBrowser = new Intent(Intent.ACTION_VIEW,Uri.parse(onClickUrl));
                try{
                    Log.i("InAppIMAGE:","CLICKED");
                    startActivity(openImageLinkInBrowser);
                }catch (ActivityNotFoundException e){
                    Log.i("Exception: ",e.toString());
                }
                dismiss();
            }
    };

    View.OnClickListener askMeLater = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                JSONObject j = new JSONObject();
                j.put("campaignId", campaignId);
                Zinteract.updatePromotionAsSeen(campaignId);
            }
            catch (Exception e){
                Log.e(TAG, "Exception: " + e);
            }

            dismiss();
        }
    };

    View.OnClickListener dontAskMeAgain = new View.OnClickListener() {
        public void onClick(View v) {
           /* try {
                JSONObject k = new JSONObject();
                k.put("campaignId", campaignId);
            }
            catch (Exception e){
                Log.e(TAG, "Exception: " + e);
            }*/
            Zinteract.removePromotion(campaignId);
            dismiss();
        }
    };

    View.OnClickListener rateMeEventHandler = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Uri uri = Uri.parse("market://details?id=?" + context.getPackageName());
            Intent rateMyApp = new Intent(Intent.ACTION_VIEW,uri);
            try{
                Log.i("RATEME:","CLICKED");
                startActivity(rateMyApp);
                Zinteract.removePromotion(campaignId);
            }catch (ActivityNotFoundException e){
                Log.i("Exception: ",e.toString());
            }
            dismiss();
        }
    };

    View.OnClickListener actionEventHandler = new View.OnClickListener(){
        public void onClick(View v){
            if(actionType.toUpperCase(Locale.US).equals("LINK")){
                Intent openLinkInBrowser = new Intent(Intent.ACTION_VIEW,Uri.parse(actionButtonUrl));
                try{
                    Log.i("ACTIONEVENT:","CLICKED");
                    startActivity(openLinkInBrowser);
                    Zinteract.updatePromotionAsSeen(campaignId);
                }catch (ActivityNotFoundException e){
                    Log.i("Exception: ",e.toString());
                }
            }
            else if(actionType.toUpperCase(Locale.US).equals("SHARE")){
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT,shareText);
                share.setType("text/plain");
                try{
                    Log.i("SHAREEVENT:","CLICKED");
                    startActivity(share);
                    Zinteract.updatePromotionAsSeen(campaignId);
                }catch(ActivityNotFoundException e){
                    Log.i("Exception: ",e.toString());
                }
            }
            dismiss();

        }
    };

    View.OnClickListener closeHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Zinteract.updatePromotionAsSeen(campaignId);
            dismiss();
        }
    };

}

