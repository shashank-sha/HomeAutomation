package com.company.whatsapp;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    String username = MainActivity1.bob;
    ListView list;
    ArrayAdapter adp;
    ArrayList<HashMap<String, String>> inboxList = new ArrayList<HashMap<String, String>>();
    ArrayList<String> results = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_list);
        inboxList =  (ArrayList<HashMap<String, String>>)getIntent().getSerializableExtra("chats");
        addInbox();
        populateListView();
        list = (ListView)findViewById(android.R.id.list);
        adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, results);
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int itemPosition =position;
                String userName = (list.getItemAtPosition(position).toString());
                Log.d("Chat","Selected user="+userName);
                Intent intent = new Intent(ChatTabActivity.this,ChatHistory.class);
                intent.putExtra("recievername",userName);
                startActivity(intent);
            }
        });
    }

    public void addInbox() {
        DBAdapter dbAdapter = DBAdapter.getDatabaseHelper(getApplicationContext());
        for (int i = 0; i < inboxList.size(); i++) {
            String username = inboxList.get(i).get("userName");
            dbAdapter.insertUser(username);
            dbAdapter.deleteDuplicateUser();
        }
    }

    private void populateListView(){
        DBAdapter dbAdapter =DBAdapter.getDatabaseHelper(getApplicationContext());
        Cursor cursor = dbAdapter.getUsers();
        if(cursor.moveToFirst()) {


            do {
                String name = cursor.getString(cursor.getColumnIndex(DBAdapter.USER_NAME));
                results.add(name);

            } while (cursor.moveToNext());
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
