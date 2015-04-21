package com.zemosolabs.zinteract.sdk;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.zemosolabs.zinteract.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Created by vedaprakash on 17/4/15.
 */
public class ScreenEditor{
    Activity currentActivity;
    static ScreenEditor screenEditor;
    private ArrayList<Change> changes = new ArrayList<Change>();
    private View rootView;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private boolean globalLayoutChangedbyMe;

    private ScreenEditor(Activity activity){
        currentActivity = activity;
        rootView = currentActivity.getWindow().getDecorView().getRootView();
    }

    static ScreenEditor getInstance(Activity activity){
        if(screenEditor==null){
            screenEditor = new ScreenEditor(activity);
        }else{
            screenEditor.currentActivity = activity;
            screenEditor.rootView = activity.getWindow().getDecorView().getRootView();
            screenEditor.changes = new ArrayList<>();
        }
        return screenEditor;
    }

    void edit(){
        JSONArray changes = getChangesFor(currentActivity);

        accumulateChanges(changes);
        prepareAndMakeEdits();
        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(globalLayoutChangedbyMe==false){
                    prepareAndMakeEdits();
                }
                else{
                    globalLayoutChangedbyMe=false;
                }

            }
        };
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    private void prepareAndMakeEdits() {
        prepareChanges();
        makeChanges();
        globalLayoutChangedbyMe = true;
        Log.i("ScreenEditor","edited");
    }

    private void makeChanges() {
        for(Change change:changes){
            change.make();
        }
        Log.i("ScreenEditor makeChng","Made changes to the screen");
    }

    private void prepareChanges() {
        for(int i=0;i<changes.size();i++){
            if(!changes.get(i).prepare()){
                Log.i("Change Error",changes.get(i).toString());
                continue;
            }
        }

        Log.i("ScreenEditor prepare","Prepared changes for editing");
    }

    private void accumulateChanges(JSONArray changesJSON) {
        for(int i=0;i<changesJSON.length();i++){
            try {
                Change change = new Change(changesJSON.getJSONObject(i),currentActivity);
                changes.add(change);
            } catch (JSONException e) {
                Log.e("ScreenEditor accumulate", e.getMessage());
            }
        }
        Log.i("ScreenEditor","Accumulated Changes");
    }

    private JSONArray getChangesFor(Activity activity){
        String activityName = activity.getClass().getCanonicalName();
        JSONArray jsonArray = null;
        JSONArray toSendJSONArray = new JSONArray();
        try {
            jsonArray = new JSONArray(readFile());
            for(int i=0;i<jsonArray.length();i++){
                String obtainedActivityName = jsonArray.getJSONObject(i).getString("activity");
                if(obtainedActivityName.equalsIgnoreCase(activityName)){
                    toSendJSONArray = (jsonArray.getJSONObject(i).getJSONArray("changes"));
                    Log.i("ScreenEditor getChanges","Changes for the Activity Found");
                }
            }
        } catch (JSONException e) {
            Log.e("ScreenEditor", e.getMessage());
        }

        return toSendJSONArray;
    }

    private String readFile(){
        StringBuilder stringBuilder = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(currentActivity.getResources().openRawResource(R.raw.view_change_json_request)));
            String line;
            stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }catch(IOException e){
            Log.i("ScreenEditor read","Reading failed" );
            Log.e("ScreenEditor read", e.getMessage());
        }
        return stringBuilder.toString();
    }

    public void purge(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
            Log.i("Remove GlobalLayoutList", "Jelly Bean or higher");
        } else {
            rootView.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
            Log.i("Remove GlobalLayoutList", "Lower than Jelly Bean");
        }
       changes = new ArrayList<>();
    }
}


