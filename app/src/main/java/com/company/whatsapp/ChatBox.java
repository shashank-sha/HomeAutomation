package com.company.whatsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import android.content.DialogInterface;

import com.company.whatsapp.CharArrayAdapter;
import com.company.whatsapp.ChatMessage;
import com.company.whatsapp.R;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ChatBox extends AppCompatActivity {
//    String url4 ="http://192.168.1.55:9000/addMessage";
//    String url5 ="http://192.168.1.55:9000/findMessage";

//    String url4 ="http://192.168.1.55:9000/addMessage";
//    String url5 ="http://192.168.1.55:9000/findMessage";

    String url4 ="http://10.0.2.2:9000/addMessage";
    String url5 ="http://10.0.2.2:9000/findMessage";

 //   private MessageListAdapter mdp;
    static String TAG = "WhatsApp";

    private ArrayList<Message> messages=new ArrayList<>();
    private CharArrayAdapter adp;
   // ArrayList<ChatMessage> chat = new ArrayList<ChatMessage>();

    private ListView list;
   // private ListView serverList;
    private EditText chatText;
    private Button send;
   static public boolean isMe =false;
    String username = MainActivity1.bob;




    public class JSONTASK4 extends AsyncTask<String,String,String> {

        public  String getRecieverName(){
            Intent intent1 = getIntent();
            String reciverName = intent1.getStringExtra("recievername");
            return reciverName;
        }
        @Override
        protected String doInBackground(String... params) {
            String username = MainActivity1.bob;
            try {

                String jsonString = "";
//                JSONObject json = new JSONObject(("{\"To\":\"" + username + "\" }"));
//                jsonString = json.toString();
//                Log.d(TAG,"jsonString=" + jsonString);
                HttpURLConnection connection = null;
                Log.d(TAG, "param=" + params[0]);

                try {
                    //Create connection


                    URL url1 = new URL(params[0] + "/" + username);
                    connection = (HttpURLConnection) url1.openConnection();

//
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
                        JSONArray chats = new JSONArray(reply);
                        try {


                            for (int i = 0; i < chats.length(); i++) {
                                JSONObject c = chats.getJSONObject(i);
                                Message message123 = new Message();
                                String messageTo = c.getString("messageTo");
                                String messageFrom = c.getString("messageFrom");
                                String message = c.getString("message");
                                String reciever = getRecieverName();
                                String user = MainActivity1.bob;

                                if(((messageTo.equals(reciever)) || (messageTo.equals(user))) && ((messageFrom.equals(reciever)) || (messageFrom.equals(user)))) {
                                    message123.setToName(messageTo);
                                    message123.setMessage(message);
                                    message123.setFromName(messageFrom);
                                    messages.add(message123);
//                                    int count = messages.size();
//                                    String user1 = reciever;
                                    Log.d(TAG,"message initialized");
                                }

                                else{
                                    Log.d(TAG,"message not initialized");
                                }






                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

    }




            @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_chat_box);
                //ImageView patch = (ImageView) findViewById(R.id.test_image);


               new JSONTASK4().execute(url5);


                send = (Button) findViewById(R.id.btn);
                //send.setText(getResources().getText(R.string.a_send));
                list = (ListView) findViewById(R.id.listView1);

                // chat.add(new ChatMessage("SUD", "abc"));

                adp = new CharArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, messages);

                //serverList = (ListView) findViewById(R.id.listView2);
                //     ArrayAdapter<ChatMessage> adapter1 = new ArrayAdapter<ChatMessage>(this, android.R.layout.simple_list_item_1, chat);

//                ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this,
//                        android.R.layout.simple_list_item_1, messages);
//                serverList.setAdapter(adapter);

                chatText = (EditText) findViewById(R.id.chat);

                chatText.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER) {
                            return sendChatMessage();

                        }


                        return false;
                    }
                });

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isMe = true;
                        sendChatMessage();
                        new JSONTask3().execute(url4);
                      //  new JSONTASK4().execute(url5);
// chatText.setText("");
                    }


                });
                list.setAdapter(null);
                list.setAdapter(adp);
//                chatText.setText("");


                adp.registerDataSetObserver(new DataSetObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        list.setSelection(adp.getCount() - 1);


                    }
                });

            }          //list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);



     public String getMessage(){
        String message = chatText.getText().toString();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatText.setText("");
            }
        });
         return message;

     }

    public void addMessage(){
      //  addChatToSqlite(messages);
        addServerChatToSqlite(messages);
        DBAdapter dbAdapter = DBAdapter.getDatabaseHelper(getApplicationContext());
        if(dbAdapter.ifEmpty()){
            Log.d(TAG,"Message  not added to SQLite DB");

        }
            Log.d(TAG,"Message added to SQLite DB");
    }



    public  String getRecieverName(){
        Intent intent1 = getIntent();
        String reciverName = intent1.getStringExtra("recievername");
        return reciverName;
    }



//   public void addChatToSqlite(ArrayList<Message> List){
//       DBAdapter dbAdapter = DBAdapter.getDatabaseHelper(getApplicationContext());
//       Iterator<Message> iterator = List.iterator();
//       while(iterator.hasNext()){
//           Message element =iterator.next();
//           String messageTo = element.getToName();
//           String message = element.getMessage();
//          long result = dbAdapter.insertRow(username, message, messageTo);
//           if(result==-1){
//               Log.d(TAG,"Message cannot be added");
//           }
//
//
//
//
//       }
//
//   }
    public void addServerChatToSqlite(ArrayList<Message> List){
        DBAdapter dbAdapter = DBAdapter.getDatabaseHelper(getApplicationContext());
        Iterator<Message> iterator = List.iterator();

        while(iterator.hasNext()){
            Message element =iterator.next();
            String messageFrom = element.getFromName();
            String message = element.getMessage();
            dbAdapter.insertRow(messageFrom, message, username);



        }
    }






    private boolean sendChatMessage() {

//

        adp.add(new Message(MainActivity1.bob, chatText.getText().toString(), getRecieverName()));
       // if(getRecieverName()==MainActivity1.bob)






       // chatText.setText("");
       // List<ChatMessage> chat = adp.getMessageList();

       addMessage();

        return true;
//        //.add(new ChatMessage(getRecieverName(), chatText.getText().toString()));
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                synchronized (adp) {
//                    adp.notify();
//                }
//
//            }
//
//            //     int count = list.getAdapter().getCount();
//            //          Log.d(TAG,"count=" + Integer.toString(count));
//
//
//        });
//
//        chatText.setText("");
//        // side=!side;
//
//        return true;

    }



    public class JSONTask3 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                String jsonString = "";
                JSONObject json = new JSONObject(("{\"From\":\"" + username +"\"" +" ,"+ "\"To\":" + getRecieverName() +" ," +" \"Message\":\"" + getMessage() + "\" }"));
                jsonString = json.toString();
                Log.d(TAG,"jsonString=" + jsonString);

                //Log.d(TAG, "username=" + jsonString);
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



    // @Override
//    protected void attachBaseContext(Context ctx) {
//        ZeTarget.doXYZ(this,ctx);
//        Log.d("jbjf","kksg");
//        super.attachBaseContext(ZeTarget.doPQR(this,ctx));
//    }









}
