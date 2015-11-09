package com.company.whatsapp;

/**
 * Created by sudhanshu on 3/11/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

/**
 * Created by sudhanshu on 30/9/15.
 */



public class CharArrayAdapter extends ArrayAdapter<ChatMessage> {
    //private TextView charText;M
    private List<ChatMessage> messageList = new ArrayList<ChatMessage>();
    private LinearLayout layout;
    private boolean isMe = true;

    public CharArrayAdapter(Context applicationContext, int textViewResourceId, List<ChatMessage> objects) {
        super(applicationContext, textViewResourceId, objects);
    }

    public void add(ChatMessage objects) {

        messageList.add(objects);
        super.add(objects);
        //View vv=getView(0,null,);
        //vv.setText(objects.toString());
    }
    public List getMessageList(){
        return messageList;
    }



    public View getView(int position, View convertView, ViewGroup parent) {


       // ChatMessage message = messageList.get(position);
        View v = convertView;

        if (v == null) {

            LayoutInflater inflator = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflator.inflate(R.layout.chat, parent, false);
            DateFormat df = new SimpleDateFormat("HH:mm");
            Calendar calobj = Calendar.getInstance();
            // System.out.println(df.format(calobj.getTime()));
            CharSequence text = df.format(calobj.getTime());
            Context context =getContext();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context,text,duration);
            toast.show();



        }
        isMe = !isMe;

        layout = (LinearLayout) v.findViewById(R.id.message1);
        ChatMessage messageobj = getItem(position);
        TextView charText = (TextView) v.findViewById(R.id.SingleMessage);
        charText.setText(messageobj.toString());
//        if (isMe) {
//            charText.setGravity(Gravity.RIGHT);
//            charText.setBackgroundColor(Color.WHITE);
//        } else {
//            charText.setGravity(Gravity.LEFT);
//            // charText.setTextColor(Color.WHITE);
//            //charText.setBackgroundColor(Color.BLACK);
//        }
        return v;
    }
    // layout.setGravity(messageobj.left? Gravity.LEFT:Gravity.RIGHT);



}

//    public Bitmap decodeToBitMap(byte[] decodedByte){
//        return BitmapFactory.decodeByteArray(decodedByte,0,decodedByte.length);
//    }




