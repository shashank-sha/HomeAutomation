package com.zemosolabs.zetarget.sdk;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;


import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
//import android.content.res.

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sudhanshu on 13/10/15.
 */
public class ZResourcesNew extends Resources {

    String TAG ="ZResourcesNEW";

    public ZResourcesNew(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        super(assets, metrics, config);
    }

    private String activityClassName ;

    void setActivityClassName(String s,Context activity) {
        activityClassName=s;
        obtainFieldNamesHashmap(activity);
    }

    private Map<Integer,String> stringFieldValuesVersusNames = new HashMap<Integer,String>();

    private void obtainFieldNamesHashmap(Context ctxt) {
        String packageName = activityClassName.substring(0,activityClassName.lastIndexOf("."));
        String classNameOfRClass= packageName+".R";
        String classNameOfRStringsClass =packageName+".R$string";
        try {
            Log.d(TAG,"The R class to fetch= "+classNameOfRClass);
            Class rclz=Class.forName(classNameOfRClass);
            Log.d(TAG,"The R string class to fetch= "+classNameOfRStringsClass);

            Class clz=Class.forName(classNameOfRStringsClass,true,ctxt.getClassLoader());
            Log.d(TAG,"Obtained clazz instance of "+classNameOfRStringsClass);
            Field[] fields =clz.getFields();
            Log.d(TAG,"Obtained fields array of length="+fields.length);
            for (int i=0;i<fields.length;i++) {
                String fieldName=fields[i].getName();
                Integer fieldValue=(Integer) fields[i].get(null);
                stringFieldValuesVersusNames.put(fieldValue,fieldName);
                Log.d(TAG,"Added "+fieldValue+"="+fieldName+" into stringFieldValuesVersusNames Map");
            }


        } catch (ClassNotFoundException cnfe) {
            Log.e(TAG,cnfe.getMessage(),cnfe);
        } catch (IllegalAccessException iae) {
            Log.e(TAG,iae.getMessage(),iae);
        }

    }


    @Override
    public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {

        super.getValue(id, outValue, resolveRefs);
        String resourceName=super.getResourceName(id);
        Log.d(TAG, "getValue(int id,TypedValue outValue) id=" + id + "("+resourceName+") outValue=" + outValue);
    }

    private boolean layoutXMLBeingHandled=false;
    @Override
    public void getValue(String name, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        super.getValue(name, outValue, resolveRefs);
        if (outValue.type == TypedValue.TYPE_STRING) {
            CharSequence cs=outValue.coerceToString();
            if (cs.toString().contains("layout")) {
                layoutXMLBeingHandled=true;
            } else {
                layoutXMLBeingHandled=false;
            }
        } else {
            layoutXMLBeingHandled=false;
        }
        Log.d(TAG,"getValue(String name,TypedValue outValue,boolean resolveRefs) name=" + name  + "outValue=" + outValue +
                "resolveRefs=" + resolveRefs);

    }

    @Override
    public CharSequence getText(int id) throws NotFoundException {
        CharSequence output=super.getText(id);
        if (stringFieldValuesVersusNames.containsKey(id)) {
            String s = stringFieldValuesVersusNames.get(id);
            String reverse = "";
            int len = s.length();
            for (int i = len - 1; i >= 0; i--) {
                reverse += s.charAt(i);
            }
            Log.d(TAG, "getText(int id) id=" + id + " output=" + reverse);
            return reverse;
        }
        Log.d(TAG, "getText(int id) id=" + id + " output=" + output);
        //stringFieldValuesVersusNames.put(2131165241,reverse);
        return output;



    }

    @Override
    public TypedArray obtainAttributes(AttributeSet set, int[] attrs) {
        TypedArray output = super.obtainAttributes(set, attrs);
        Log.d(TAG,"obtainAttributes set=" + set + "attrs=" + Arrays.toString(attrs));
//        if (layoutXMLBeingHandled) {
//            Throwable t = new Throwable();
//            t.fillInStackTrace();
//            Log.e(TAG, "ObtainAttributes Stack =", t);
//            Log.d(TAG, " -----------------------output=" + output);
//        }
        return output;
    }

}
