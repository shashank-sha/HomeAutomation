package com.zemosolabs.zinteract.sdk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vedaprakash on 30/3/15.
 */
class ScreenCapture {
    private Activity currentActivity;
    private View rootView;
    private static ScreenCapture instance;
    private JSONObject viewsInAPage;
    private File file;
    private String editSessionId ="SOIEHFUBSIFJPEHPIWPJFHIOW";

    private ScreenCapture(){

    }
    void initialize(){
        currentActivity = ZinteractActivityLifecycleCallbacks.currentActivity;
        rootView = currentActivity.getWindow().getDecorView().getRootView();
        String packageName = currentActivity.getPackageName();
        viewsInAPage = new JSONObject();
        try {
            viewsInAPage.put("osName",CommonUtils.replaceWithJSONNull(Zinteract.deviceDetails.getOSName()));
            viewsInAPage.put("osFamily",CommonUtils.replaceWithJSONNull(Zinteract.deviceDetails.getOsFamily()));
            viewsInAPage.put("osVersion",CommonUtils.replaceWithJSONNull(Zinteract.deviceDetails.getOSVersion()));
            viewsInAPage.put("brand",CommonUtils.replaceWithJSONNull(Zinteract.deviceDetails.getBrand()));
            viewsInAPage.put("manufacturer",CommonUtils.replaceWithJSONNull(Zinteract.deviceDetails.getManufacturer()));
            viewsInAPage.put("deviceModel",CommonUtils.replaceWithJSONNull(Zinteract.deviceDetails.getModel()));
            viewsInAPage.put("deviceResolution",CommonUtils.replaceWithJSONNull(Zinteract.deviceDetails.getScreenResolution()));
            viewsInAPage.put("language",CommonUtils.replaceWithJSONNull(Zinteract.deviceDetails.getLanguage()));
            viewsInAPage.put("editingSessionId",CommonUtils.replaceWithJSONNull(editSessionId));
            viewsInAPage.put("appName",CommonUtils.replaceWithJSONNull(packageName));
            viewsInAPage.put("screenName",CommonUtils.replaceWithJSONNull(ZinteractActivityLifecycleCallbacks.currentActivity.getClass().getCanonicalName()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    static ScreenCapture  getInstance(){
        if(instance==null){
           return instance = new ScreenCapture();
        }
        return instance;
    }

    void writeViewToFile(){
        try {
            JSONObject screenDetails = new JSONObject();
            screenDetails.put("hierarchyAndProps",buildHierarchy(rootView,-1));
            screenDetails.put("screenshot",retrieveSnapshotOfView(rootView));
            screenDetails.put("screenshotType","png");
            viewsInAPage.put("screenDetails",screenDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        createNewFile();
        Zinteract.sendSnapshot(viewsInAPage);
    }
    private JSONObject buildHierarchy(View view,int index){
        JSONObject viewHierarchy = new JSONObject();
        try {

            viewHierarchy.putOpt("contentDescription",view.getContentDescription());
            viewHierarchy.put("id",view.getId());
            viewHierarchy.put("index",index);
            Class<?> viewClass = view.getClass();
            JSONArray classes = new JSONArray();
            while(viewClass!=Object.class){
                classes.put(viewClass.getCanonicalName());
                viewClass = viewClass.getSuperclass();
            }
            viewHierarchy.put("classes",classes);
            /*viewHierarchy = writeToJSONViewDimAndLoc(view,viewHierarchy);
            viewHierarchy = writeToJSONViewPadding(view,viewHierarchy);*/
            viewHierarchy = writeToJSONVIewAllProps(view,viewHierarchy);
            JSONArray childrenViews = new JSONArray();
            if(view instanceof android.view.ViewGroup){
                ViewGroup vg = (ViewGroup)view;
                int size = (vg).getChildCount();
                for(int i=0; i<size; i++){
                    childrenViews.put(buildHierarchy(vg.getChildAt(i),i));
                }
                viewHierarchy.put("children",childrenViews);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return viewHierarchy;
    }

    private JSONObject writeToJSONVIewAllProps(View v,JSONObject j){
        try{
            JSONArray allViewProps = new JSONArray();
            Method[] methodsOfView = v.getClass().getMethods();
            for(Method methodUnderInspection:methodsOfView){
                if(methodUnderInspection.getParameterTypes().length==0){
                    if(methodUnderInspection.getReturnType()!=Void.TYPE && methodUnderInspection.getReturnType()!=Void.class){
                        String methodName = methodUnderInspection.getName();
                        if(isAccessorMethod(methodName)) {
                            JSONObject property = new JSONObject();
                            try {
                                property.put("name",methodName);
                                property.put("value",methodUnderInspection.invoke(v));
                                property.put("type",methodUnderInspection.getReturnType().getCanonicalName());
                            } catch (InvocationTargetException e) {
                                property.put("name", methodName);
                                property.put("value","Exception Thrown");
                            }
                            allViewProps.put(property);
                        }
                    }
                }
            }
            j.put("props",allViewProps);
        }catch(JSONException e){
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return j;
    }
    private JSONObject writeToJSONViewPadding(View v, JSONObject j){
        try{
            JSONObject padding = new JSONObject();
            padding.put("left",v.getPaddingLeft());
            padding.put("top",v.getPaddingTop());
            padding.put("bottom",v.getPaddingBottom());
            padding.put("right",v.getPaddingRight());
            j.put("padding",padding);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return j;
    }

    private JSONObject writeToJSONViewDimAndLoc(View v,JSONObject j){
        try {
        JSONObject dimAndLoc = new JSONObject();
        dimAndLoc.put("left",v.getLeft());
        dimAndLoc.put("top",v.getTop());
        dimAndLoc.put("width",v.getWidth());
        dimAndLoc.put("height",v.getHeight());
        dimAndLoc.put("translationX",v.getTranslationX());
        dimAndLoc.put("translationY",v.getTranslationY());
        j.put("dim_and_loc",dimAndLoc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return j;
    }

    private String retrieveSnapshotOfView(View v){
        Bitmap rootViewScreenshot= null;
        String base64Screenshot = "";
        try {
            Method createSnapshot = View.class.getDeclaredMethod("createSnapshot", Bitmap.Config.class, Integer.TYPE, Boolean.TYPE);
            createSnapshot.setAccessible(true);
            rootViewScreenshot = (Bitmap)createSnapshot.invoke(v,Bitmap.Config.RGB_565, Color.WHITE,false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(rootViewScreenshot==null){
            if(v.isDrawingCacheEnabled()){
                rootViewScreenshot = v.getDrawingCache();
            }else{
                v.setDrawingCacheEnabled(true);
                rootViewScreenshot = v.getDrawingCache().copy(Bitmap.Config.RGB_565,true);
                v.setDrawingCacheEnabled(false);
            }
        }
        if(rootViewScreenshot!=null){

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Base64OutputStream b64os = new Base64OutputStream(baos, Base64.NO_WRAP);
            Bitmap bitmap = rootViewScreenshot;
            bitmap.compress(Bitmap.CompressFormat.PNG,100,b64os);
            try {
                b64os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bitmapBytes = baos.toByteArray();
            base64Screenshot = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            /*int screenShotDensity = rootViewScreenshot.getDensity();
            Float scale = (float)(Zinteract.deviceDetails.getScreenDensity()/screenShotDensity);*/
        }
        return base64Screenshot;
    }

    private boolean isAccessorMethod(String methodName){
        if(methodName.matches("has[A-Z].*")){
            return true;
        }else if (methodName.matches("get[A-Z].*")){
            return true;
        }else if(methodName.matches("is[A-Z].*")){
            return true;
        }else if(methodName.matches("should[A-Z].*")){
            return true;
        }else if(methodName.matches("will[A-Z].*")){
            return true;
        }else if(methodName.matches("can[A-Z].*")){
            return true;
        }
        else
            return false;
    }

    void writeToFile(String data){
        Log.i("writeFile:","In the method");
        try {
            // FileOutputStream fOut = new FileOutputStream(file);
            FileWriter myOutWriter;
            myOutWriter = new FileWriter(file,true);
            myOutWriter.write(data);
            myOutWriter.flush();
            myOutWriter.close();
            Log.d("WRITE","SUCCESS");
        } catch (Exception e) {
            Log.d("WRITE","ERROR"+e.toString());
        }
    }

    void createNewFile(){
        try {
            file = new File("/sdcard/Zinteract/viewHierarchy.txt");
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
