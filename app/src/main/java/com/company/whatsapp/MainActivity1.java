package com.company.whatsapp;

import android.content.Entity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
//    String url1 = "http://192.168.2.15:9000/checkUser";
//    String url2 = "http://192.168.2.15:9000/addUser";
//    String url3 = "http://192.168.2.15:9000/findAll";

    String url1 = "http://10.0.2.2:9000/checkUser";
    String url2 = "http://10.0.2.2:9000/addUser";
    String url3 = "http://10.0.2.2:9000/findAll";



//    String url1 = "http://192.168.1.55:9000/checkUser";
//  String url2 = "http://192.168.1.55:9000/addUser";
//    String url3 = "http://192.168.1.55:9000/findAll";

    //    String url = "http://polls.apiblueprint.org/questions";
    //  String result ="";

    static String bob;
    TextView mText;
    EditText mEdit;

    Button Button1;
    Button Button2;
    InputStream inputStream = null;
    final static String apiKey ="1d327259-6d71-4e78-b4c2-91c93c76039c";
    final static String googleApiProjectNumber="263819815942 ";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button1 = (Button) findViewById(R.id.button1);
        Button2 = (Button) findViewById(R.id.button2);

        mEdit = (EditText) findViewById(R.id.editText1);
        mText = (TextView) findViewById(R.id.textView1);
        ZeTarget.setZeTargetURL("http://devapi.zetarget.com/");
        ZeTarget.initializeWithContextAndKey(getApplicationContext(), apiKey,
                googleApiProjectNumber);


        ZeTarget.enableDebugging();





        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

       //         DBAdapter dbHelper = DBAdapter.getDatabaseHelper(getApplicationContext());
//               String message = dbHelper.existingSelected(mEdit.getText().toString());
//                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                JSONTask1 jsonTask = new JSONTask1();
                jsonTask.execute(url1);
                Intent intent1 = new Intent(MainActivity1.this, TabandList.class);
                intent1.putExtra("username",mEdit.getText().toString());
                MainActivity1.this.startActivity(intent1);

                if (jsonTask.getStatus() == AsyncTask.Status.FINISHED) {
                    String reply = jsonTask.getreply(url1);
//
//
                    Toast.makeText(getApplicationContext(), reply, Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //     DBAdapter dbHelper = DBAdapter.getDatabaseHelper(getApplicationContext());
                //       String message2 =  dbHelper.newSelected(mEdit.getText().toString());
                new JSONTask().execute(url2);
                Intent intent1 = new Intent(MainActivity1.this, TabandList.class);
                intent1.putExtra("username",mEdit.getText().toString());
                MainActivity1.this.startActivity(intent1);

                // Toast.makeText(getApplicationContext(),; ,Toast.LENGTH_SHORT).show();


            }
        });

    }

    public  String getUserName() {
        String user_name = mEdit.getText().toString();
        bob = user_name;
        return user_name;

    }




    public class JSONTask extends AsyncTask<String, String, String> {

            @Override
            protected String doInBackground(String... params) {

                try {

                    String jsonString = "";
                    JSONObject json = new JSONObject(("{\"NewAccount_Name\":\"" + getUserName() + "\" }"));
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
//                        connection.setRequestProperty("Content-Length", "" +
//                                Integer.toString(urlParameters.getBytes().length));
                        connection.setRequestProperty("Content-Language", "en-US");

//                    connection.setUseCaches(false);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
//                    connection.setConnectTimeout(10000);
//                    connection.setReadTimeout(10000);
                        connection.connect();
                        //    Log.d(TAG, "before send ");
                        //Send request
                        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                        // wr.writeBytes (urlParameters);
                        out.write(json.toString());
                        //  Log.d(TAG,"out=" + out.toString());


                        //  Log.d(TAG, "after send");
                        //Get Response
                        //InputStream is = connection.getInputStream();
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


//                        try {
//                            JSONArray inbox = jsonObject.getJSONArray("");
//                            for (int i = 0; i < inbox.length(); i++) {
//                                JSONObject c = inbox.getJSONObject(i);
//                                String id = c.getString("id");
//                                String user_name = c.getString("userName");
//
//                                HashMap<String, String> map = new HashMap<String, String>();
//                                map.put("id", id);
//                                map.put("userName", user_name);
//                                inboxList.add(map);
//                            }


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
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (ProtocolException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }




            public String getreply(String url) {
                String reply = doInBackground(url);
                return reply;
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
//                        connection.setRequestProperty("Content-Length", "" +
//                                Integer.toString(urlParameters.getBytes().length));
                        connection.setRequestProperty("Content-Language", "en-US");

//                    connection.setUseCaches(false);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
//                    connection.setConnectTimeout(10000);
//                    connection.setReadTimeout(10000);
                        connection.connect();
                        //    Log.d(TAG, "before send ");
                        //Send request
                        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                        // wr.writeBytes (urlParameters);
                        out.write(json.toString());
                        //  Log.d(TAG,"out=" + out.toString());


                        //  Log.d(TAG, "after send");
                        //Get Response
                        //InputStream is = connection.getInputStream();
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

            public String getreply(String url) {
                String reply = doInBackground(url);
                return reply;
            }

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
