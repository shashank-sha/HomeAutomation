package com.zemosolabs.zetarget.sdk;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ResourceBundle;

/**
 * Created by sudhanshu on 9/10/15.
 */
public class ZResources extends Resources {

    // The reference to the original Resources object that is created by the Android system.

    String TAG ="ZResources";
    private Resources origResource;

    public ZResources(AssetManager assets, DisplayMetrics metrics, Configuration config,Resources origResources) {
        super(assets, metrics, config);
        origResource = origResources;

    }

    public ZResources(Resources origResources) {
        super(origResources.getAssets(),origResources.getDisplayMetrics(),origResources.getConfiguration());
        origResource=origResources;
    }


    @Override
    public CharSequence getText(int id) throws NotFoundException {
        CharSequence output=origResource.getText(id);
        Log.d(TAG, "getText(int id) id=" + id + " output=" + output);
        return output;
    }

    @Override
    public CharSequence getQuantityText(int id, int quantity) throws NotFoundException {
        CharSequence output = origResource.getQuantityText(id,quantity);
        Log.d(TAG,"getQuantityText id=" + id +"getQuantityText quantity=" + quantity+ " output=" + output);
        return output;
    }

    @NonNull
    @Override
    public String getString(int id) throws NotFoundException {
        String output = origResource.getString(id);
        Log.d(TAG,"getString(int id) id=" + id + "output" + output);
        return output;
    }

