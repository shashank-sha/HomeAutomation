package com.zemosolabs.zetarget.user_interfaces;

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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannedString;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemosolabs.zetarget.R;
import com.zemosolabs.zetarget.sdk.ZeTarget;
import com.zemosolabs.zetarget.sdk.ZeTargetInAppNotification;

import org.json.JSONException;
import org.json.JSONObject;

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
public class DefaultInAppNotification extends ZeTargetInAppNotification {

    private Context context;
    private String campaignId;
    private String title;
    private String message;
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
//          imageBase64 = context.getString(R.string.base64ImageStringTest);
            return;*/
        }
        try {

            JSONObject definition;
            if(template.has("message") && template.get("message")!=JSONObject.NULL) {
                message = template.getString("message");
                Log.i("Message InAPP",message);
               // message = "lksjdflkdsjklfjsdlfkaj;ldkfjsdlfkdjflksdjfslkdfslkdfjsdlfsfkjklsjflkdjfksldjfsdlkfjdskfdsflksdjf";
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
                Log.i("Title InAPP",title);
                //title = "LKDSjflksjflkdsjfklsdjfldksfjsf";
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

                            if (actionButton.has("buttonText") && actionButton.get("buttonText") != JSONObject.NULL) {
                                actionButtonText = actionButton.getString("buttonText");
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
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            Log.i(TAG,"height of bitmap is "+bitmap.getHeight()+"width of bitmap is "+bitmap.getWidth());
            float aspectRatio = (float)bitmap.getHeight()/(float)bitmap.getWidth();
            int finalWidth = 95*width/100;
            Log.i(TAG,"final width is "+finalWidth);
            int finalHeight = (int)((float)finalWidth*aspectRatio);
            Log.i(TAG,"final height is "+finalHeight);
            resultBitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);

            Rect rect = new Rect(0, 0, finalWidth, finalHeight);
            Rect bottomRect = new Rect(0, finalHeight / 2, finalWidth, finalHeight);
            RectF rectF = new RectF(rect);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Canvas canvas = new Canvas(resultBitmap);
            float cornerRadius = 10.0f;
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
            canvas.drawRect(bottomRect, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, null, rect, paint);
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
        //String titleText = Html.fromHtml(title).toString().trim();
        View v = null;
        TextView mv = null;
        Typeface robotoMedium = Typeface.createFromAsset(context.getAssets(), "roboto_medium.ttf");
        Typeface robotoLight = Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf");
        Typeface robotoRegular = Typeface.createFromAsset(context.getAssets(), "roboto_regular.ttf");
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if(actionType.toUpperCase(Locale.US).equals("NONE")){
            v = inflater.inflate(R.layout.regular_in_app_message, container, false);
            Button button = (Button)v.findViewById(R.id.dismiss_button_regular);
            button.setTypeface(robotoMedium);
            Log.i("DismissButton", "Updating");
            button.setText(dismissButtonText);
            button.setOnClickListener(closeHandler);
            TextView tv = (TextView)v.findViewById(R.id.title_regular);
            tv.setTypeface(robotoRegular);
            if(title!=null){
                Log.i("Title", "Updating");
                tv.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
                tv.setText(tv.getText().toString().trim());
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
                    imgView.setOnTouchListener(imageClickHandler);
                }
            }
            else{
                Log.i("Image","ImgView made invisible");
                imgView.setVisibility(View.GONE);
            }

            mv = (TextView)v.findViewById(R.id.message_regular);
            mv.setText(mv.getText().toString().trim());
            mv.setTypeface(robotoLight);

        }

        else if(actionType.toUpperCase(Locale.US).equals("RATE")) {

            v = inflater.inflate(R.layout.rate_me_in_app_message, container, false);
            Button askLaterButton = (Button) v.findViewById(R.id.neutral_button_rate_me);
            askLaterButton.setOnClickListener(askMeLater);
            askLaterButton.setTypeface(robotoMedium);
            Button dontAskButton = (Button) v.findViewById(R.id.dismiss_button_rate_me);
            dontAskButton.setOnClickListener(dontAskMeAgain);
            dontAskButton.setTypeface(robotoMedium);
            Button rateItButton = (Button) v.findViewById(R.id.action_button_rate_me);
            rateItButton.setOnClickListener(rateMeEventHandler);
            rateItButton.setTypeface(robotoMedium);
            Log.i("RateMeButton", "Updating");
            rateItButton.setText(actionButtonText);
            askLaterButton.setText(remindButtonText);
            dontAskButton.setText(dismissButtonText);
            TextView tv = (TextView)v.findViewById(R.id.title_rate_me);
            tv.setTypeface(robotoRegular);
            if (title != null) {
                Log.i("Title", "Updating");
                tv.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
                tv.setText(tv.getText().toString().trim());
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
                    imgView.setOnTouchListener(imageClickHandler);
                }
            } else {
                Log.i("Image", "ImgView made invisible");
                imgView.setVisibility(View.GONE);
            }

            mv = (TextView)v.findViewById(R.id.message_rate_me);
            mv.setTypeface(robotoLight);
        }
        else if(actionType.toUpperCase(Locale.US).equals("SHARE")||actionType.toUpperCase(Locale.US).equals("LINK")) {
            v = inflater.inflate(R.layout.action_in_app_message, container, false);
            Button dismissButton = (Button) v.findViewById(R.id.dismiss_button_action);
            dismissButton.setTypeface(robotoMedium);
            Log.i("DismissButton", "Updating");
            dismissButton.setText(dismissButtonText);
            dismissButton.setOnClickListener(closeHandler);
            Button actionButton = (Button) v.findViewById(R.id.action_button_action);
            actionButton.setTypeface(robotoMedium);
            Log.i("ActionButton", "Updating");
            actionButton.setText(actionButtonText);
            actionButton.setOnClickListener(actionEventHandler);
            TextView tv = (TextView)v.findViewById(R.id.title_action);
            tv.setTypeface(robotoRegular);
            if (title != null) {
                Log.i("Title", "Updating");
                tv.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
                tv.setText(tv.getText().toString().trim());
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
                    imgView.setOnTouchListener(imageClickHandler);
                }
            } else {
                Log.i("Image", "ImgView made invisible");
                imgView.setVisibility(View.GONE);
            }

            mv =(TextView) v.findViewById(R.id.message_action);
            mv.setTypeface(robotoLight);

        }
       // String msgText = Html.fromHtml(message).toString().trim();

        mv.setText(Html.fromHtml(message),TextView.BufferType.SPANNABLE);
        mv.setText(mv.getText().toString().trim());
        Log.i("Message","Message Updated");
        return v;
    }

    View.OnTouchListener imageClickHandler = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_MOVE||event.getAction()==MotionEvent.ACTION_UP||event.getAction()==MotionEvent.ACTION_SCROLL){
                return true;
            }else{
                onClick(v);
            }
            return false;
        }
        //onClickListener's method unchanged and used
        private void onClick(View v) {
        Intent openImageLinkInBrowser = new Intent(Intent.ACTION_VIEW,Uri.parse(onClickUrl));
        try{
            Log.i("InAppIMAGE:","CLICKED");
            startActivity(openImageLinkInBrowser);
        }catch (ActivityNotFoundException e){
            Log.i("Exception: ",e.toString());
        }
        ZeTarget.updatePromotionAsSeen(campaignId);
        dismiss();
        }
    };

    View.OnClickListener askMeLater = new View.OnClickListener() {
        public void onClick(View v) {
            ZeTarget.updatePromotionAsSeen(campaignId);
            dismiss();
        }
    };

    View.OnClickListener dontAskMeAgain = new View.OnClickListener() {
        public void onClick(View v) {
            ZeTarget.removePromotion(campaignId);
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
                ZeTarget.removePromotion(campaignId);
                dismiss();
                startActivity(rateMyApp);
            }catch (ActivityNotFoundException e){
                Log.i("Exception: ",e.toString());
            }
        }
    };

    View.OnClickListener actionEventHandler = new View.OnClickListener(){
        public void onClick(View v){
            if(actionType.toUpperCase(Locale.US).equals("LINK")){
                Intent openLinkInBrowser = new Intent(Intent.ACTION_VIEW,Uri.parse(actionButtonUrl));
                try{
                    Log.i("ACTIONEVENT:", "CLICKED");
                    ZeTarget.updatePromotionAsSeen(campaignId);
                    startActivity(openLinkInBrowser);
                    dismiss();
                }catch (ActivityNotFoundException e){
                    Log.i("Exception: ",e.toString());
                }
            }
            else if(actionType.toUpperCase(Locale.US).equals("SHARE")){
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT,shareText);
                share.setType("text/plain");
                try{
                    Log.i("SHAREEVENT:", "CLICKED");
                    ZeTarget.updatePromotionAsSeen(campaignId);
                    startActivity(share);
                    dismiss();
                }catch(ActivityNotFoundException e){
                    Log.i("Exception: ",e.toString());
                }
            }

        }
    };

    View.OnClickListener closeHandler = new View.OnClickListener() {
        public void onClick(View v) {
            ZeTarget.updatePromotionAsSeen(campaignId);
            dismiss();
        }
    };

}

