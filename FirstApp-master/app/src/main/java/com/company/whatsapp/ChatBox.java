package com.company.whatsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ChatBox extends Activity {

    String url4 = Constants.TABLET_SIDE + "addMessage";
    String url5 = Constants.TABLET_SIDE + "findMessage";
    static boolean flag = false;
    static String TAG = "WhatsApp";
    private ArrayList<Message> messages = new ArrayList<>();
    private CharArrayAdapter adp;


    private ListView list;
    private EditText chatText;
    private Button send;
    private Button upload;
    private ImageView img;
    private ImageView img1;
    private Button uploadphoto;
    private Button downloadphoto;
    static public boolean isMe = false;
    String username = MainActivity1.bob;
    static Bitmap image;
    static Bitmap decodedByte;

    public class JSONTASK4 extends AsyncTask<String, String, String> {

        public String getRecieverName() {
            Intent intent1 = getIntent();
            String reciverName = intent1.getStringExtra("recievername");
            return reciverName;
        }

        @Override
        protected String doInBackground(String... params) {
            String username = MainActivity1.bob;
            try {

                String jsonString = "";
                HttpURLConnection connection = null;
                Log.d(TAG, "param=" + params[0]);

                try {
                    URL url1 = new URL(params[0] + "/" + username);
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
                        JSONArray chats = new JSONArray(reply);
                        try {


                            for (int i = 0; i < chats.length(); i++) {
                                JSONObject c = chats.getJSONObject(i);
                                Message message123 = new Message();
                                String messageTo = c.getString("messageTo");
                                String messageFrom = c.getString("messageFrom");
                                String message = c.getString("message");
                                String date = c.getString("datetime");
                                String url = c.getString("imageUrl");

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                Date convertedDate = new Date();
                                try {
                                    convertedDate = dateFormat.parse(date);
                                } catch (ParseException e) {

                                    e.printStackTrace();
                                }

                                String reciever = getRecieverName();
                                String user = MainActivity1.bob;

                                if (((messageTo.equals(reciever)) || (messageTo.equals(user))) && ((messageFrom.equals(reciever)) || (messageFrom.equals(user)))) {
                                    message123.setToName(messageTo);
                                    message123.setMessage(message);
                                    message123.setFromName(messageFrom);
                                    message123.setDateTime(convertedDate);
                                    message123.setUrl(url);
                                    messages.add(message123);

                                    Log.d(TAG, "message initialized");
                                } else {
                                    Log.d(TAG, "message not initialized");
                                }

                                Set<Message> hs = new HashSet<>();
                                hs.addAll(messages);
                                messages.clear();
                                messages.addAll(hs);
                                Collections.sort(messages);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //ImageView patch = (ImageView) findViewById(R.id.test_image);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_box);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

        new JSONTASK4().execute(url5);
        addMessage();


        send = (Button) findViewById(R.id.btn);
        upload = (Button) findViewById(R.id.upload);
        downloadphoto = (Button) findViewById(R.id.downloadImage);
        //send.setText(getResources().getText(R.string.a_send));
        list = (ListView) findViewById(R.id.listView1);
        adp = new CharArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, messages);
        chatText = (EditText) findViewById(R.id.chat);

        addMessage();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMe = true;
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Calendar calobj = Calendar.getInstance();
//            // System.out.println(df.format(calobj.getTime()));
                CharSequence text = df.format(calobj.getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                Date convertedDate = new Date();
               if(img!=null) {
                   img.setVisibility(View.GONE);
               }

                try {
                    convertedDate = dateFormat.parse(text.toString());
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (flag)
                    sendChatMessage(convertedDate, Constants.TABLET_SIDE + "assets/images/" + MainActivity1.bob + ".jpg");
                if (!flag)
                    sendChatMessage(convertedDate, null);
                new JSONTask3().execute(url4);

            }


        });
        adp.notifyDataSetChanged();
        list.setAdapter(null);
        list.setAdapter(adp);//                chatText.setText("");


        adp.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                list.setSelection(adp.getCount() - 1);


            }
        });

//            }          list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
                img = (ImageView) findViewById(R.id.img);
                img1 = (ImageView) findViewById(R.id.img1);
                // image = ((BitmapDrawable) img.getDrawable()).getBitmap();
                // img.getVisibility();
                flag = true;
            }
        });

        downloadphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        img1 = (ImageView)findViewById(R.id.img1);
