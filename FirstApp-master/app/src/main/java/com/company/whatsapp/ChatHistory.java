
package com.company.whatsapp;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ChatHistory extends ListActivity {
    static String reciever_name;
    String username = MainActivity1.bob;
     ArrayList<SQLite_Messages> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
        Intent intent = getIntent();
         reciever_name = intent.getStringExtra("recievername");
        try {
            populateListView();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //   final ListView lv1 = (ListView) findViewById(R.id.ListView01);

    }


    private void populateListView() throws ParseException {
        DBAdapter dbAdapter =DBAdapter.getDatabaseHelper(getApplicationContext());
        ArrayList<SQLite_Messages> results = new ArrayList<SQLite_Messages>();
        Cursor cursor = dbAdapter.getMessages(username,reciever_name);
       if(cursor.moveToFirst()) {


           do {
               String message_from = cursor.getString(cursor.getColumnIndex(DBAdapter.MESSAGE_FROM));
               String message = cursor.getString(cursor.getColumnIndex(DBAdapter.MESSAGE));
               String message_to = cursor.getString(cursor.getColumnIndex(DBAdapter.MESSAGE_TO));
               String time = cursor.getString(cursor.getColumnIndex(DBAdapter.DATE));
               DateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH);
               Date date = format.parse(time);
               SQLite_Messages message123 = new SQLite_Messages();
               message123.setFrom_Name(message_from);
               message123.setMessage(message);
               message123.setTo_Name(message_to);
               message123.setDateTime(date);
               results.add(message123);

           }while(cursor.moveToNext());





           final ListView lv1 = (ListView) findViewById(R.id.ListView01);
         //  String[] name = {"name","city","state"};
         //  int[] id = {R.id.name,R.id.cityState,R};
//          adapter = new SimpleAdapter(ChatHistory.this, results,
////                   R.layout.custom_row_view,String[]{"name","city","state"},);
//           ArrayAdapter<SQLite_Messages> adapter = new ArrayAdapter<SQLite_Messages>(this,
//                   android.R.layout.simple_list_item_1, results);
         //  list.setTextFilterEnabled(true);
           //list.setAdapter(adapter);
           lv1.setAdapter(new MyCustomBaseAdapter(ChatHistory.this,results));



        // lv1.setAdapter(adapter);
       }

//        String[] fieldNames = new String[]{DBAdapter.MESSAGE_FROM,DBAdapter.MESSAGE,DBAdapter.MESSAGE_TO};
//        int[] viewIDs = new int[]{R.id.message_from,R.id.message,R.id.message_to};
//        SimpleCursorAdapter myCursorAdapter;
//        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.chathistoryitems,cursor,fieldNames,viewIDs);
//        SQLAdapter<r> adapter = new ArrayAdapter<SQLite_Messages>(this,
//                android.R.layout.simple_list_item_1, results);
//        ListView list =(ListView)findViewById(android.R.id.list);
//        list.setAdapter(adapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_history, menu);
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
