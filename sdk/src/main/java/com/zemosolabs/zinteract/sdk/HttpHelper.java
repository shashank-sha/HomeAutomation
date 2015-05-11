package com.zemosolabs.zinteract.sdk;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by praveen on 21/01/15.
 */
public class HttpHelper {

    private static HttpHelper httpHelper;

    private static final String TAG = "com.zemosolabs.zinteract.sdk.HttpHelper";


    private HttpHelper(){

    }

    synchronized static HttpHelper getHttpHelper(){
        if(httpHelper == null){
            httpHelper = new HttpHelper();
        }
        return httpHelper;
    }

    static String doPost(String url, JSONObject postParams){

        if(Zinteract.isDebuggingOn()){
            Log.d(TAG,"doPost() called");
        }

        postParams = addRequiredParams(postParams);

        /*if(Zinteract.isDebuggingOn()){
            Log.d(TAG, "Post parameters are: " + postParams.toString());
            if(url.equalsIgnoreCase(Constants.Z_SEND_SNAPSHOT_URL)){
                ScreenCapture.getInstance().writeToFile(postParams.toString());
            }
        }*/

        HttpResponse response;
        String stringResponse = null;
        HttpPost postRequest = new HttpPost(url);
        HttpClient client = new DefaultHttpClient();
        try {
            String jsonString = postParams.toString();
            postRequest.setHeader("Accept", "application/json");
            postRequest.setHeader("Content-type", "application/json");
            postRequest.setEntity(new StringEntity(jsonString));
            response = client.execute(postRequest);
            stringResponse = EntityUtils.toString(response.getEntity());
            if(Zinteract.isDebuggingOn()){
                if(url.equalsIgnoreCase(Constants.Z_SEND_SNAPSHOT_URL)){
                    Log.i("HttpResponse",stringResponse);
                }
            }
        } catch (org.apache.http.conn.HttpHostConnectException e) {
            // Log.w(TAG,
            // "No internet connection found, unable to upload events");
        } catch (java.net.UnknownHostException e) {
            // Log.w(TAG,
            // "No internet connection found, unable to upload events");N
        } catch (ClientProtocolException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (AssertionError e) {
            // This can be caused by a NoSuchAlgorithmException thrown by DefaultHttpClient
            Log.e(TAG, "Exception:", e);
        } catch (Exception e) {
            // Just log any other exception so things don't crash on upload
            Log.e(TAG, "Exception:", e);
        } finally {
            if (client.getConnectionManager() != null) {
                client.getConnectionManager().shutdown();
            }
        }
        if(Zinteract.isDebuggingOn()){
            Log.d(TAG, "Post Response is: " + stringResponse);
        }
        return stringResponse;
    }

    private static JSONObject addRequiredParams(JSONObject postParams){
        try {
            postParams.put("apiKey",CommonUtils.replaceWithJSONNull(Zinteract.getApiKey()));
            postParams.put("userId",CommonUtils.replaceWithJSONNull(Zinteract.getUserId()));

            postParams.put("deviceId", CommonUtils.replaceWithJSONNull(Zinteract.getDeviceId()));
            postParams.put("sdkId", CommonUtils.replaceWithJSONNull(Constants.Z_SDK_ID));
            postParams.put("appVersion",CommonUtils.replaceWithJSONNull(Zinteract.deviceDetails.getVersionName()));
            return postParams;
        }
        catch (Exception e){
            Log.e(TAG,"Exception: "+e);
        }
        return postParams;
    }
}