    @NonNull
    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        String output = origResource.getString(id,formatArgs);
        Log.d(TAG,"getString(int id,Object...formatArgs)id=" + id +"output=" + output);
        return output;
    }

    @Override
    public String getQuantityString(int id, int quantity, Object... formatArgs) throws NotFoundException {
        String output = origResource.getQuantityString(id, quantity, formatArgs);
        Log.d(TAG, "getQuantityString(int id,Quantity,Object...formatArgs)id=" + id + "Quantity=" + quantity + "output=" + output);
        return output;
    }

    @Override
    public String getQuantityString(int id, int quantity) throws NotFoundException {
        String output = origResource.getQuantityString(id, quantity);
        Log.d(TAG,"getQuantityString(int id,int quantity) id=" + id +"getQuantityString quantity=" + quantity+ " output=" + output);
        return output;
    }

    @Override
    public CharSequence getText(int id, CharSequence def) {
        CharSequence output = origResource.getText(id,def);
        Log.d(TAG,"getText(int id,CharSequence def) id=" + id + "def=" + def+ " output=" + output );
        return output;


    }

    @Override
    public CharSequence[] getTextArray(int id) throws NotFoundException {
        CharSequence[] output = origResource.getTextArray(id);
        Log.d(TAG,"getTextArray id=" + id + " output=" + output);
        return output;
    }

    @Override
    public String[] getStringArray(int id) throws NotFoundException {
        String[] output = origResource.getStringArray(id);
        Log.d(TAG,"getStringArray id=" + id + " output=" + output);
        return output;
    }

    @Override
    public int[] getIntArray(int id) throws NotFoundException {
        int[] output = origResource.getIntArray(id);
        Log.d(TAG,"getIntArray id=" + id + " output=" + output);
        return output;
    }

    @Override
    public TypedArray obtainTypedArray(int id) throws NotFoundException {
        TypedArray output = origResource.obtainTypedArray(id);
        Log.d(TAG,"obtainTypedArray id=" + id + " output=" + output);
        return output;
    }

    @Override
    public float getDimension(int id) throws NotFoundException {
        float output = origResource.getDimension(id);
        Log.d(TAG,"getDimension id=" + id + " output=" + output);
        return output;
    }

    @Override
    public int getDimensionPixelOffset(int id) throws NotFoundException {
        int output = origResource.getDimensionPixelOffset(id);
        Log.d(TAG,"getDimensionalPixelOffset id=" + id + " output=" + output);
        return output;
    }

    @Override
    public int getDimensionPixelSize(int id) throws NotFoundException {
        int output = origResource.getDimensionPixelSize(id);
        Log.d(TAG,"getDimensionPixelSize id=" + id + " output=" + output);
        return output;
    }

    @Override
    public float getFraction(int id, int base, int pbase) {
        float output = origResource.getFraction(id, base, pbase);
        Log.d(TAG,"getFraction id=" + id +"base=" +base + "pbase=" + pbase + " output=" + output);
        return output;
    }


    @Override
    public Drawable getDrawable(int id) throws NotFoundException {
        Drawable output = origResource.getDrawable(id);
        Log.d(TAG,"getDrawable id=" + id + " output=" + output);
        return output;
    }

    @Override
    public Drawable getDrawable(int id, Theme theme) throws NotFoundException {
        Drawable output = origResource.getDrawable(id);
        Log.d(TAG,"getDrawable id=" + id + "Theme=" + theme+ " output=" + output);
        return output;
    }


    @Override
    public Drawable getDrawableForDensity(int id, int density) throws NotFoundException {
        Drawable output = origResource.getDrawableForDensity(id, density);
        Log.d(TAG,"getDrawableForDensity(int id, int density) id=" + id +"density=" + density+ " output=" + output);
        return output;
    }


    @Override
    public Drawable getDrawableForDensity(int id, int density, Theme theme) {
        Drawable output = origResource.getDrawableForDensity(id,density,theme);
        Log.d(TAG,"getDrawableForDensity(int id, int density, Theme theme) id=" + id +"density=" + density + "Theme=" +theme+ " output=" + output);
        return output;
    }

    @Override
    public Movie getMovie(int id) throws NotFoundException {
        Movie output = origResource.getMovie(id);
        Log.d(TAG,"getMovie(int id) id=" + id + " output=" + output);
        return output;
    }

    @Override
    public int getColor(int id) throws NotFoundException {
        int output = origResource.getColor(id);
        Log.d(TAG,"getColor(int id) id=" + id + " output=" + output);
        return output;
    }

    @Nullable
    @Override
    public ColorStateList getColorStateList(int id) throws NotFoundException {
        ColorStateList output = origResource.getColorStateList(id);
        Log.d(TAG,"getColorStateList(int id) id=" + id + " output=" + output);
        return output;
    }

    @Override
    public boolean getBoolean(int id) throws NotFoundException {
        boolean output = origResource.getBoolean(id);
        Log.d(TAG,"getBoolean(int id) id=" + id + " output=" + output);
        return output;
    }

    @Override
    public int getInteger(int id) throws NotFoundException {
        int output = origResource.getInteger(id);
        Log.d(TAG,"getInteger(int id) id=" + id+ " output=" + output);
        return output;
    }

    @Override
    public XmlResourceParser getLayout(int id) throws NotFoundException {
        XmlResourceParser output = origResource.getLayout(id);
        Log.d(TAG,"getLayout(int id) id=" + id+ " output=" + output);

        MyInvocationHandler handler= new MyInvocationHandler(output);
        Class[] intfs= new Class[] {XmlResourceParser.class,XmlPullParser.class,AttributeSet.class,AutoCloseable.class};
        //We are creating a proxy for the XmlResourceParser and returning back the proxy so that the method can be hijacked using the
        //invocation handler
        XmlResourceParser proxy= (XmlResourceParser) Proxy.newProxyInstance(MyInvocationHandler.class.getClassLoader(),intfs,handler);
        Log.d(TAG,"getLayout(int id) id=" + id+ " output=" + output);
        Log.d(TAG," ***************************Returned the proxied instance in getLayout******************************");
        return proxy;
        //return output;
    }

    @Override
    public XmlResourceParser getAnimation(int id) throws NotFoundException {
        XmlResourceParser output = origResource.getAnimation(id);
        Log.d(TAG,"getAnimation(int id) id=" + id+ " output=" + output);
        return output;
    }

    @Override
    public XmlResourceParser getXml(int id) throws NotFoundException {
        XmlResourceParser output = origResource.getXml(id);

        // The proxy experiment did NOT work because somewhere in Android codebase they are typcasting to XMLBlock.Parser-
        // the concrete implementation and NOT relying on the interface.
        //MyInvocationHandler handler= new MyInvocationHandler(output);
        //Class[] intfs= new Class[] {XmlResourceParser.class,XmlPullParser.class,AttributeSet.class,AutoCloseable.class};
        // We are creating a proxy for the XmlResourceParser and returning back the proxy so that the method can be hijacked using the
        // invocation handler
        //XmlResourceParser proxy= (XmlResourceParser) Proxy.newProxyInstance(MyInvocationHandler.class.getClassLoader(),intfs,handler);
        Log.d(TAG,"getXml(int id) id=" + id+ " output=" + output);
        //Log.d(TAG," ***************************Returned the proxied instance******************************");
        //return proxy;
        return output;
    }

    @Override
    public InputStream openRawResource(int id) throws NotFoundException {
        InputStream output = origResource.openRawResource(id);
        Log.d(TAG,"openRawResources(int id) id=" + id+ " output=" + output);
        return output;
    }

    @Override
    public InputStream openRawResource(int id, TypedValue value) throws NotFoundException {
        InputStream output = origResource.openRawResource(id);
        Log.d(TAG,"openRawResources(int id,TypedValue value) id=" + id + "value=" + value+ " output=" + output);
        return output;
    }

    @Override
    public AssetFileDescriptor openRawResourceFd(int id) throws NotFoundException {
        AssetFileDescriptor output = origResource.openRawResourceFd(id);
        Log.d(TAG,"openRawResourceFd(int id) id=" + id+ " output=" + output);
        return output;
    }

    @Override
    public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {

        origResource.getValue(id,outValue,resolveRefs);
        Log.d(TAG,"getValue(int id,TypedValue outValue) id=" + id + "outValue=" + outValue);
    }

    @Override
    public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        origResource.getValueForDensity(id,density,outValue,resolveRefs);


        Log.d(TAG,"getValueForDensity(int id,int density,TypedValue outValue,boolean resolveRefs) id=" + id + "density="+ density + "outValue=" + outValue +
                "resolveRefs=" + resolveRefs);
    }

    @Override
    public void getValue(String name, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        origResource.getValue(name,outValue,resolveRefs);
        Log.d(TAG,"getValue(String name,TypedValue outValue,boolean resolveRefs) name=" + name  + "outValue=" + outValue +
                "resolveRefs=" + resolveRefs);
    }

    @Override
    public TypedArray obtainAttributes(AttributeSet set, int[] attrs) {
        TypedArray output = origResource.obtainAttributes(set, attrs);
        Log.d(TAG,"obtainAttributes set=" + set + "attrs" + attrs + " output=" + output);
        return output;
    }


    @Override
    public void updateConfiguration(Configuration config, DisplayMetrics metrics) {
        int j=0;
        if (origResource!=null) origResource.updateConfiguration(config, metrics);
        Log.d(TAG, "updateConfiguration config=" + config + "metrics" + metrics);

    }

    @Override
    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics output = origResource.getDisplayMetrics();
        Log.d(TAG,"DisplayMetrics"+ " output=" + output);
        return output;
    }

    @Override
    public Configuration getConfiguration() {
        Configuration output = origResource.getConfiguration();
        Log.d(TAG,"getConfiguration"+ " output=" + output);
        return output;
    }

    @Override
    public int getIdentifier(String name, String defType, String defPackage) {
         int output = origResource.getIdentifier(name,defType,defPackage);
        Log.d(TAG,"getIdentifier name=" + name + "defType=" + defType + "defPackage=" + defPackage + " output=" + output);
        return output;
    }

    @Override
    public String getResourceName(int resid) throws NotFoundException {
        String output = origResource.getResourceName(resid);
        Log.d(TAG,"getResourceName resid=" + resid+ " output=" + output);
        return output;
    }

    @Override
    public String getResourcePackageName(int resid) throws NotFoundException {
        String output = origResource.getResourcePackageName(resid);
        Log.d(TAG,"getResourcePackageName resid=" + resid+ " output=" + output);
        return output;
    }

    @Override
    public String getResourceTypeName(int resid) throws NotFoundException {
        String output = origResource.getResourceTypeName(resid);
        Log.d(TAG,"getResourceTypeName resid=" + resid+ " output=" + output);
        return output;
    }

    @Override
    public String getResourceEntryName(int resid) throws NotFoundException {
        String output = origResource.getResourceEntryName(resid);
        Log.d(TAG,"getResourceEntryName resid=" + resid+ " output=" + output);
        return output;
    }

    @Override
    public void parseBundleExtras(XmlResourceParser parser, Bundle outBundle) throws XmlPullParserException, IOException {
        origResource.parseBundleExtras(parser, outBundle);
        Log.d(TAG, "parseBundleExtras(XmlResourceParser parser, Bundle outBundle) parser=" + parser + "outBundle=" + outBundle);

    }

    @Override
    public void parseBundleExtra(String tagName, AttributeSet attrs, Bundle outBundle) throws XmlPullParserException {
        origResource.parseBundleExtra(tagName, attrs, outBundle);
        Log.d(TAG, "parseBundleExtras(String tagName, AttributeSet attrs, Bundle outBundle) tagName=" + tagName + "outBundle=" + outBundle
        + "attrs=" + attrs);

    }

    static class MyInvocationHandler implements InvocationHandler {

        private Object orig;

        private static String MYTAG = "ZResources.MyInvocationHandler";

        public MyInvocationHandler(Object original) {
            this.orig=original;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            Object ret=method.invoke(orig,objects);
            if (method.getName().equals("getAttributeValue")) {
                Log.d(MYTAG,"********************************MyInvocationHandler getAttributeValue input objects="+objects+" return="+ret);
            }
            return ret;
        }
    }
}
