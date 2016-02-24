package com.company.whatsapp;

import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.zemosolabs.zetarget.sdk.ZeTarget;


public class MainActivity1 extends AppCompatActivity {
    String TAG = "WhatsApp";
    String url1 = Constants.TABLET_SIDE+"checkUser";
    String url2 = Constants.TABLET_SIDE+"addUser";

    static String bob;
    TextView mText;
    EditText mEdit;

    Button Existing_User;
    Button New_User;
    Button Log_Out;
    InputStream inputStream = null;
    final static String apiKey ="1d327259-6d71-4e78-b4c2-91c93c76039c";
    final static String googleApiProjectNumber="263819815942 ";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Existing_User = (Button) findViewById(R.id.button1);
        New_User = (Button) findViewById(R.id.button2);
        Log_Out = (Button) findViewById(R.id.button3);

        mEdit = (EditText) findViewById(R.id.editText1);
        mText = (TextView) findViewById(R.id.textView1);
        ZeTarget.setZeTargetURL("http://devapi.zetarget.com/");
        ZeTarget.initializeWithContextAndKeyAndUserId(getApplicationContext(), apiKey, MainActivity1.bob, googleApiProjectNumber,null);
        mEdit.setText(loadPrefs("username"));
        JSONTask1 jsonTask = new JSONTask1();
        if(mEdit.getText().length()!=0) {
            jsonTask.execute(url1);



            Intent intent1 = new Intent(MainActivity1.this, TabandList.class);
            intent1.putExtra("username", mEdit.getText().toString());
            MainActivity1.this.startActivity(intent1);
        }

        ZeTarget.enableDebugging();

        Existing_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONTask1 jsonTask = new JSONTask1();
                jsonTask.execute(url1);
                savePrefs("username", bob);
                DBAdapter dbAdapter =DBAdapter.getDatabaseHelper(getApplicationContext());
                dbAdapter.deleteDuplicateUser();

                if(dbAdapter.checkUser(mEdit.getText().toString())) {
                    Intent intent1 = new Intent(MainActivity1.this, TabandList.class);
                    intent1.putExtra("username", mEdit.getText().toString());
                    MainActivity1.this.startActivity(intent1);
                }
            }
        });

        New_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONTask().execute(url2);
                savePrefs("username", bob);
                DBAdapter dbAdapter =DBAdapter.getDatabaseHelper(getApplicationContext());
                dbAdapter.deleteDuplicateUser();
                dbAdapter.insertUser(mEdit.getText().toString());

                Intent intent1 = new Intent(MainActivity1.this, TabandList.class);
                intent1.putExtra("username",mEdit.getText().toString());
                MainActivity1.this.startActivity(intent1);
            }
        });

        Log_Out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePrefs();
                finish();
            }
        });

    }

    public  String getUserName() {
        String user_name = mEdit.getText().toString();
        bob = user_name;
        return user_name;

    }
    private void savePrefs(String key,String value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();

    }

    private String loadPrefs(String key){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String user = sp.getString("username", "");
        return user;
    }

    private void deletePrefs(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().clear().commit();
    }



    public class JSONTask extends AsyncTask<String, String, String> {

            @Override
            protected String doInBackground(String... params) {

                try {
                    String jsonString = "";
                    JSONObject json = new JSONObject(("{\"NewAccount_Name\":\"" + getUserName() + "\" }"));
                    jsonString = json.toString();
                    HttpURLConnection connection = null;
                    try {
                        //Create connection
                        URL url1 = new URL(params[0]);
                        connection = (HttpURLConnection) url1.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json);");
                        connection.setRequestProperty("Content-Language", "en-US");
                        connection.connect();
                        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                        out.write(json.toString());
                        out.close();

                        String reply = "";
                        String jsonArray = "";
                        InputStream in = connection.getInputStream();
                        StringBuffer sb = new StringBuffer();
                        //    ArrayList<HashMap<String,String>> inboxList;
                        try {
                            int chr;
                            while ((chr = in.read()) != -1) {
                                sb.append((char) chr);
                            }
                            reply = sb.toString();
                            JSONObject jsonObject = new JSONObject(reply);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            in.close();
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();

                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
    }


    public class JSONTask1 extends AsyncTask<String, String, String> {

            @Override
            protected String doInBackground(String... params) {

                try {

                    String jsonString = "";
                    JSONObject json = new JSONObject(("{\"ExistingAccount_Name\":\"" + getUserName() + "\" }"));
                    jsonString = json.toString();

                    Log.d(TAG, "username=" + jsonString);
                    HttpURLConnection connection = null;
                    Log.d(TAG, "param=" + params[0]);
                    try {
                        //Create connection
                        URL url1 = new URL(params[0]);
                        connection = (HttpURLConnection) url1.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json);");
                        connection.setRequestProperty("Content-Language", "en-US");
                        connection.connect();
                        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                        out.write(json.toString());
                        out.close();

                        String reply = "";
                        InputStream in = connection.getInputStream();
                        StringBuffer sb = new StringBuffer();
                        try {
                            int chr;
                            while ((chr = in.read()) != -1) {
                                sb.append((char) chr);
                            }
                            reply = sb.toString();
                            Log.d(TAG, "reply=" + reply);
                        } finally {
                            in.close();
                            return reply;
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            Intent intent = new Intent(getApplicationContext(),ChatTabActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
