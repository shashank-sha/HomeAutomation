package com.zemoso.zinteract.sdk;

import android.util.Log;

import com.zemoso.zinteract.ZinteractSampleApp.BuildConfig;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by praveen on 21/01/15.
 */
public class HttpHelper {

    private static HttpHelper httpHelper;

    private static final String TAG = "com.zemoso.zinteract.sdk.HttpHelper";


    private HttpHelper(){

    }

    public synchronized static HttpHelper getHttpHelper(){
        if(httpHelper == null){
            httpHelper = new HttpHelper();
        }
        return httpHelper;
    }

    public static String doPost(String url, JSONObject postParams){

        if(BuildConfig.DEBUG && Zinteract.isDebuggingOn()){
            Log.d(TAG,"doPost() called");
        }

        postParams = addRequiredParams(postParams);

        if(BuildConfig.DEBUG && Zinteract.isDebuggingOn()){
            Log.d(TAG, "Post parameters are: " + postParams.toString());
        }

        HttpResponse response;
        String stringResponse = null;
        HttpPost postRequest = new HttpPost(url);
        try {
            String jsonString = postParams.toString();
            //postRequest.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
            postRequest.setHeader("Accept", "application/json");
            postRequest.setHeader("Content-type", "application/json");
            postRequest.setEntity(new StringEntity(jsonString));
        } catch (UnsupportedEncodingException e) {
            // According to
            // http://stackoverflow.com/questions/5049524/is-java-utf-8-charset-exception-possible,
            // this will never be thrown
            Log.e(TAG, e.toString());
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }
        HttpClient client = new DefaultHttpClient();
        try {
            response = client.execute(postRequest);
            stringResponse = EntityUtils.toString(response.getEntity());
        } catch (org.apache.http.conn.HttpHostConnectException e) {
            // Log.w(TAG,
            // "No internet connection found, unable to upload events");
        } catch (java.net.UnknownHostException e) {
            // Log.w(TAG,
            // "No internet connection found, unable to upload events");
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
        return stringResponse;
    }

    private static JSONObject addRequiredParams(JSONObject postParams){
        try {
            postParams.put("apiKey",CommonUtils.replaceWithJSONNull(Zinteract.getApiKey()));
            postParams.put("userId",CommonUtils.replaceWithJSONNull(Zinteract.getUserId()));

            postParams.put("deviceId", CommonUtils.replaceWithJSONNull(Zinteract.getDeviceId()));
            postParams.put("sdkId", CommonUtils.replaceWithJSONNull(Constants.Z_SDK_ID));
            return postParams;
        }
        catch (Exception e){
            Log.e(TAG,"Exception: "+e);
        }
        return postParams;
    }
}
