package com.company.whatsapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatTabActivity extends ListActivity {
//    String url3 = "http://192.168.1.55:9000/findAll";
//    String url3 = "http://192.168.2.15:9000/findAll";
String url3 = "http://10.0.2.2:9000/findAll";
    String username = MainActivity1.bob;
    ListView list;

    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_list);
        ArrayList<HashMap<String, String>> inboxList =  (ArrayList<HashMap<String, String>>)getIntent().getSerializableExtra("chats");




        list = (ListView)findViewById(android.R.id.list);

//        new JSONTask5().execute(url3);
        String[] name = {"userName"};
        int[] id = {R.id.user_name};
        adapter = new SimpleAdapter(ChatTabActivity.this, inboxList,
                R.layout.inbox_list_item,name,id);
        list.setTextFilterEnabled(true);
        list.setAdapter(adapter);





        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int itemPosition =position;
                HashMap<String,String> map1 = (HashMap<String,String>) list.getItemAtPosition(position);
                String userName = map1.get("userName");


                // String itemValue =(String) list.getItemAtPosition(position);
                Intent intent = new Intent(ChatTabActivity.this,ChatHistory.class);
                intent.putExtra("recievername",userName);

                startActivity(intent);
            }
        });
    }



//    public class JSONTask5 extends AsyncTask<String,String,String> {
//        String TAG = "WhatsApp";
//
//        String user_name;
//        String id;
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            HttpURLConnection connection = null;
//            BufferedReader reader = null;
//
//            try {
//                URL url1 = new URL(params[0]);
//                connection = (HttpURLConnection) url1.openConnection();
//                // connection.setRequestMethod("GET");
////                connection.setRequestProperty("Content-Type", "application/json);");
//////                        connection.setRequestProperty("Content-Length", "" +
//////                                Integer.toString(urlParameters.getBytes().length));
////                connection.setRequestProperty("Content-Language", "en-US");
////
//                connection.connect();
//
//                String reply = "";
//                InputStream in = connection.getInputStream();
//
//                StringBuffer sb = new StringBuffer();
//                try {
//                    int chr;
//                    while ((chr = in.read()) != -1) {
//                        sb.append((char) chr);
//                    }
//                    reply = sb.toString();
//                    Log.d(TAG, "reply2=" + reply);
//                    // JSONObject jsonObject = new JSONObject(reply);
//                    JSONArray inbox = new JSONArray(reply);
//
//
//                    try {
//
//                        // JSONArray inbox = jsonObject.getJSONArray("");
//
//                        for (int i = 0; i < inbox.length(); i++) {
//                            JSONObject c = inbox.getJSONObject(i);
//                            id = c.getString("id");
//                            user_name = c.getString("userName");
//                            Log.d("id=" + id, "user_name=" + user_name);
//
//                            HashMap<String, String> map = new HashMap<String, String>();
//                            map.put("id", id);
//                            map.put("userName", user_name);
//                            inboxList.add(map);
//                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                synchronized (adapter) {
//                                    adapter.notify();
//                                }
//
//                            }
//
//                            //     int count = list.getAdapter().getCount();
//                            //          Log.d(TAG,"count=" + Integer.toString(count));
//
//
//                        });
//
//
//
//                    }finally {
//                        if (connection != null)
//                            connection.disconnect();
//                    }
//                    try {
//                        if (reader != null)
//                            reader.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//
//
//
//
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inbox, menu);
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
