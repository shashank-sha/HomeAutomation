package com.shashank.homeautomation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

public class ParticularRoomActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_room);

        final int room_number = getIntent().getExtras().getInt("room_number");
        Toast.makeText(getApplicationContext(), room_number + "", Toast.LENGTH_SHORT).show();

        sharedPreferences = getSharedPreferences("shaPreferences", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final int appliances = sharedPreferences.getInt("room" + room_number + "_num", 4);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayoutVerticalRoom);

        String room_name = sharedPreferences.getString("room" + room_number + "_name", "roomname");
        TextView room = (TextView) findViewById(R.id.room_textView);
        room.setText(room_name.toUpperCase());
        room.setTypeface(null, Typeface.BOLD);
        room.setGravity(Gravity.CENTER);
        //linearLayout.addView(room);

        room.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40.f);
        room.setTextColor(Color.parseColor("#FFFFFF"));
        room.setShadowLayer(5, 3, 3, Color.BLACK);

        //LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //llp.setMargins(0, 20, 0, 0);
        //textView.setLayoutParams(llp);


        Set<String> s = sharedPreferences.getStringSet("room" + room_number + "_appliances", null);
        Iterator<String> itr = s.iterator();
        while (itr.hasNext()) {
            final String appliance_name = itr.next();

            //String appliance_name = sharedPreferences.getString("room" + (i + 1) + "_name", "roomname");
            TextView textView = new TextView(this);
            textView.setText(appliance_name);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 20, 0, 0);
            textView.setLayoutParams(llp);

            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25.f);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            textView.setShadowLayer(5, 3, 3, Color.BLACK);


            textView.setClickable(true);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int val = sharedPreferences.getInt("room" + room_number + "_" + appliance_name, 0);
                    Toast.makeText(getApplicationContext(), val + "", Toast.LENGTH_SHORT).show();
                    new GETStatus().execute("http://192.168.2.68:8088");
                }
            });


            LinearLayout linearLayoutHorizontal = new LinearLayout(this);
            linearLayoutHorizontal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);

            linearLayoutHorizontal.addView(textView);


            linearLayout.addView(linearLayoutHorizontal);
            View v = new View(this);
            v.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    2
            ));
            v.setBackgroundColor(Color.parseColor("#FFFFFF"));

            setMargins(v, 0, 15, 0, 0);

            linearLayout.addView(v);

        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ParticularRoom Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.shashank.homeautomation/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ParticularRoom Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.shashank.homeautomation/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class GETStatus extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            try {
                Log.d("hhhhhhhhhhhhh", "hhhhhhhhh");

                URL url = new URL(params[0]);
                Log.d("hhhhhhhhhhhhh","uuuuuuuuuuuuuuuuuu");
                httpURLConnection = (HttpURLConnection) url.openConnection();           //if the connection is not established an IOException will occur

                httpURLConnection.setConnectTimeout(5000);                              //sets the timeout in milliseconds for connection
                //Log.d("hhhhhhhhhhhhh",httpURLConnection.getResponseCode()+"");
                Log.d("hhhhhhhhhhhhh","iiiiiiiiiiiii");
                httpURLConnection.connect();
                Log.d("hhhhhhhhhhhhh", "aaaaaaaaaaaaaaaa");

                InputStream stream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                    Log.d("bbbbbbbbbbbbbb", buffer.toString());

                }

                return buffer.toString();
            } catch (MalformedURLException e) {
                Log.d("hhhhhhhhhhhhh", "malformed");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("hhhhhhhhhhhhh", "ioexception");
                Intent intent=new Intent(ParticularRoomActivity.this,BeforeMain_1.class);
                startActivity(intent);
                e.printStackTrace();
            } finally {
                Log.d("hhhhhhhhhhhhh", "sssssssss");

                if (httpURLConnection != null) {
                    Log.d("hhhhhhhhhhhhh", "nnnnnnnnnnnnn");

                    httpURLConnection.disconnect();
                }
                try {
                    if (bufferedReader != null) {
                        Log.d("hhhhhhhhhhhhh", "bbbbbbbbbbb");

                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //tv=(TextView)findViewById(R.id.textView);
            if(result==null);
            else
                Log.d("hhhhhhhhhhhhh", result);


        }
    }


}


