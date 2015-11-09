package com.company.whatsapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudhanshu on 5/11/15.
 */
public class MessageListAdapter extends ArrayAdapter<Message> {



    public MessageListAdapter(Context context, int textViewResourceId, ArrayList<Message> items) {
        super(context, textViewResourceId, items);
    }






    private static class ViewHolder {
        private TextView itemView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.activity_chat_box, parent, false);


            viewHolder.itemView = (TextView) convertView.findViewById(R.id.lblMsgFrom);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Message item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            viewHolder.itemView.setText(String.format("%s %s", item.getMessage(), item.getFromName()));
        }

        return convertView;
    }
}