//                        new DownLoadImage().execute("http://192.168.1.98:9000/assets/images/" + MainActivity1.bob +".jpg");
                image = ((BitmapDrawable) img.getDrawable()).getBitmap();
                Intent myIntent = new Intent(ChatBox.this, Drawing.class);
                startActivity(myIntent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            img.setImageURI(selectedImage);
            try {
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                image = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                        selectedImage), null, bmpFactoryOptions);

                bmpFactoryOptions.inJustDecodeBounds = false;
                image = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                        selectedImage), null, bmpFactoryOptions);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public String getMessage() {
        String message = chatText.getText().toString();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatText.setText("");
            }
        });
        return message;

    }

    public void addMessage() {
        //  addChatToSqlite(messages);
        addServerChatToSqlite(messages);
        DBAdapter dbAdapter = DBAdapter.getDatabaseHelper(getApplicationContext());
        if (dbAdapter.ifEmpty()) {
            Log.d(TAG, "Message  not added to SQLite DB");

        }
        Log.d(TAG, "Message added to SQLite DB");
    }


    public String getRecieverName() {
        Intent intent1 = getIntent();
        String reciverName = intent1.getStringExtra("recievername");
        return reciverName;
    }

    public void addServerChatToSqlite(ArrayList<Message> List) {
        DBAdapter dbAdapter = DBAdapter.getDatabaseHelper(getApplicationContext());
        Iterator<Message> iterator = List.iterator();

        while (iterator.hasNext()) {
            Message element = iterator.next();
            String messageFrom = element.getFromName();
            String message = element.getMessage();
            String messageTo = element.getToName();
            Date date = element.getDateTime();
            dbAdapter.insertRow(messageFrom, message, messageTo);
            dbAdapter.deleteDuplicates();


        }
    }

    private boolean sendChatMessage(Date date, String url) {

        Message message = new Message((MainActivity1.bob), chatText.getText().toString(), getRecieverName(), date, url);
        if (chatText.getText().toString() != null) {
            adp.add(message);
            addMessage();

            return true;
        }  //.add(new ChatMessage(getRecieverName(), chatText.getText().toString()));
        return false;
    }


    public class JSONTask3 extends AsyncTask<String, String, String> {

        Bitmap image = ChatBox.image;
        String name = MainActivity1.bob;
        String reciever = getRecieverName();
        String encodedImage = null;

        @Override
        protected String doInBackground(String... params) {

            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                Calendar calobj = Calendar.getInstance();
                // System.out.println(df.format(calobj.getTime()));
                CharSequence text = df.format(calobj.getTime());
                Log.i(TAG, "doInBack called on UploadImageAsync");
                if (flag) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                    Log.d(TAG, "ImageCode");
                } else {
                    encodedImage = "";
                }
                String jsonString = "";

                JSONObject json = new JSONObject(("{\"From\":\"" + username + "\"" + " ," + "\"To\":" + "\"" + getRecieverName() + "\""
                        + " ," + " \"Message\":\"" + getMessage() + "\"" + " ," + " \"Date\":\"" + text + "\"" + " ,"
                        + " \"ImageCode\":\"" + encodedImage + "\" }"));
                jsonString = json.toString();
                Log.d(TAG, "jsonString=" + jsonString);

                //Log.d(TAG, "username=" + jsonString);
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
                    // wr.writeBytes (urlParameters);
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
