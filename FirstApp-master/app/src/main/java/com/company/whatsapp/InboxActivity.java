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

public class InboxActivity extends ListActivity {

    String username = MainActivity1.bob;
    ListView list;
   // ArrayList<HashMap<String, String>> inboxList;
    ArrayList<String> results = new ArrayList<String>();
   // private ListAdapter adapter;
    ArrayAdapter adp;
    ArrayList<HashMap<String, String>> inboxList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_list);

        list = (ListView) findViewById(android.R.id.list);
        inboxList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("contacts");
        addInbox();
        populateListView();

        adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, results);
        list.setAdapter(adp);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition =position;
//                HashMap<String,String> map1 = (HashMap<String,String>) list.getItemAtPosition(position);
//                String userName = map1.get("userName");
                String userName = (list.getItemAtPosition(position).toString());
                Log.d("Chat","Selected user="+userName);



               // String itemValue =(String) list.getItemAtPosition(position);
                Intent intent = new Intent(InboxActivity.this,ChatBox.class);
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
      //  ArrayList<String> results = new ArrayList<String>();
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
