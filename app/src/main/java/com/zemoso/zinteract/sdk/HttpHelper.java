package com.zemoso.zinteract.sdk;

import android.util.Log;

import com.zemoso.zinteract.ZinteractSampleApp.BuildConfig;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

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

    public static String doPost(String url, List<NameValuePair> postParams){

        if(BuildConfig.DEBUG && Zinteract.isDebuggingOn()){
            Log.d(TAG,"doPost() called");
        }

        postParams = addRequiredParams(postParams);

        HttpResponse response;
        String stringResponse = null;
        HttpPost postRequest = new HttpPost(url);
        try {
            postRequest.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            // According to
            // http://stackoverflow.com/questions/5049524/is-java-utf-8-charset-exception-possible,
            // this will never be thrown
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

    private static List<NameValuePair> addRequiredParams(List<NameValuePair> postParams){
        postParams.add(new BasicNameValuePair("apiKey", Zinteract.getApiKey()));
        postParams.add(new BasicNameValuePair("userId", Zinteract.getUserId()));
        postParams.add(new BasicNameValuePair("deviceId", Zinteract.getDeviceId()));
        postParams.add(new BasicNameValuePair("sdkId", Constants.Z_SDK_ID));
        return postParams;
    }
}
