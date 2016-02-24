package com.company.whatsapp;

import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by sudhanshu on 30/10/15.
 */
public class TabandList extends TabActivity {

    private static final String Contact_Name = "Contact";
    private static final String Chat = "Chat_Messages";
    String url3 = Constants.TABLET_SIDE+"findAll";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new JSONTask5().execute(url3);
        TabHost tabHost = getTabHost();
        TabHost.TabSpec inboxSpec = tabHost.newTabSpec(Contact_Name);

        inboxSpec.setIndicator("Contacts");
        Intent inboxIntent = new Intent(this, InboxActivity.class);
        inboxIntent.putExtra("contacts",JSONTask5.inboxList);
        inboxSpec.setContent(inboxIntent);

        TabHost.TabSpec chatSpec = tabHost.newTabSpec(Chat);
        chatSpec.setIndicator("Chats");
        Intent chatIntent = new Intent(this, ChatTabActivity.class);
        chatIntent.putExtra("chats",JSONTask5.inboxList);
        chatSpec.setContent(chatIntent);

        tabHost.addTab(inboxSpec);
        tabHost.addTab(chatSpec);
    }

    public static class JSONTask5 extends AsyncTask<String,String,String> {

        String TAG = "WhatsApp";
       static ArrayList<HashMap<String,String>> inboxList = new ArrayList<HashMap<String, String>>();
       // static ArrayList<HashMap<String,String>> inboxList1 = new ArrayList<>();
        String user_name;
        String id;


        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url1 = new URL(params[0]);
                connection = (HttpURLConnection) url1.openConnection();
                connection.connect();

                String reply = "";
                InputStream in = connection.getInputStream();

                StringBuffer sb = new StringBuffer();
                try {
                    int chr;
                    while ((chr = in.read()) != -1) {
                        sb.append((char) chr);
                    }
                    reply = sb.toString();
                    Log.d(TAG, "reply2=" + reply);
                    JSONArray inbox = new JSONArray(reply);
                    try
                    {
                        for (int i = 0; i < inbox.length(); i++) {
                            JSONObject c = inbox.getJSONObject(i);
                            id = c.getString("id");
                            user_name = c.getString("userName");
                            Log.d("id=" + id, "user_name=" + user_name);
                            HashMap<String, String> map = new HashMap<String, String>();
                                map.put("id", id);
                                map.put("userName", user_name);
                                inboxList.add(map);

                        }

                        Set<HashMap<String,String>> hs = new HashSet<>();
                        hs.addAll(inboxList);
                        inboxList.clear();
                        inboxList.addAll(hs);
                    }

                    finally {
                        if (connection != null)
                            connection.disconnect();
                    }
                    try {
                        if (reader != null)
                            reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
                return null;
        }

    }
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







