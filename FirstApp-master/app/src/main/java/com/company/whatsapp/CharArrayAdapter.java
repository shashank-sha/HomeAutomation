package com.company.whatsapp;

/**
 * Created by sudhanshu on 3/11/15.
 */

import android.content.Context;
import android.database.Cursor;
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

/**
 * Created by sudhanshu on 30/9/15.
 */



public class CharArrayAdapter extends ArrayAdapter<Message> {
    //private TextView charText;M
    private List<Message> messageList = new ArrayList<Message>();
    private LinearLayout layout;
   // private boolean isMe ;

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
//            DateFormat df = new SimpleDateFormat("HH:mm");
//            Calendar calobj = Calendar.getInstance();
//            // System.out.println(df.format(calobj.getTime()));
//            CharSequence text = df.format(calobj.getTime());
//            Context context =getContext();
//            int duration = Toast.LENGTH_LONG;
//            Toast toast = Toast.makeText(context,text,duration);
//            toast.show();



        }
       // isMe = !isMe;
//        DateFormat df = new SimpleDateFormat("HH:mm");
//        Calendar calobj = Calendar.getInstance();
//        // System.out.println(df.format(calobj.getTime()));
//        CharSequence text = df.format(calobj.getTime());


        layout = (LinearLayout) v.findViewById(R.id.message1);
        Message messageobj = getItem(position);

        TextView charText = (TextView) v.findViewById(R.id.SingleMessage);
        charText.setText(messageobj.toString());
       // DBAdapter dbAdapter =DBAdapter.getDatabaseHelper(getContext());


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
        }
            else {


            //charText.setGravity(Gravity.START);
            charText.setBackgroundResource(R.drawable.turq);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);

            params.gravity = Gravity.START;
            charText.setLayoutParams(params);

        }//  charText.setBackgroundColor(Color.WHITE);
//         else {
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




