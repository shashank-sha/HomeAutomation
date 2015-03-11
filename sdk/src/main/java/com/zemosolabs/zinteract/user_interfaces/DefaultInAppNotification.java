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
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by praveen on 30/01/15.
 */
public class DefaultInAppNotification extends ZinteractInAppNotification {

    private Context context;
    private String campaignId;
    private String title;
    private String message;
    private String templateType;
    private String imageUrl;
    private String onClickUrl;
    private String actionType;
    private String dismissButtonText;
    private String actionButtonUrl;
    private String actionButtonText;
    private String remindButtonText;
    private String shareText;
    private boolean DEBUGGING_MODE = true;

    private static final String TAG = "InAppNotification";

    public void customize(Context context, String campaignId, JSONObject template) {
        this.campaignId = campaignId;
        this.context = context;
        if (DEBUGGING_MODE) {
            templateType = "ACTION";
            title = "Test Title";
            message = "This message is a test message. Let's see how the UI reacts when the message exceeds more than one line ";
            imageUrl = "http://news.bbcimg.co.uk/media/images/81539000/jpg/_81539447_95ca831d-7a1d-4b02-b3ca-0c9968649937.jpg";
            onClickUrl = "http://www.google.com";
            actionType = "SHARE";
            dismissButtonText = "CANCEL";
            actionButtonUrl = "http://www.facebook.com";
            shareText = "This App is awesome! Check it out";
            actionButtonText = "GO";
            remindButtonText = "REMIND ME LATER";
            return;
        }
        try {
            templateType = template.getString("template_type");
            message = template.getString("subject");
            if (template.get("imageUrl") != null) {
                imageUrl = template.getString("imageUrl");
            } else imageUrl = null;
            if (template.get("onClickUrl") != null) {
                onClickUrl = template.getString("onClickUrl");
            } else onClickUrl = null;
            if (template.get("name") != null) {
                title = template.getString("name");
            } else title = null;
            JSONObject definition = template.getJSONObject("definition");
            actionType = definition.getString("action");
            dismissButtonText = definition.getString("dismissText");
            JSONObject actionButton;
            if (definition.get("actionButton") != null) {
                actionButton = definition.getJSONObject("actionButton");
            } else actionButton = new JSONObject();

            switch (actionType) {
                case "link":
                    if (actionButton.get("url") != null) {
                        actionButtonUrl = actionButton.getString("url");
                    } else actionButtonUrl = null;

                    if (actionButton.get("text") != null) {
                        actionButtonText = actionButton.getString("text");
                    } else actionButtonText = "Check it out";
                    break;
                case "rate":
                    if (actionButton.get("text") != null) {
                        actionButtonText = actionButton.getString("text");
                    } else actionButtonText = "Rate Us";
                    if (definition.getJSONObject("remindButton").get("text") != null) {
                        remindButtonText = definition.getJSONObject("remindButton").getString("text");
                    }
                    remindButtonText = "Remind Me Later";
                    break;
                case "share":
                    if(actionButton.get("share_text") != null){
                        shareText = actionButton.getString("share_text");
                    }
                    if (actionButton.get("text") != null) {
                        actionButtonText = actionButton.getString("text");
                    } else actionButtonText = "Share";
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


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
        Log.i("inside Notification","control came here");
        View v;
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if(templateType.toUpperCase(Locale.US).equals("REGULAR")){
            v = inflater.inflate(R.layout.regular_in_app_message, container, false);
            Button button = (Button)v.findViewById(R.id.dismiss_button_regular);
            Log.i("DismissButton","Updating");
            button.setText(dismissButtonText);
            button.setOnClickListener(closeHhandler);
            View tv = v.findViewById(R.id.title_regular);
            if(title!=null){
                Log.i("Title","Updating");
                ((TextView)tv).setText(title);
            }
            else{
                Log.i("Title","Being Removed");
                tv.setVisibility(View.GONE);
            }
            ImageView imgView = (ImageView) v.findViewById(R.id.img_container_regular);
            if(imageUrl!=null){
                Log.i("Image","ImgView updating");
                new UpdateImageViewAsyncTask(imageUrl,imgView).execute();
            }
            else{
                Log.i("Image","ImgView made invisible");
                imgView.setVisibility(View.GONE);
            }

            View mv = v.findViewById(R.id.message_regular);
            ((TextView)mv).setText(message);
        }
        else if(templateType.toUpperCase(Locale.US).equals("RATE_ME")){
            v = inflater.inflate(R.layout.rate_me_in_app_message, container, false);
            Button askLaterButton = (Button)v.findViewById(R.id.remind_later_button_rate_me);
            askLaterButton.setOnClickListener(askMeLater);
            Button dontAskButton = (Button)v.findViewById(R.id.dismiss_button_rate_me);
            dontAskButton.setOnClickListener(dontAskMeAgain);
            Button rateItButton = (Button)v.findViewById(R.id.rate_button_rate_me);
            rateItButton.setOnClickListener(rateMeEventHandler);
            Log.i("RateMeButton","Updating");
            rateItButton.setText(actionButtonText);
            askLaterButton.setText(remindButtonText);
            dontAskButton.setText(dismissButtonText);
            View tv = v.findViewById(R.id.title_rate_me);
            if(title!=null){
                Log.i("Title","Updating");
                ((TextView)tv).setText(title);
            }
            else{
                Log.i("Title","Being Removed");
                tv.setVisibility(View.GONE);
            }
            ImageView imgView = (ImageView) v.findViewById(R.id.img_container_rate_me);
            if(imageUrl!=null){
                Log.i("Image","ImgView updating");
                new UpdateImageViewAsyncTask(imageUrl,imgView).execute();
            }
            else{
                Log.i("Image","ImgView made invisible");
                imgView.setVisibility(View.GONE);
            }

            View mv = v.findViewById(R.id.message_rate_me);
            ((TextView)mv).setText(message);
        }
        else if(templateType.toUpperCase(Locale.US).equals("ACTION")){
            v = inflater.inflate(R.layout.action_in_app_message, container, false);
            Button dismissButton = (Button)v.findViewById(R.id.dismiss_button_action);
            Log.i("DismissButton","Updating");
            dismissButton.setText(dismissButtonText);
            dismissButton.setOnClickListener(closeHhandler);
            Button actionButton = (Button)v.findViewById(R.id.action_button_action);
            Log.i("ActionButton","Updating");
            actionButton.setText(actionButtonText);
            actionButton.setOnClickListener(actionEventHandler);
            View tv = v.findViewById(R.id.title_action);
            if(title!=null){
                Log.i("Title","Updating");
                ((TextView)tv).setText(title);
            }
            else{
                Log.i("Title","Being Removed");
                tv.setVisibility(View.GONE);
            }
            ImageView imgView = (ImageView) v.findViewById(R.id.img_container_action);
            if(imageUrl!=null){
                Log.i("Image","ImgView updating");
                new UpdateImageViewAsyncTask(imageUrl,imgView).execute();
            }
            else{
                Log.i("Image","ImgView made invisible");
                imgView.setVisibility(View.GONE);
            }

            View mv = v.findViewById(R.id.message_action);
            ((TextView)mv).setText(message);
        }
        else{
            v=null;
        }



        Log.i("Message","Message Updated");
        return v;
    }

    View.OnClickListener askMeLater = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                JSONObject j = new JSONObject();
                j.put("campaignId", campaignId);
                Zinteract.logEvent("ViewEventLater", j);
            }
            catch (Exception e){
                Log.e(TAG, "Exception: " + e);
            }

            dismiss();
        }
    };

    View.OnClickListener dontAskMeAgain = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                JSONObject k = new JSONObject();
                k.put("campaignId", campaignId);
                Zinteract.logEvent("DoNotAskMeAgain", k);
                Zinteract.updatePromotionAsSeen(campaignId);
            }
            catch (Exception e){
                Log.e(TAG, "Exception: " + e);
            }
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
                    Log.i("ACTIONEVENT:","CLICKED");
                    startActivity(openLinkInBrowser);
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
                }catch(ActivityNotFoundException e){
                    Log.i("Exception: ",e.toString());
                }
            }

        }
    };

    View.OnClickListener closeHhandler = new View.OnClickListener() {
        public void onClick(View v) {
            Zinteract.updatePromotionAsSeen(campaignId);
            dismiss();
        }
    };

    private class UpdateImageViewAsyncTask extends AsyncTask<Void,Void,Bitmap> {

        private String url;
        private ImageView imgView;

        UpdateImageViewAsyncTask(String url, ImageView imgView){
            this.url = url;
            this.imgView = imgView;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Rect rect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
                Rect bottomRect = new Rect(0,bitmap.getHeight()/2,bitmap.getWidth(),bitmap.getHeight());
                RectF rectF = new RectF(rect);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                Canvas canvas = new Canvas(resultBitmap);
                float cornerRadius = getResources().getDimension(R.dimen.bitmap_corner_radius);
                canvas.drawARGB(0,0,0,0);
                canvas.drawRoundRect(rectF,cornerRadius,cornerRadius,paint);
                canvas.drawRect(bottomRect,paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap,rect,rect,paint);
                Log.i("Bitmap","Bitmap Loaded");
                return resultBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imgView.setImageBitmap(bitmap);
        }
    }
}

