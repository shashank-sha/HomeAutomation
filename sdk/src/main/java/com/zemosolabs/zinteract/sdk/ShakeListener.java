package com.zemosolabs.zinteract.sdk;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by vedaprakash on 22/4/15.
 */
public class ShakeListener implements SensorEventListener {
    Activity currentActivity;
    private static ShakeListener instance;
    private SensorManager sensorManager;
    private Sensor accSensor,gyroSensor;
    private Float lastAccelerationX,lastAccelerationY,lastAccelerationZ;
    private int changeOfDirectionCount=0;
    private long timestamp;
    private long thresholdTime = 500;

    ShakeListener(Activity activity){
        currentActivity=activity;
    }

    ShakeListener getInstance(){
        if(instance==null){
            instance = new ShakeListener(ZinteractActivityLifecycleCallbacks.currentActivity);
        }else{
            instance.currentActivity=ZinteractActivityLifecycleCallbacks.currentActivity;
        }
        return instance;
    }
    void initialize(){
        sensorManager = (SensorManager)currentActivity.getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(accSensor!=null){
            sensorManager.registerListener(this,accSensor,3);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor==accSensor){
            if(lastAccelerationX==null||lastAccelerationY==null||lastAccelerationZ==null){
                lastAccelerationX = event.values[0];
                lastAccelerationY = event.values[1];
                lastAccelerationZ = event.values[2];
                timestamp=System.currentTimeMillis();
                return;
            }

            if(changeOfDirection(event)){
                if(System.currentTimeMillis()-timestamp<thresholdTime) {
                    changeOfDirectionCount++;
                }else{
                    changeOfDirectionCount=1;
                }
            }
            lastAccelerationX=event.values[0];
            lastAccelerationY=event.values[1];
            lastAccelerationZ=event.values[2];

        }
    }

    private boolean changeOfDirection(SensorEvent event) {
        if( (lastAccelerationX>0&&event.values[0]<0) || (lastAccelerationX<0&&event.values[0]>0) ){
            return true;
        }
        if( (lastAccelerationY>0&&event.values[1]<0) || (lastAccelerationY<0&&event.values[1]>0) ){
            return true;
        }
        if( (lastAccelerationZ>0&&event.values[2]<0) || (lastAccelerationZ<0&&event.values[2]>0) ){
            return true;
        }

        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    void purge(){
        sensorManager.unregisterListener(this);
    }
}
