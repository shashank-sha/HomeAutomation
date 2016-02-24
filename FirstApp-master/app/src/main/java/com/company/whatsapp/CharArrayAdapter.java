package com.company.whatsapp;

/**
 * Created by sudhanshu on 3/11/15.
 */

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;




import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.view.Gravity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
//import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by sudhanshu on 30/9/15.
 */



public class CharArrayAdapter extends ArrayAdapter<Message> {
    //private TextView charText;M
    private List<Message> messageList = new ArrayList<Message>();
    private LinearLayout layout;
   // private boolean isMe ;
    static public Bitmap decodedByte;
    private ImageView imageView;
    OutputStream outStream;


    public CharArrayAdapter(Context applicationContext, int textViewResourceId, List<Message> objects) {
        super(applicationContext, textViewResourceId, objects);
    }

    public void add(Message objects) {

        messageList.add(objects);
        super.add(objects);
        //View vv=getView(0,null,);
        //vv.setText(objects.toString());
    }
    public List getMessageList(){
        return messageList;
    }

    public void getDate(){
        DBAdapter dbAdapter = DBAdapter.getDatabaseHelper(getContext());
    }

    public class getImageinChat extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            String username = MainActivity1.bob;
            HttpURLConnection connection = null;
            try {
                URL url1 = new URL(params[0]);
                connection = (HttpURLConnection) url1.openConnection();
                connection.connect();
                String reply = "";
                InputStream in = connection.getInputStream();
                CharArrayAdapter.decodedByte = BitmapFactory.decodeStream(in);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if(Drawing.alteredBitmap!=null)
            imageView.setImageBitmap(Drawing.alteredBitmap);
                else
                imageView.setImageBitmap(decodedByte);

            ChatBox.flag =false;
        }

        }

    private String getDaysAgo(Date date){
        long days = (new Date().getTime() - date.getTime()) / 86400000;

        if(days == 0) return "Today";
        else if(days == 1) return "Yesterday";
        else return days + " days ago";
    }



    public View getView(int position, View convertView, ViewGroup parent) {


       // ChatMessage message = messageList.get(position);
        View v = convertView;

        if (v == null) {

            LayoutInflater inflator = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflator.inflate(R.layout.chat, parent, false);
        }


        layout = (LinearLayout) v.findViewById(R.id.message1);
        Message messageobj = getItem(position);

        TextView charText = (TextView) v.findViewById(R.id.SingleMessage);
        charText.setText(messageobj.toString());
        String text = charText.getText().toString().trim();
        if(text.length() == 0 || text.equals("") || text == null)
        {
            charText.setVisibility(View.GONE);

        }
        else
        {
            charText.setVisibility(View.VISIBLE);
        }
        imageView = (ImageView)v.findViewById(R.id.imginchat);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(extStorageDirectory, "/whatsapp");
               myDir.mkdirs();
                Random generator = new Random();
                int n =1000;
                n = generator.nextInt(n);
                String fname = "Image-"+n+".jpg";
                File file = new File(myDir,fname);
                try{
                    outStream = new FileOutputStream(file);
                    decodedByte.compress(Bitmap.CompressFormat.PNG,100,outStream);
                    outStream.flush();
                    outStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(),"image saved",Toast.LENGTH_LONG).show();

                return true;
            }
        });
       // DBAdapter dbAdapter =DBAdapter.getDatabaseHelper(getContext());
       if(messageobj.getUrl()!=null)
        new getImageinChat().execute(messageobj.getUrl());



        TextView date = (TextView) v.findViewById(R.id.Date);
        date.setText(getDaysAgo(messageobj.getDateTime()) + " " + new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(messageobj.getDateTime()));
        String user = messageobj.getFromName();
       String user2 = messageobj.getToName();
        String user1 =MainActivity1.bob;
       Date date1 = messageobj.getDateTime();

//        if(ChatBox.isMe) {
//            charText.setGravity(Gravity.RIGHT);
//        ChatBox.isMe=false;
//        }

        if(user.equals(user1)) {


           // charText.setGravity(Gravity.RIGHT);
            charText.setBackgroundResource(R.drawable.turqfocus);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
            params.gravity = Gravity.END;
            charText.setLayoutParams(params);
            date.setLayoutParams(params);
            imageView.setLayoutParams(params);
        }
            else {


            //charText.setGravity(Gravity.START);
            charText.setBackgroundResource(R.drawable.turq);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);

            params.gravity = Gravity.START;
            charText.setLayoutParams(params);
            imageView.setLayoutParams(params);

        }

        return v;
    }
}




