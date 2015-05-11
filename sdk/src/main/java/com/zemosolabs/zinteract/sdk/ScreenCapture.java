package com.zemosolabs.zinteract.sdk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
    synchronized static ScreenCapture  getInstance(){
        if(instance==null){
           return instance = new ScreenCapture();
        }
        return instance;
    }

    void captureAndSend(){
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
        //writeToFile(viewsInAPage.toString());
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
                                Object value = methodUnderInspection.invoke(v);
                                if(value instanceof Drawable){
                                    String base64BackgroundDrawable = getBase64ImageOfDrawable((Drawable)((Drawable) value).mutate());
                                    property.put("value",base64BackgroundDrawable);
                                    property.put("type","Base64Bitmap");
                                   // property.put("drawableFeatures",writeToJSONDrawableProps((Drawable)value));
                                }else {
                                    property.put("value", methodUnderInspection.invoke(v));
                                    property.put("type", methodUnderInspection.getReturnType().getCanonicalName());
                                }
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

    private JSONArray writeToJSONDrawableProps(Drawable value) {
        JSONArray drawableProps = new JSONArray();
        try{
            Method[] methodsOfView = value.getClass().getMethods();
            for(Method methodUnderInspection:methodsOfView){
                if(methodUnderInspection.getParameterTypes().length==0){
                    if(methodUnderInspection.getReturnType()!=Void.TYPE && methodUnderInspection.getReturnType()!=Void.class){
                        String methodName = methodUnderInspection.getName();
                        if(isAccessorMethod(methodName)) {
                            JSONObject property = new JSONObject();
                            try {
                                property.put("name",methodName);
                                property.put("value", methodUnderInspection.invoke(value));
                                property.put("type", methodUnderInspection.getReturnType().getCanonicalName());
                            } catch (InvocationTargetException e) {
                                property.put("name", methodName);
                                property.put("value","Exception Thrown");
                            } catch (IllegalAccessException e) {
                                property.put("name", methodName);
                                property.put("value","Exception Thrown");
                            }
                            drawableProps.put(property);
                        }
                    }
                }
            }
        }catch(JSONException e){
            Log.e("ScreenCapture","Writing Drawable to json",e);
        }
        return drawableProps;
    }


    private String getBase64ImageOfDrawable(Drawable value) {
        if(value instanceof BitmapDrawable){
            return base64ScreenshotOf(((BitmapDrawable) value).getBitmap());
        }

        int width = !value.getBounds().isEmpty()? value.copyBounds().width(): value.getIntrinsicWidth();
        int height = !value.getBounds().isEmpty()? value.copyBounds().height():value.getIntrinsicHeight();
        width = (width<=0)? 3 : width;
        height = (height<=0)? 3:height;
        Log.i("Zinteract","Base64BmpFromDrawable "+width);
        Log.i("Zinteract","Base64BmpFromDrawable "+height);
        Bitmap bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        value.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
        value.draw(canvas);
        return base64ScreenshotOf(bitmap);
    }

    private String retrieveSnapshotOfView(View v){
        Bitmap rootViewScreenshot= null;
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

        return base64ScreenshotOf(rootViewScreenshot);
    }

    private String base64ScreenshotOf(Bitmap rootViewScreenshot) {
        String base64Screenshot = "";

        if(rootViewScreenshot!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Base64OutputStream b64os = new Base64OutputStream(baos, Base64.NO_WRAP);
            rootViewScreenshot.compress(Bitmap.CompressFormat.PNG,100,b64os);
            try {
                b64os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bitmapBytes = baos.toByteArray();
            base64Screenshot = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
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
            file = new File("/sdcard/Zinteract/viewHierarchy2.txt");
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
